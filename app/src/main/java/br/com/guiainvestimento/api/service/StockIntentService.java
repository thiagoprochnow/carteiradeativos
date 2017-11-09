package br.com.guiainvestimento.api.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.StockQuote;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */

public class StockIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = StockIntentService.class.getSimpleName();
    private final String BFToken = "42745199c91e49ff73706f997cd8f465";
    private final String CedroL = "thiprochnow";
    private final String CedroP = "102030";
    private boolean mSuccess = false;
    private String mSymbol;
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";

    /**
     * Constructor matching super is needed
     */
    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mHandler = new Handler();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Only calls the service if the symbol is present
        if (intent.hasExtra(ADD_SYMBOL)) {
            // try the Bolsa Financeira backup API
            int success = this.addStockTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
            if (success == GcmNetworkManager.RESULT_SUCCESS){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_stocks), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_stocks), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private int addStockTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(StockIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            OkHttpClient mClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cookieJar(new JavaNetCookieJar(CookieHandler.getDefault()))
                    .build();

            RequestBody body = RequestBody.create(null, new byte[]{});
            Request requestPost = new Request.Builder()
                    .url("http://webfeeder.cedrofinances.com.br/SignIn?login=thiprochnow&password=102030")
                    .post(body)
                    .build();

            Response responsePost = mClient.newCall(requestPost).execute();

            Log.d(LOG_TAG, "Post: " + responsePost.body().string());

            Request requestGet = new Request.Builder()
                    .url("http://webfeeder.cedrofinances.com.br/services/quotes/quote/petr4")
                    .build();

            Response responseGet = mClient.newCall(requestGet).execute();

            Log.d(LOG_TAG, "Get: " + responseGet.body().string());

            String[] symbols = params.getExtras().getString(StockIntentService.ADD_SYMBOL).split(",");


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }
/*
    // This is used whem the yahoo API fails
    // Since yahoo API is very inconsistent it is important to have a paid service backup fallback
    private int backupAddStockTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackupAPIStockService.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ContentValues stockDataCV = new ContentValues();
        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(StockIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            // Make the request and parse the result
            BackupAPIStockService service = retrofit.create(BackupAPIStockService.class);

            String[] symbols = params.getExtras().getString(StockIntentService.ADD_SYMBOL).split(",");
            for (String symbol: symbols) {
                Call<String> call;
                Response<String> response;
                String responseGetStock;

                call = service.getStockBackupAPI(BFToken, symbol);
                response = call.execute();
                responseGetStock = response.body();
                ContentValues updateStock = new ContentValues();
                if (response.isSuccessful() && responseGetStock != null && responseGetStock != "" && !responseGetStock.trim().isEmpty()) {
                    String[] arrayGetStock = responseGetStock.split(",");
                    // Prepare the data of the current price to update the StockData table
                    if (arrayGetStock.length > 10) {
                        stockDataCV.put(symbol, arrayGetStock[9]);
                        updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                        updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, arrayGetStock[10]);
                    } else {
                        updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                        updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);
                    }
                } else {
                    // symbol not updated automatically
                    updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);
                }

                String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                String[] updatedSelectionArguments = {symbol};
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.StockData.URI,
                        updateStock, updateSelection, updatedSelectionArguments);
            }

            if (stockDataCV.size() > 0){
                // Update value on stock data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.StockData.BULK_UPDATE_URI,
                        stockDataCV, null, null);
                resultStatus = GcmNetworkManager.RESULT_SUCCESS;
            } else {
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
            resultStatus = GcmNetworkManager.RESULT_FAILURE;
        }
        return resultStatus;
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sendBroadcast(new Intent(Constants.Receiver.STOCK));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));
    }
}