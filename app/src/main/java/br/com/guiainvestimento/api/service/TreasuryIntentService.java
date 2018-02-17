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

import java.io.IOException;

import br.com.guiainvestimento.domain.TreasuryQuote;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */

public class TreasuryIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = TreasuryIntentService.class.getSimpleName();
    private final String username = "mainuser";
    private final String password = "user1133";
    private String mResult = "";
    private String mSymbol;
    private String mType;
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String CONSULT_SYMBOL = "consult_symbol";

    /**
     * Constructor matching super is needed
     */
    public TreasuryIntentService() {
        super(TreasuryIntentService.class.getName());
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
                // try the Bolsa Financeira backup API
                int success = this.addTreasuryTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                if (success == GcmNetworkManager.RESULT_SUCCESS) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_treasury), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_treasury), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } /*else if (intent.hasExtra(CONSULT_SYMBOL)) {
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
            } */else {
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL, CONSULT_SYMBOL");
            }
        } catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int addTreasuryTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(TreasuryIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            OkHttpClient mClient = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build();

            // Build retrofit base request
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TreasuryService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            TreasuryService service = retrofit.create(TreasuryService.class);

            String[] symbols = params.getExtras().getString(TreasuryIntentService.ADD_SYMBOL).split(",");

            ContentValues treasuryDataCV = new ContentValues();

            for (String symbol: symbols){

                // Find id of symbol
                String[] treasuryTreasury = Constants.Symbols.TREASURY;
                int index = Arrays.asList(treasuryTreasury).indexOf(symbol);
                String treasuryId = "0";
                if (index >= 0){
                    String[] treasuryIds = Constants.Symbols.TREASURY_ID;
                    treasuryId = treasuryIds[index];
                }

                Call<TreasuryQuote> callGet = service.getTreasury(treasuryId);
                retrofit2.Response<TreasuryQuote> responseGet = callGet.execute();
                TreasuryQuote treasury = null;
                if (responseGet != null && responseGet.isSuccessful()) {
                    treasury = responseGet.body();
                }

                ContentValues updateTreasury = new ContentValues();

                if (responseGet != null && responseGet.isSuccessful() && treasury != null && treasury.getError() == null && treasury.toString().length() > 0){
                    // Success on request
                    if (treasury.getValor() != null){
                        treasuryDataCV.put(symbol, treasury.getValor());
                        updateTreasury.put(PortfolioContract.TreasuryData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                    } else {
                        updateTreasury.put(PortfolioContract.TreasuryData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    }
                } else {
                    // Mark symbol as failer and set NOT UPDATED on sql db
                    updateTreasury.put(PortfolioContract.TreasuryData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                }

                String updateSelection = PortfolioContract.TreasuryData.COLUMN_SYMBOL + " = ?";
                String[] updatedSelectionArguments = {symbol};
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.TreasuryData.URI,
                        updateTreasury, updateSelection, updatedSelectionArguments);
            }

            if (treasuryDataCV.size() > 0){
                // Update value on treasury data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.TreasuryData.BULK_UPDATE_URI,
                        treasuryDataCV, null, null);
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
/*
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
            retrofit2.Response<String> responsePost = callPost.execute();
            String responseString = responsePost.body();

            if (responseString.equals("true")){
                String symbol = params.getExtras().getString(TreasuryIntentService.CONSULT_SYMBOL);

                Call<StockQuote> callGet = service.getStock(symbol.toLowerCase());
                retrofit2.Response<StockQuote> responseGet = callGet.execute();
                StockQuote stock = null;
                if (responseGet != null && responseGet.isSuccessful()) {
                    stock = responseGet.body();
                }

                if (responseGet != null && responseGet.isSuccessful() && stock != null && stock.getError() == null && stock.toString().length() > 0){
                    // Success on request
                    if (stock.getLast() != null && stock.getError() == null){
                        result = stock.getLast();
                    }
                } else if(stock.getError() != null){
                    result = "error";
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mType == ADD_SYMBOL) {
            this.sendBroadcast(new Intent(Constants.Receiver.TREASURY));
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.TREASURY));
        } else if (mType == CONSULT_SYMBOL){
            Intent broadcastIntent = new Intent (Constants.Receiver.CONSULT_QUOTE); //put the same message as in the filter you used in the activity when registering the receiver
            broadcastIntent.putExtra("quote", mResult);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
    }

    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials)
                    .header("Accept", "applicaton/json")
                    .build();
            return chain.proceed(authenticatedRequest);
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