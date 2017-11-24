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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

            // Build retrofit base request
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(StockService.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            StockService service = retrofit.create(StockService.class);

            Call<String> callPost = service.getConnection(CedroL, CedroP);
            Response<String> responsePost = callPost.execute();
            String responseString = responsePost.body();

            ContentValues stockDataCV = new ContentValues();

            if (responseString.equals("true")){
                String[] symbols = params.getExtras().getString(StockIntentService.ADD_SYMBOL).split(",");

                for (String symbol: symbols){
                    Call<StockQuote> callGet = service.getStock(symbol.toLowerCase());
                    Response<StockQuote> responseGet = callGet.execute();
                    StockQuote stock = null;
                    if (responseGet != null && responseGet.isSuccessful()) {
                        stock = responseGet.body();
                    }

                    ContentValues updateStock = new ContentValues();

                    if (responseGet != null && responseGet.isSuccessful() && stock != null && stock.getError() == null){
                        // Success on request
                        if (stock.getLast() != null){
                            stockDataCV.put(symbol, stock.getLast());
                            updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                            updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, stock.getPrevious());
                        } else {
                            updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                            updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);
                        }
                    } else {
                        // Mark symbol as failer and set NOT UPDATED on sql db
                        updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                        updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);
                    }

                    String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {symbol};
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.StockData.URI,
                            updateStock, updateSelection, updatedSelectionArguments);
                }
            } else {
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
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
        }
        return resultStatus;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sendBroadcast(new Intent(Constants.Receiver.STOCK));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));
    }
}