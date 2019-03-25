package br.com.guiainvestimento.api.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.guiainvestimento.R;

import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.FiiQuote;
import br.com.guiainvestimento.domain.StockQuote;
import br.com.guiainvestimento.receiver.FiiReceiver;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */

public class FiiIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = FiiIntentService.class.getSimpleName();
    private final String apiKey = "XT5MYEQ27BZ6LXYC";
    private final String function = "GLOBAL_QUOTE";
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String PREMIUM = "premium";

    /**
     * Constructor matching super is needed
     */
    public FiiIntentService() {
        super(FiiIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Only calls the service if the symbol is present
        if (intent.hasExtra(ADD_SYMBOL)) {
            int count = 0;
            int success = -1;
            TaskParams params = new TaskParams(ADD_SYMBOL, intent.getExtras());
            do {
                success = this.addFiiTask(params);
                count++;
            }
            while (success != GcmNetworkManager.RESULT_SUCCESS && success != GcmNetworkManager.RESULT_RESCHEDULE && count <= 3);
            if (success == GcmNetworkManager.RESULT_SUCCESS) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_fii), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (success == GcmNetworkManager.RESULT_RESCHEDULE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fii_limit), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fii), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private int addFiiTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        boolean isPremium = params.getExtras().getBoolean(FiiIntentService.PREMIUM, true);
        int limit = 0;
        int size = 0;
        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(FiiIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            OkHttpClient mClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cookieJar(new JavaNetCookieJar(CookieHandler.getDefault()))
                    .build();

            // Build retrofit base request
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FiiService.BASE_URL)
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            FiiService service = retrofit.create(FiiService.class);

            ContentValues fiiDataCV = new ContentValues();

            String[] symbols = params.getExtras().getString(FiiIntentService.ADD_SYMBOL).split(",");

            List<FiiQuote> fiis = new ArrayList<FiiQuote>();

            for (String symbol : symbols) {
                if (limit <= 3) {
                    boolean success = false;
                    Call<String> callGet = service.getFii(function, symbol + ".SA", apiKey);
                    Response<String> responseGet = callGet.execute();
                    if (responseGet != null && responseGet.isSuccessful()) {
                        FiiQuote fii = new FiiQuote();
                        String responseStock = responseGet.body();
                        try {
                            JSONObject jsonObj = new JSONObject(responseStock);
                            JSONObject resultObj = jsonObj.getJSONObject("Global Quote");
                            fii.setmSymbol(symbol);
                            fii.setmLast(resultObj.getString("05. price"));
                            fii.setmOpen(resultObj.getString("02. open"));
                            fii.setmPrevious(resultObj.getString("08. previous close"));
                            fiis.add(fii);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error in json " + e.getMessage());
                            ContentValues updateStock = new ContentValues();
                            updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                            updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);

                            String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                            String[] updatedSelectionArguments = {symbol};
                            int updatedRows = this.getContentResolver().update(
                                    PortfolioContract.StockData.URI,
                                    updateStock, updateSelection, updatedSelectionArguments);
                            e.printStackTrace();
                        }
                    } else {
                        ContentValues updateStock = new ContentValues();
                        updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                        updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);

                        String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                        String[] updatedSelectionArguments = {symbol};
                        int updatedRows = this.getContentResolver().update(
                                PortfolioContract.StockData.URI,
                                updateStock, updateSelection, updatedSelectionArguments);
                        resultStatus = GcmNetworkManager.RESULT_FAILURE;
                    }
                } else {
                    // Limit reach, change Not Updated status
                    ContentValues updateFii = new ContentValues();
                    updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    updateFii.put(PortfolioContract.FiiData.COLUMN_CLOSING_PRICE, 0);

                    String updateSelection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {symbol};
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.FiiData.URI,
                            updateFii, updateSelection, updatedSelectionArguments);
                }

                if (!isPremium) {
                    limit++;
                }
            }
            // Remove the one that was added first in case it was first element
            limit--;

            if (fiis != null && fiis.size() > 0) {
                for (String symbol : symbols) {
                    boolean success = false;
                    ContentValues updateFii = new ContentValues();
                    for (FiiQuote fii : fiis) {
                        if (fii != null && fii.toString() != null && fii.toString().length() > 0 && symbol.equalsIgnoreCase(fii.getSymbol()) && fii.getLast() != null) {
                            String lastPrice = "0.0";
                            if (Double.valueOf(fii.getLast()) > 0) {
                                lastPrice = fii.getLast();
                            } else {
                                lastPrice = fii.getPrevious();
                            }

                            // Success on request
                            fiiDataCV.put(symbol, lastPrice);
                            updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                            updateFii.put(PortfolioContract.FiiData.COLUMN_CLOSING_PRICE, fii.getPrevious());
                            success = true;
                        }
                    }

                    if (!success) {
                        // Mark symbol as failer and set NOT UPDATED on sql db
                        updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                        updateFii.put(PortfolioContract.FiiData.COLUMN_CLOSING_PRICE, 0);
                    }

                    String updateSelection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {symbol};
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.FiiData.URI,
                            updateFii, updateSelection, updatedSelectionArguments);
                }
            } else {
                for (String symbol : symbols) {
                    ContentValues updateFii = new ContentValues();
                    // Mark symbol as failer and set NOT UPDATED on sql db
                    updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    updateFii.put(PortfolioContract.FiiData.COLUMN_CLOSING_PRICE, 0);

                    String updateSelection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {symbol};
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.FiiData.URI,
                            updateFii, updateSelection, updatedSelectionArguments);
                }
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }

            if (fiiDataCV.size() > 0) {
                // Update value on fii data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.FiiData.BULK_UPDATE_URI,
                        fiiDataCV, null, null);
                if (limit <= 3) {
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    resultStatus = GcmNetworkManager.RESULT_RESCHEDULE;
                }
            } else {
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            this.addFiiTask(params);
            e.printStackTrace();
        }
        return resultStatus;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FiiReceiver fiiReceiver = new FiiReceiver(this);
        fiiReceiver.updateFiiPortfolio();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FII));

        PortfolioReceiver portfolioReceiver = new PortfolioReceiver(this);
        portfolioReceiver.updatePortfolio();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
    }

    public class NullOnEmptyConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) return null;
                    return delegate.convert(body);
                }
            };
        }
    }
}