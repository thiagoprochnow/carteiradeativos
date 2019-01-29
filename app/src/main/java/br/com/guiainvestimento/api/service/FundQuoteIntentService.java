package br.com.guiainvestimento.api.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Fund;
import br.com.guiainvestimento.domain.FundQuote;
import br.com.guiainvestimento.domain.Income;
import br.com.guiainvestimento.receiver.FundReceiver;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */

public class FundQuoteIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = FundQuoteIntentService.class.getSimpleName();
    private final String username = "mainuser";
    private final String password = "user1133";
    private String mResult = "";
    private String mCnpj;
    private String mType;
    Handler mHandler;

    // Extras
    public static final String ADD_CNPJ = "cnpj";
    public static final String PREMIUM = "premium";

    /**
     * Constructor matching super is needed
     */
    public FundQuoteIntentService() {
        super(FundQuoteIntentService.class.getName());
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
            if (intent.hasExtra(ADD_CNPJ)) {
                mType = ADD_CNPJ;
                // try the Bolsa Financeira backup API
                int success = this.addFundQuoteTask(new TaskParams(ADD_CNPJ, intent.getExtras()));
                if (success == GcmNetworkManager.RESULT_SUCCESS) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_fund), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fund), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                throw new IOException("Missing one of the following Extras: ADD_CNPJ, CONSULT_SYMBOL");
            }
        } catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int addFundQuoteTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        boolean isPremium = params.getExtras().getBoolean(FundQuoteIntentService.PREMIUM, true);
        int limit = 0;
        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(FundQuoteIntentService.ADD_CNPJ).isEmpty()) {
                throw new IOException("Missing Extra ADD_CNPJ");
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            OkHttpClient mClient = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build();

            // Build retrofit base request
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FundQuoteService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            FundQuoteService service = retrofit.create(FundQuoteService.class);

            String[] cnpjs = params.getExtras().getString(FundQuoteIntentService.ADD_CNPJ).split(",");
            resultStatus = GcmNetworkManager.RESULT_SUCCESS;

            for (String cnpj: cnpjs){
                long timestamp = Long.valueOf(getLastFundQuoteTime(cnpj));
                Date date = new Date(timestamp);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String data  = dateFormat.format(date);

                String selection = PortfolioContract.FundData.COLUMN_CNPJ + " = ?";
                String[] selectionArguments = {cnpj};

                // Update Stock FundQuote table
                Call<List<FundQuote>> callGet = service.getFundQuotes(cnpj.toUpperCase(),data);
                retrofit2.Response<List<FundQuote>> responseGet = callGet.execute();
                List<FundQuote> fundQuotes = null;
                if (responseGet != null && responseGet.isSuccessful()) {
                    fundQuotes = responseGet.body();
                }

                if(fundQuotes != null && responseGet != null && responseGet.isSuccessful()) {
                    for (FundQuote fundQuote : fundQuotes) {
                        if (fundQuote != null) {
                            String quoteData = fundQuote.getData();
                            long quoteTimestamp = 0;
                            try {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                                Date parsedDate = dateFormat.parse(quoteData);
                                quoteTimestamp = parsedDate.getTime();
                            } catch(Exception e) { //this generic but you can control another types of exception
                                // look the origin of excption
                            }

                            ContentValues fundQuoteCV = new ContentValues();
                            fundQuoteCV.put(PortfolioContract.FundQuotes.COLUMN_CNPJ,fundQuote.getCnpj());
                            fundQuoteCV.put(PortfolioContract.FundQuotes.COLUMN_TIMESTAMP,quoteTimestamp);
                            fundQuoteCV.put(PortfolioContract.FundQuotes.COLUMN_QUOTES,fundQuote.getQuote().replace(".",""));
                            // TODO: Calculate the percent based on total stocks value that received the income
                            // Adds to the database
                            Uri insertedUri = this.getContentResolver().insert(PortfolioContract.FundQuotes.URI,
                                    fundQuoteCV);
                        }
                    }
                    List<Double> updatedFund = getFundData(cnpj);
                    double currentTotal = updatedFund.get(0);
                    double boughtTotal = updatedFund.get(1);
                    double gainTotal = updatedFund.get(2);

                    // Update Total

                    ContentValues updateCV = new ContentValues();

                    updateCV.put(PortfolioContract.FundData.COLUMN_CURRENT_TOTAL, currentTotal);
                    updateCV.put(PortfolioContract.FundData.COLUMN_BUY_VALUE_TOTAL, boughtTotal);
                    updateCV.put(PortfolioContract.FundData.COLUMN_NET_GAIN, gainTotal);
                    updateCV.put(PortfolioContract.FundData.COLUMN_TOTAL_GAIN, gainTotal);
                    updateCV.put(PortfolioContract.FundData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);

                    int updateQueryCursor = this.getContentResolver().update(
                            PortfolioContract.FundData.URI,
                            updateCV, selection, selectionArguments);
                } else {
                    resultStatus = GcmNetworkManager.RESULT_FAILURE;

                    ContentValues updateCV = new ContentValues();
                    updateCV.put(PortfolioContract.FundData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);

                    int updateQueryCursor = this.getContentResolver().update(
                            PortfolioContract.FundData.URI,
                            updateCV, selection, selectionArguments);
                }
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }

    // Changed so it will always get all incomes and check if they were already inserted.
    private String getLastFundQuoteTime(String cnpj){
        // Stock Income
        String[] affectedColumn = {PortfolioContract.FundQuotes.COLUMN_TIMESTAMP};
        String sortOrder = PortfolioContract.FundQuotes.COLUMN_TIMESTAMP + " DESC LIMIT 1";
        String selection = PortfolioContract.FundQuotes.COLUMN_CNPJ + " = ?";
        String[] selectionArguments = {cnpj};

        Cursor cursor = getApplicationContext().getContentResolver().query(
                PortfolioContract.FundQuotes.URI,
                affectedColumn, selection, selectionArguments, sortOrder);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            //long timestamp = Long.parseLong(cursor.getString(0));
            return cursor.getString(0);
        }
        return "1262131200";
    }

    // Changed so it will always get all incomes and check if they were already inserted.
    private String getLatestFundQuote(String cnpj){
        // Fund
        String[] affectedColumn = {PortfolioContract.FundQuotes.COLUMN_QUOTES};
        String sortOrder = PortfolioContract.FundQuotes.COLUMN_TIMESTAMP + " DESC LIMIT 1";
        String selection = PortfolioContract.FundQuotes.COLUMN_CNPJ + " = ?";
        String[] selectionArguments = {cnpj};

        Cursor cursor = getApplicationContext().getContentResolver().query(
                PortfolioContract.FundQuotes.URI,
                affectedColumn, selection, selectionArguments, sortOrder);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            //long timestamp = Long.parseLong(cursor.getString(0));
            return cursor.getString(0);
        }
        return "0.00";
    }

    // Changed so it will always get all incomes and check if they were already inserted.
    private String getFundQuote(String cnpj, long timestamp){
        // Fund
        String[] affectedColumn = {PortfolioContract.FundQuotes.COLUMN_QUOTES};
        String sortOrder = PortfolioContract.FundQuotes.COLUMN_TIMESTAMP + " ASC LIMIT 1";
        String selection = PortfolioContract.FundQuotes.COLUMN_CNPJ + " = ? AND " + PortfolioContract.FundQuotes.COLUMN_TIMESTAMP + " >= ? ";
        String[] selectionArguments = {cnpj,String.valueOf(timestamp)};

        Cursor cursor = getApplicationContext().getContentResolver().query(
                PortfolioContract.FundQuotes.URI,
                affectedColumn, selection, selectionArguments, sortOrder);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            //long timestamp = Long.parseLong(cursor.getString(0));
            return cursor.getString(0);
        }
        return "0.00";
    }

    // Update each Fund Transcation with current total by using the FundQuote tables to get gain rate from bought date to latest date
    private List<Double> getFundData(String cnpj){
        String latestQuote = getLatestFundQuote(cnpj);
        double currentTotal = 0.0;
        double boughtTotal = 0.0;
        double gainTotal = 0.0;
        Cursor fundTransaction = getFundTransactions(cnpj);
        // Go through every transaction to get total current value
        if(fundTransaction.getCount() > 0){
            fundTransaction.moveToFirst();
            do{
                long boughtTimestamp = fundTransaction.getLong(fundTransaction.getColumnIndex(PortfolioContract.FundTransaction.COLUMN_TIMESTAMP));
                double bought = fundTransaction.getDouble(fundTransaction.getColumnIndex(PortfolioContract.FundTransaction.COLUMN_TOTAL));
                String fundQuote = getFundQuote(cnpj,boughtTimestamp);
                if(fundQuote == "0.00"){
                    fundQuote = latestQuote;
                }
                double gainRate = (double) Double.valueOf(latestQuote)/Double.valueOf(fundQuote);
                currentTotal += bought*gainRate;
                boughtTotal += bought;
            } while (fundTransaction.moveToNext());
        }

        gainTotal = currentTotal - boughtTotal;
        List<Double> fund = Arrays.asList(currentTotal,boughtTotal,gainTotal);

        return fund;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mType == ADD_CNPJ) {
            FundReceiver fundReceiver = new FundReceiver(this);
            fundReceiver.updateFundPortfolio();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FUND));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(this);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
        }
    }

    private Cursor getFundTransactions(String cnpj){
        String selectionData = PortfolioContract.FundTransaction.COLUMN_CNPJ + " = ? ";
        String[] selectionDataArguments = {cnpj};
        Cursor cursor = this.getContentResolver().query(
                PortfolioContract.FundTransaction.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
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