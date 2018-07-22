package br.com.guiainvestimento.api.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Cdi;
import br.com.guiainvestimento.domain.Ipca;
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

public class FixedIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = FixedIntentService.class.getSimpleName();
    private final String username = "mainuser";
    private final String password = "user1133";
    private String mResult = "";
    private String mSymbol;
    private String mType;
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";

    /**
     * Constructor matching super is needed
     */
    public FixedIntentService() {
        super(FixedIntentService.class.getName());
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
                int success = this.addCdiTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                if (success == GcmNetworkManager.RESULT_SUCCESS) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_fixed), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (success == GcmNetworkManager.RESULT_RESCHEDULE){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fixed_rate), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fixed), Toast.LENGTH_SHORT).show();
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

    private int addCdiTask(TaskParams params) {
        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(FixedIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            OkHttpClient mClient = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build();

            // Build retrofit base request
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CdiService.BASE_URL)
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mClient)
                    .build();

            CdiService service = retrofit.create(CdiService.class);

            String timestamp = getLastCdiTime();

            // Update CDI table
            Call<List<Cdi>> callGet = service.getCdi(timestamp);
            retrofit2.Response<List<Cdi>> responseGet = callGet.execute();
            List<Cdi> cdis = null;
            if (responseGet != null && responseGet.isSuccessful()) {
                cdis = responseGet.body();
            }

            ContentValues[] cdisArray;
            ArrayList<ContentValues> cdisCV = new ArrayList<ContentValues>();

            if (responseGet != null && responseGet.isSuccessful()){
                if(cdis != null && cdis.size() > 0){
                    for (Cdi cdi : cdis) {
                        ContentValues cdiCV = new ContentValues();
                        cdiCV.put(PortfolioContract.Cdi._ID, cdi.getId());
                        cdiCV.put(PortfolioContract.Cdi.COLUMN_VALUE, cdi.getValor());
                        cdiCV.put(PortfolioContract.Cdi.COLUMN_DATA, cdi.getDataString());
                        cdiCV.put(PortfolioContract.Cdi.COLUMN_TIMESTAMP, cdi.getData());
                        cdiCV.put(PortfolioContract.Cdi.LAST_UPDATE, cdi.getAtualizado());
                        cdisCV.add(cdiCV);
                    }
                    cdisArray = new ContentValues[cdisCV.size()];
                    cdisCV.toArray(cdisArray);

                    int insert = getApplicationContext().getContentResolver().bulkInsert(
                            PortfolioContract.Cdi.URI,
                            cdisArray);
                }

            } else {
                resultStatus = GcmNetworkManager.RESULT_FAILURE;
            }

            // Update IPCA table
            Call<List<Ipca>> callGetIpca = service.getIpca();
            retrofit2.Response<List<Ipca>> responseGetIpca = callGetIpca.execute();
            List<Ipca> ipcas = null;
            if (responseGetIpca != null && responseGetIpca.isSuccessful()) {
                ipcas = responseGetIpca.body();
            }

            ContentValues[] ipcaArray;
            ArrayList<ContentValues> ipcasCV = new ArrayList<ContentValues>();

            if (responseGetIpca != null && responseGetIpca.isSuccessful()){
                if(ipcas != null && ipcas.size() > 0){
                    for (Ipca ipca : ipcas) {
                        ContentValues ipcaCV = new ContentValues();
                        ipcaCV.put(PortfolioContract.Ipca._ID, ipca.getId());
                        ipcaCV.put(PortfolioContract.Ipca.COLUMN_ANO, ipca.getAno());
                        ipcaCV.put(PortfolioContract.Ipca.COLUMN_MES, ipca.getMes());
                        ipcaCV.put(PortfolioContract.Ipca.COLUMN_VALUE, ipca.getValor());
                        ipcaCV.put(PortfolioContract.Ipca.LAST_UPDATE, ipca.getAtualizado());
                        ipcasCV.add(ipcaCV);
                    }
                    ipcaArray = new ContentValues[ipcasCV.size()];
                    ipcasCV.toArray(ipcaArray);

                    int delete = getApplicationContext().getContentResolver().delete(
                            PortfolioContract.Ipca.URI,
                            null, null);

                    int insert = getApplicationContext().getContentResolver().bulkInsert(
                            PortfolioContract.Ipca.URI,
                            ipcaArray);
                }
            }

            // After CDI and IPCA update, start fixed income updates
            String[] symbols = params.getExtras().getString(FixedIntentService.ADD_SYMBOL).split(",");

            boolean update = true;
            ContentValues updateFail;
            for (String symbol: symbols) {
                boolean updateSuccess = updateFixedData(symbol);
                if (!updateSuccess){
                    updateFail = new ContentValues();
                    updateFail.put(PortfolioContract.FixedData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);

                    update = false;
                } else {
                    updateFail = new ContentValues();
                    updateFail.put(PortfolioContract.FixedData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                }
                String updateSelection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ?";
                String[] updatedSelectionArguments = {symbol};
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.FixedData.URI,
                        updateFail, updateSelection, updatedSelectionArguments);
            }

            if (update) {
                resultStatus = GcmNetworkManager.RESULT_SUCCESS;
            } else {
                // One or more Fixed income are without CDI Rate
                resultStatus = GcmNetworkManager.RESULT_RESCHEDULE;
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
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FIXED));
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

    private boolean updateFixedData(String symbol){
        double currentFixedValue = 0;
        double gainRate = 1;
        String selection = PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        Cursor transaction = getApplicationContext().getContentResolver().query(
                PortfolioContract.FixedTransaction.URI,
                null, selection, selectionArguments, null);

        // Cursor to look ahead
        Cursor nextTransaction = getApplicationContext().getContentResolver().query(
                PortfolioContract.FixedTransaction.URI,
                null, selection, selectionArguments, null);


        transaction.moveToFirst();
        nextTransaction.moveToFirst();
        do {
            double value = transaction.getDouble(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TOTAL));
            int type = transaction.getInt(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TYPE));
            int gainType = transaction.getInt(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_GAIN_TYPE));
            String timestamp = String.valueOf(transaction.getLong(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP))).substring(0,10);

            // CDI
            if(gainType == Constants.FixedType.CDI) {
                String sortOrder = PortfolioContract.Cdi.COLUMN_TIMESTAMP + " ASC";
                // Informações do cdi posteriores a data timestamp
                Cursor cdi = getApplicationContext().getContentResolver().query(
                        PortfolioContract.Cdi.makeUriForCdi(timestamp),
                        null, null, null, sortOrder);

                if (type == Constants.Type.BUY) {
                    currentFixedValue += value;
                    gainRate = transaction.getDouble(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_GAIN_RATE));
                } else {
                    // SELL
                    currentFixedValue -= value;
                }

                if (gainRate == 0) {
                    return false;
                }

                if (!cdi.moveToFirst()) {
                    // was not able to update cdi from server or there is not cdi for this transaction timestamp yet
                    continue;
                }

                if (nextTransaction.moveToNext()) {
                    // Has Next Transaction (Buy, Sell)
                    long cdiTimestamp = 0;
                    String nextTimeString = nextTransaction.getString(nextTransaction.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP)).substring(0, 10);
                    ;
                    long nextTimestamp = Long.valueOf(nextTimeString);
                    do {
                        double cdiValue = cdi.getDouble(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_VALUE));
                        double cdiDaily = getCdiDaily(cdiValue, gainRate);

                        currentFixedValue = currentFixedValue * cdiDaily;

                        if (cdi.moveToNext()) {
                            cdiTimestamp = cdi.getLong(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP));
                        } else {
                            // Transaction timestamp is bigger then last cdi timestamp
                            cdi.moveToLast();
                            break;
                        }
                    } while (cdiTimestamp < nextTimestamp);
                } else {
                    // Last one, only needs to sum or subtract and updated until end of CDI
                    // Last transaction
                    do {
                        double cdiValue = cdi.getDouble(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_VALUE));
                        double cdiDaily = getCdiDaily(cdiValue, gainRate);

                        currentFixedValue = currentFixedValue * cdiDaily;
                    } while (cdi.moveToNext());
                }
            } else if(gainType == Constants.FixedType.IPCA){
                // IPCA
                String sortOrder = PortfolioContract.Cdi.COLUMN_TIMESTAMP + " ASC";
                // Informações do cdi posteriores a data timestamp
                Cursor cdi = getApplicationContext().getContentResolver().query(
                        PortfolioContract.Cdi.makeUriForCdi(timestamp),
                        null, null, null, sortOrder);

                if (type == Constants.Type.BUY) {
                    currentFixedValue += value;
                    gainRate = transaction.getDouble(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_GAIN_RATE));
                    gainRate = gainRate*100;
                } else {
                    // SELL
                    currentFixedValue -= value;
                }

                if (!cdi.moveToFirst()) {
                    // was not able to update cdi from server or there is not cdi for this transaction timestamp yet
                    continue;
                }

                if (nextTransaction.moveToNext()) {
                    // Has Next Transaction (Buy, Sell)
                    long cdiTimestamp = 0;
                    String nextTimeString = nextTransaction.getString(nextTransaction.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP)).substring(0, 10);
                    ;
                    long nextTimestamp = Long.valueOf(nextTimeString);
                    do {
                        double ipcaGainRate = getIpcaGain(cdiTimestamp);
                        double cdiDaily = getCdiDaily(gainRate+ipcaGainRate, 1);

                        currentFixedValue = currentFixedValue * cdiDaily;

                        if (cdi.moveToNext()) {
                            cdiTimestamp = cdi.getLong(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP));
                        } else {
                            // Transaction timestamp is bigger then last cdi timestamp
                            cdi.moveToLast();
                            break;
                        }
                    } while (cdiTimestamp < nextTimestamp);
                } else {
                    // Last one, only needs to sum or subtract and updated until end of CDI
                    // Last transaction
                    do {
                        long cdiTimestamp = cdi.getLong(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP));;
                        double ipcaGainRate = getIpcaGain(cdiTimestamp);
                        ipcaGainRate = (double)Math.round(ipcaGainRate * 100d) / 100d;
                        double cdiDaily = getCdiDaily(gainRate+ipcaGainRate, 1);

                        currentFixedValue = currentFixedValue * cdiDaily;
                    } while (cdi.moveToNext());
                }
            } else if(gainType == Constants.FixedType.PRE){
                // Pré Fixado
                String sortOrder = PortfolioContract.Cdi.COLUMN_TIMESTAMP + " ASC";
                // Informações do cdi posteriores a data timestamp
                Cursor cdi = getApplicationContext().getContentResolver().query(
                        PortfolioContract.Cdi.makeUriForCdi(timestamp),
                        null, null, null, sortOrder);

                if (type == Constants.Type.BUY) {
                    currentFixedValue += value;
                    gainRate = transaction.getDouble(transaction.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_GAIN_RATE));
                    gainRate = gainRate*100;
                } else {
                    // SELL
                    currentFixedValue -= value;
                }

                if (!cdi.moveToFirst()) {
                    // was not able to update cdi from server or there is not cdi for this transaction timestamp yet
                    continue;
                }

                if (nextTransaction.moveToNext()) {
                    // Has Next Transaction (Buy, Sell)
                    long cdiTimestamp = 0;
                    String nextTimeString = nextTransaction.getString(nextTransaction.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP)).substring(0, 10);
                    ;
                    long nextTimestamp = Long.valueOf(nextTimeString);
                    do {
                        double cdiDaily = getCdiDaily(gainRate, 1);

                        currentFixedValue = currentFixedValue * cdiDaily;

                        if (cdi.moveToNext()) {
                            cdiTimestamp = cdi.getLong(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_TIMESTAMP));
                        } else {
                            // Transaction timestamp is bigger then last cdi timestamp
                            cdi.moveToLast();
                            break;
                        }
                    } while (cdiTimestamp < nextTimestamp);
                } else {
                    // Last one, only needs to sum or subtract and updated until end of CDI
                    // Last transaction
                    do {
                        double cdiValue = cdi.getDouble(cdi.getColumnIndex(PortfolioContract.Cdi.COLUMN_VALUE));
                        double cdiDaily = getCdiDaily(gainRate, 1);

                        currentFixedValue = currentFixedValue * cdiDaily;
                    } while (cdi.moveToNext());
                }
            }
        } while (transaction.moveToNext());

        currentFixedValue = (double) Math.round(currentFixedValue * 100) / 100;

        ContentValues fixedCV = new ContentValues();

        fixedCV.put(symbol, currentFixedValue);

        String updateSelection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ?";
        String[] updatedSelectionArguments = {symbol};

        int updatedRows = this.getContentResolver().update(
                PortfolioContract.FixedData.BULK_UPDATE_URI,
                fixedCV, null, null);

        return true;
    }

    // Does math formula to transform year cdi into daily cdi, according to formula found on google
    // https://www.calcbank.com.br/blog/como-fazer-calculos-em-do-cdi/
    private double getCdiDaily(double value, double gainRate){
        double cdiDaily;
        cdiDaily = ((Math.pow((1 + value/100),1f/252))-1)*gainRate + 1;
        return cdiDaily;
    }

    private String getLastCdiTime(){
        String[] affectedColumn = {PortfolioContract.Cdi.COLUMN_TIMESTAMP};
        String sortOrder = PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " DESC";

        Cursor cursor = getApplicationContext().getContentResolver().query(
                PortfolioContract.Cdi.URI,
                affectedColumn, null, null, sortOrder);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(0);
        }

        return "";
    }

    private double getIpcaGain(long cdiTimestamp){

        // Get month and year of cdi to use on query for IPCA value
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cdiTimestamp*1000);
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String yearLimit = String.valueOf(cal.get(Calendar.YEAR)-1);

        // Select IPCA value based on its year and month
        String[] affectedColumn = {PortfolioContract.Ipca.COLUMN_VALUE};
        String selection = "("+PortfolioContract.Ipca.COLUMN_MES + " <= ? AND "
                + PortfolioContract.Ipca.COLUMN_ANO + " = ?) OR ("
                + PortfolioContract.Ipca.COLUMN_MES + " > ? AND "
                + PortfolioContract.Ipca.COLUMN_ANO + " = ?)";
        String[] selectionArguments = {month, year, month, yearLimit};

        Cursor ipca = getApplicationContext().getContentResolver().query(
                PortfolioContract.Ipca.URI,
                null, selection, selectionArguments, null);

        // If a value was found, return it, else return zero
        if (ipca.moveToFirst()){
            // Sums for the hole last twelve month of IPCA. Cannot sum on query because of Compost IPCA
            double ipcaValue = 0;
            do{
                double rate = ipca.getDouble(ipca.getColumnIndex(PortfolioContract.Ipca.COLUMN_VALUE));
                ipcaValue = ipcaValue + rate + (ipcaValue *rate/100);
            } while (ipca.moveToNext());
            return ipcaValue;
        }

        return 0;
    }
}