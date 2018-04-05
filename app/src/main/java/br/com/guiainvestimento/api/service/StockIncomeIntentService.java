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
import br.com.guiainvestimento.fragment.BaseFragment;
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

public class StockIncomeIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = StockIncomeIntentService.class.getSimpleName();
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
    public StockIncomeIntentService() {
        super(StockIncomeIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mHandler = new Handler();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            boolean isPremium = intent.getExtras().getBoolean(StockIntentService.PREMIUM, true);
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL)) {
                mType = ADD_SYMBOL;
                if (isPremium) {
                    int success = this.addIncomeTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                    if (success == GcmNetworkManager.RESULT_SUCCESS) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_income), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_income), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    mHandler.post(new Runnable() {
                        // Show not premium message
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_income_premium), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                Intent mStockServiceIntent = new Intent(this, StockIntentService
                        .class);

                // Stock quotes update
                mStockServiceIntent.putExtra(StockIntentService.ADD_SYMBOL, intent.getStringExtra(ADD_SYMBOL));
                mStockServiceIntent.putExtra(StockIntentService.PREMIUM, isPremium);
                startService(mStockServiceIntent);
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
            if (params.getExtras() == null || params.getExtras().getString(StockIncomeIntentService.ADD_SYMBOL).isEmpty()) {
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

            String[] symbols = params.getExtras().getString(StockIntentService.ADD_SYMBOL).split(",");

            for (String symbol: symbols) {
                String timestamp = getLastIncomeTime(symbol);

                // Update Stock Income table
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
                            if(!isInserted){
                                double tax = 0;

                                String type = provento.getTipo();
                                int tipo = Constants.IncomeType.INVALID;
                                if (type.equalsIgnoreCase("DIV")) {
                                    tipo = Constants.IncomeType.DIVIDEND;
                                } else if (type.equalsIgnoreCase("JCP")) {
                                    tipo = Constants.IncomeType.JCP;
                                }

                                if (tipo != Constants.IncomeType.INVALID) {
                                    double perStock = Double.valueOf(provento.getValor());
                                    long exdividend = Long.parseLong(provento.getTimestamp()) * 1000;
                                    int affected = getStockQuantity(symbol, exdividend);

                                    double receiveValue = affected * perStock;
                                    double liquidValue = receiveValue;
                                    double brokerage = 0;

                                    // If it is JCP, needs to calculate the tax and liquid value to be received
                                    if (tipo == Constants.IncomeType.JCP) {
                                        tax = calculateTax(receiveValue);
                                        liquidValue = receiveValue - tax;
                                    }

                                    ContentValues incomeCV = new ContentValues();
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_SYMBOL, symbol);
                                    // TODO: Type is hardcoded
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_TYPE, tipo);
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_PER_STOCK, perStock);
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP, exdividend);
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, affected);
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_TAX, tax);
                                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, liquidValue);
                                    // TODO: Calculate the percent based on total stocks value that received the income
                                    // Adds to the database
                                    Uri insertedUri = this.getContentResolver().insert(PortfolioContract.StockIncome.URI,
                                            incomeCV);
                                    // If error occurs to add, shows error message
                                    if (insertedUri != null) {
                                        boolean updateStockDataIncome = updateStockDataIncome(symbol, liquidValue, tax);
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
        this.sendBroadcast(new Intent(Constants.Receiver.STOCK));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));
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
        // Stock Income
        /*
        String[] affectedColumn = {PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP};
        String sortOrder = PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " DESC";
        String selection = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor cursor = getApplicationContext().getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                affectedColumn, selection, selectionArguments, sortOrder);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            long timestamp = Long.parseLong(cursor.getString(0))/1000;
            return String.valueOf(timestamp);
        }
        */
        return "1262131200";
    }

    private double calculateTax(double receiveValue){
        // Tax for JCP is of 15% of total receive value
        return receiveValue*0.15;
    }

    // Get stock quantity that will receive the dividend per stock
    // symbol is to query by specific symbol only
    // income timestamp is to query only the quantity of stocks transactions before the timestamp
    public int getStockQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of stock
        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(incomeTimestamp)};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        // Check if the symbol exists in the db
        Cursor queryCursor = this.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            int quantityTotal = 0;
            int currentType = 0;
            do {
                currentType = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.BONIFICATION:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        quantityTotal = quantityTotal*queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        quantityTotal = quantityTotal/queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (queryCursor.moveToNext());
            return quantityTotal;
        } else{
            return 0;
        }
    }

    // Update Total Income on Stock Data by new income added
    private boolean updateStockDataIncome(String symbol, double valueReceived, double tax){
        // Prepare query to update stock data income
        // and the total income received
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = this.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_NET_INCOME));
            double dbTax = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_INCOME_TAX));
            double totalIncome = dbIncome + valueReceived;
            double totalTax = dbTax + tax;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.StockData.COLUMN_NET_INCOME, totalIncome);
            updateCV.put(PortfolioContract.StockData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = this.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    updateCV, selection, selectionArguments);
        }
        return false;
    }

    private Cursor getInsertedIncomes(String symbol){
        String selectionData = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ? ";
        String[] selectionDataArguments = {symbol};
        Cursor cursor = this.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    private boolean isIncomeInserted(Income provento, Cursor insertedIncomes){
        double value = Double.valueOf(provento.getValor());
        long timestamp = Long.parseLong(provento.getTimestamp())*1000;
        String type = provento.getTipo();
        int tipo = Constants.IncomeType.INVALID;
        if (type.equalsIgnoreCase("DIV")) {
            tipo = Constants.IncomeType.DIVIDEND;
        } else if (type.equalsIgnoreCase("JCP")) {
            tipo = Constants.IncomeType.JCP;
        }

        if (insertedIncomes.getCount() > 0){
            insertedIncomes.moveToFirst();
            do{
                double insertedValue = insertedIncomes.getDouble(insertedIncomes.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK));
                long insertedTimestamp = insertedIncomes.getLong(insertedIncomes.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                int insertedTipo = insertedIncomes.getInt(insertedIncomes.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE));
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