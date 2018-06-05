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
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Income;
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

public class FiiIncomeIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = FiiIncomeIntentService.class.getSimpleName();
    private final String username = "mainuser";
    private final String password = "user1133";
    private String mResult = "";
    private String mSymbol;
    private String mType;
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String PREMIUM = "premium";

    /**
     * Constructor matching super is needed
     */
    public FiiIncomeIntentService() {
        super(FiiIncomeIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mHandler = new Handler();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            boolean isPremium = intent.getExtras().getBoolean(FiiIntentService.PREMIUM, true);
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL)) {
                mType = ADD_SYMBOL;
                if (isPremium) {
                    int success = this.addIncomeTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                    if (success == GcmNetworkManager.RESULT_SUCCESS) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_fii_income), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fii_income), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    mHandler.post(new Runnable() {
                        // Show not premium message
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fii_income_premium), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                Intent mFiiServiceIntent = new Intent(this, FiiIntentService
                        .class);

                // Fii quotes update
                mFiiServiceIntent.putExtra(FiiIntentService.ADD_SYMBOL, intent.getStringExtra(ADD_SYMBOL));
                mFiiServiceIntent.putExtra(FiiIntentService.PREMIUM, isPremium);
                startService(mFiiServiceIntent);
            } else {
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL");
            }
        } catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int addIncomeTask(TaskParams params) {
        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(FiiIncomeIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            OkHttpClient mClient = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build();

            // Build retrofit base request
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(IncomeService.BASE_URL)
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            IncomeService service = retrofit.create(IncomeService.class);

            String[] symbols = params.getExtras().getString(FiiIntentService.ADD_SYMBOL).split(",");

            for (String symbol: symbols) {
                String timestamp = getLastIncomeTime(symbol);

                // Update Fii Income table
                Call<List<Income>> callGet = service.getProvento(symbol.toUpperCase(),timestamp);
                retrofit2.Response<List<Income>> responseGet = callGet.execute();
                List<Income> proventos = null;
                if (responseGet != null && responseGet.isSuccessful()) {
                    proventos = responseGet.body();
                }

                if(proventos != null && responseGet != null && responseGet.isSuccessful()) {
                    Cursor insertedIncomes = getInsertedIncomes(symbol);
                    for (Income provento : proventos) {
                        if (provento != null) {
                            boolean isInserted = isIncomeInserted(provento, insertedIncomes);
                            // Check if this income was already inserted and will not generate a duplicate
                            if(!isInserted) {
                                double tax = 0;

                                String type = provento.getTipo();
                                int tipo = Constants.IncomeType.INVALID;
                                if (type.equalsIgnoreCase("FII")) {
                                    tipo = Constants.IncomeType.FII;
                                }

                                if (tipo != Constants.IncomeType.INVALID) {
                                    double perFii = Double.valueOf(provento.getValor());
                                    long exdividend = Long.parseLong(provento.getTimestamp()) * 1000;
                                    double affected = getFiiQuantity(symbol, exdividend);

                                    double receiveValue = affected * perFii;
                                    double liquidValue = receiveValue;
                                    double brokerage = 0;

                                    ContentValues incomeCV = new ContentValues();
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_SYMBOL, symbol);
                                    // TODO: Type is hardcoded
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_TYPE, tipo);
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_PER_FII, perFii);
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP, exdividend);
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_AFFECTED_QUANTITY, affected);
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_TAX, tax);
                                    incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID, liquidValue);
                                    // Adds to the database
                                    Uri insertedUri = this.getContentResolver().insert(PortfolioContract.FiiIncome.URI,
                                            incomeCV);
                                    // If error occurs to add, shows error message
                                    if (insertedUri != null) {
                                        boolean updateFiiDataIncome = updateFiiDataIncome(symbol, liquidValue, tax);
                                    }
                                }
                            }
                        }
                    }
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    resultStatus = GcmNetworkManager.RESULT_FAILURE;
                }
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
        this.sendBroadcast(new Intent(Constants.Receiver.FII));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FII));
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

    // Changed so it will always get all incomes and check if they were already inserted.
    private String getLastIncomeTime(String symbol){
        // Fii Income
        String[] affectedColumn = {PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP};
        String sortOrder = PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " DESC";
        String selection = PortfolioContract.FiiIncome.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor cursor = getApplicationContext().getContentResolver().query(
                PortfolioContract.FiiIncome.URI,
                affectedColumn, selection, selectionArguments, sortOrder);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            long timestamp = Long.parseLong(cursor.getString(0))/1000 - 1;
            return String.valueOf(timestamp);
        }
        return "1262131200";
    }

    // Get fii quantity that will receive the dividend per fii
    // symbol is to query by specific symbol only
    // income timestamp is to query only the quantity of fiis transactions before the timestamp
    public double getFiiQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of fii
        String selection = PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(incomeTimestamp)};
        String sortOrder = PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " ASC";

        // Check if the symbol exists in the db
        Cursor queryCursor = this.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI,
                null, selection, selectionArguments, sortOrder);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            double quantityTotal = 0;
            int currentType = 0;
            do {
                currentType = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        quantityTotal = quantityTotal*queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        quantityTotal = quantityTotal/queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (queryCursor.moveToNext());
            return quantityTotal;
        } else{
            return 0;
        }
    }

    // Update Total Income on Fii Data by new income added
    private boolean updateFiiDataIncome(String symbol, double valueReceived, double tax){
        // Prepare query to update fii data income
        // and the total income received
        String selection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = this.getContentResolver().query(
                PortfolioContract.FiiData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_INCOME));
            double totalIncome = dbIncome + valueReceived;
            double totalTax = 0;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.FiiData.COLUMN_INCOME, totalIncome);
            updateCV.put(PortfolioContract.FiiData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = this.getContentResolver().update(
                    PortfolioContract.FiiData.URI,
                    updateCV, selection, selectionArguments);
        }
        return false;
    }

    private Cursor getInsertedIncomes(String symbol){
        String selectionData = PortfolioContract.FiiIncome.COLUMN_SYMBOL + " = ? ";
        String[] selectionDataArguments = {symbol};
        Cursor cursor = this.getContentResolver().query(
                PortfolioContract.FiiIncome.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    private boolean isIncomeInserted(Income provento, Cursor insertedIncomes){
        double value = Double.valueOf(provento.getValor());
        long timestamp = Long.parseLong(provento.getTimestamp())*1000;
        String type = provento.getTipo();
        int tipo = Constants.IncomeType.INVALID;
        if (type.equalsIgnoreCase("FII")) {
            tipo = Constants.IncomeType.FII;
        }

        if (insertedIncomes.getCount() > 0){
            insertedIncomes.moveToFirst();
            do{
                double insertedValue = insertedIncomes.getDouble(insertedIncomes.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_PER_FII));
                long insertedTimestamp = insertedIncomes.getLong(insertedIncomes.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                int insertedTipo = insertedIncomes.getInt(insertedIncomes.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE));
                if (value == insertedValue && timestamp == insertedTimestamp && tipo == insertedTipo){
                    return true;
                }
            } while (insertedIncomes.moveToNext());

            // If provento was not found as inserted already, return false to be inserted
            return false;
        } else {
            // No inserted incomes yet, safe to insert
            return false;
        }
    }
}