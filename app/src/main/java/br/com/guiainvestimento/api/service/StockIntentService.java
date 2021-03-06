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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.activity.MainActivity;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.StockQuote;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import br.com.guiainvestimento.receiver.StockReceiver;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
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
    private final String CedroL = "thiprochnow";
    private final String CedroP = "4schGOS";
    private boolean mSuccess = false;
    private String mResult = "";
    private String mSymbol;
    private String mType;
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String PREMIUM = "premium";
    public static final String CONSULT_SYMBOL = "consult_symbol";

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

        try {
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL)) {
                mType = ADD_SYMBOL;
                int count = 0;
                int success = -1;
                TaskParams params = new TaskParams(ADD_SYMBOL, intent.getExtras());
                do {
                    success = this.addStockTask(params);
                    count++;
                } while (success != GcmNetworkManager.RESULT_SUCCESS && success != GcmNetworkManager.RESULT_RESCHEDULE && count <= 3);
                if (success == GcmNetworkManager.RESULT_SUCCESS) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_stocks), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (success == GcmNetworkManager.RESULT_RESCHEDULE){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_stocks_limit), Toast.LENGTH_LONG).show();
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
            } else if (intent.hasExtra(CONSULT_SYMBOL)) {
                mType = CONSULT_SYMBOL;
                mResult = this.consultStockTask(new TaskParams(CONSULT_SYMBOL, intent.getExtras()));
                if (mResult == "") {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_consult_quote), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL, CONSULT_SYMBOL");
            }
        } catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int addStockTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;
        boolean isPremium = params.getExtras().getBoolean(StockIntentService.PREMIUM, true);
        int limit = 0;
        int size = 0;
        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(StockIntentService.ADD_SYMBOL).isEmpty()) {
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
                    .baseUrl(StockService.BASE_URL)
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            StockService service = retrofit.create(StockService.class);

            Call<String> callPost = service.getConnection(CedroL, CedroP);
            Response<String> responsePost = callPost.execute();
            String responseString = responsePost.body();

            ContentValues stockDataCV = new ContentValues();

            String[] symbols = params.getExtras().getString(StockIntentService.ADD_SYMBOL).split(",");

            String path = "";
            if (responseString != null && responseString.equals("true")){
                for (String symbol: symbols){
                    if (limit <= 5) {
                        if(limit == 0){
                            path = symbol;
                            limit++;
                            size++;
                        } else {
                            path += "/"+symbol;
                            size++;
                        }
                    } else {
                        // Limit reach, change Not Updated status
                        ContentValues updateStock = new ContentValues();
                        updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                        updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);

                        String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                        String[] updatedSelectionArguments = {symbol};
                        int updatedRows = this.getContentResolver().update(
                                PortfolioContract.StockData.URI,
                                updateStock, updateSelection, updatedSelectionArguments);
                    }

                    if (!isPremium) {
                        limit++;
                    }
                }
                // Remove the one that was added first in case it was first element
                limit--;
            } else {

                for (String symbol: symbols) {
                    ContentValues updateStock = new ContentValues();
                    // Mark symbol as failer and set NOT UPDATED on sql db
                    updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);

                    String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {symbol};
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.StockData.URI,
                            updateStock, updateSelection, updatedSelectionArguments);
                }

                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }
            path = path.toLowerCase();
            List<StockQuote> stocks = null;
            if(size <= 1){
                boolean success = false;
                Call<StockQuote> callGet = service.getStock(path);
                Response<StockQuote> responseGet = callGet.execute();
                if (responseGet != null && responseGet.isSuccessful()) {
                    success = true;
                    StockQuote stock = responseGet.body();
                    stocks = new ArrayList<StockQuote>();
                    stocks.add(stock);
                } else {
                    stocks = null;
                }
            } else {
                boolean success = false;
                do {
                    Call<List<StockQuote>> callGet = service.getStocks(path);
                    Response<List<StockQuote>> responseGet = callGet.execute();
                    if (responseGet != null && responseGet.isSuccessful()) {
                        stocks = responseGet.body();
                        success = true;
                    } else {
                        stocks = null;
                    }
                } while (!success);
            }

            if (stocks != null && stocks.size() > 0) {
                for (String symbol: symbols) {
                    boolean success = false;
                    ContentValues updateStock = new ContentValues();
                    for (StockQuote stock : stocks) {
                        if (stock != null && stock.toString().length() > 0 && symbol.equalsIgnoreCase(stock.getSymbol()) && stock.getLast() != null) {
                            String lastPrice = "0.0";
                            if(Double.valueOf(stock.getLast()) > 0){
                                lastPrice = stock.getLast();
                            } else {
                                lastPrice = stock.getPrevious();
                            }

                            // Success on request
                            stockDataCV.put(symbol, lastPrice);
                            updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                            updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, stock.getPrevious());
                            success = true;
                        }
                    }

                    if(!success){
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

                for (String symbol: symbols) {
                    ContentValues updateStock = new ContentValues();
                    // Mark symbol as failer and set NOT UPDATED on sql db
                    updateStock.put(PortfolioContract.StockData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    updateStock.put(PortfolioContract.StockData.COLUMN_CLOSING_PRICE, 0);

                    String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {symbol};
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.StockData.URI,
                            updateStock, updateSelection, updatedSelectionArguments);
                }
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }

            if (stockDataCV.size() > 0){
                // Update value on stock data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.StockData.BULK_UPDATE_URI,
                        stockDataCV, null, null);
                if (limit <= 5) {
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    resultStatus = GcmNetworkManager.RESULT_RESCHEDULE;
                }
            } else {
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }

    private String consultStockTask(TaskParams params) {

        String result = "";

        try {

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
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            StockService service = retrofit.create(StockService.class);

            Call<String> callPost = service.getConnection(CedroL, CedroP);
            Response<String> responsePost = callPost.execute();
            String responseString = responsePost.body();

            if (responseString.equals("true")){
                String symbol = params.getExtras().getString(StockIntentService.CONSULT_SYMBOL);

                Call<StockQuote> callGet = service.getStock(symbol.toLowerCase());
                Response<StockQuote> responseGet = callGet.execute();
                StockQuote stock = null;
                if (responseGet != null && responseGet.isSuccessful()) {
                    stock = responseGet.body();
                }

                if (responseGet != null && responseGet.isSuccessful() && stock != null && stock.toString().length() > 0){
                    // Success on request
                    if (stock.getLast() != null){
                        result = stock.getLast();
                    }
                } else{
                    result = "error";
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mType == ADD_SYMBOL) {
            super.onDestroy();
            StockReceiver stockReceiver = new StockReceiver(this);
            stockReceiver.updateStockPortfolio();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(this);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
        } else if (mType == CONSULT_SYMBOL){
            Intent broadcastIntent = new Intent (Constants.Receiver.CONSULT_QUOTE); //put the same message as in the filter you used in the activity when registering the receiver
            broadcastIntent.putExtra("quote", mResult);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
    }

    public class NullOnEmptyConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) return null;
                    return delegate.convert(body);                }
            };
        }
    }
}