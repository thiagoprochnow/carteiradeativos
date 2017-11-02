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

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.domain.ResponseCurrency;
import br.com.guiainvestimento.api.domain.ResponseCurrencyBackup;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Currency;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyIntentService extends IntentService {

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";

    Handler mHandler;

    // Log variable
    private static final String LOG_TAG = CurrencyIntentService.class.getSimpleName();

    /**
     * Constructor matching super is needed
     */
    public CurrencyIntentService() {
        super(CurrencyIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mHandler = new Handler();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try{
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL)) {
                int success = this.addCurrencyTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                if (success == GcmNetworkManager.RESULT_SUCCESS){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_currency), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // try the Pro Master backup API
                    int backupSuccess = this.backupAddCurrencyTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                    if (backupSuccess == GcmNetworkManager.RESULT_SUCCESS){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_currency), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_currency), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }else{
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL");
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }

    }

    private int addCurrencyTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StockService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            // Make the request and parse the result
            StockService service = retrofit.create(StockService.class);

            String[] symbols = params.getExtras().getString(CurrencyIntentService.ADD_SYMBOL).split(",");

            // Prepare the query to be added in YQL (Yahoo API)
            String query = "select * from yahoo.finance.xchange where pair in ("
                    + buildSymbolQuery(symbols) + ")";

            Call<ResponseCurrency> call;
            Response<ResponseCurrency> response;
            ResponseCurrency responseGetRate;
            int count = 0;

            do {
                call = service.getCurrency(query);
                response = call.execute();
                responseGetRate = response.body();
                count++;
            } while (response.code() == 400 && count < 20);

            if(response.isSuccessful() && responseGetRate.getDividendQuotes() != null) {
                for(Currency currency : responseGetRate.getDividendQuotes()){

                    // Remove last 3 letter
                    String tableSymbol = currency.getSymbol();
                    if( (tableSymbol.substring(tableSymbol.length() - 4, tableSymbol.length()).equals("/BRL"))) {
                        tableSymbol = tableSymbol.substring(0, tableSymbol.length() -4);
                    }

                    // Prepare the data of the current price to update the StockData table
                    ContentValues currencyDataCV = new ContentValues();
                    currencyDataCV.put(tableSymbol,
                            currency.getRate());

                    // Update value on stock data
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.CurrencyData.BULK_UPDATE_URI,
                            currencyDataCV, null, null);
                    // Log update success/fail result
                }
                resultStatus = GcmNetworkManager.RESULT_SUCCESS;
            } else {
                return GcmNetworkManager.RESULT_FAILURE;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }

    private int backupAddCurrencyTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackupAPICurrencyService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ContentValues currencyDataCV = new ContentValues();
        try {
            // Make the request and parse the result
            BackupAPICurrencyService service = retrofit.create(BackupAPICurrencyService.class);

            String[] symbols = params.getExtras().getString(CurrencyIntentService.ADD_SYMBOL).split(",");

            Call<ResponseCurrencyBackup> call;
            Response<ResponseCurrencyBackup> response;
            ResponseCurrencyBackup responseGetRate;
            int count = 0;

            for(String symbol : symbols) {
                call = service.getCurrencyBackupAPI(symbol, "json");
                response = call.execute();
                responseGetRate = response.body();
                count++;

                if (response.isSuccessful() && responseGetRate.getQuote(symbol) != null && responseGetRate.getQuote(symbol) != "") {

                        // Prepare the data of the current price to update the StockData table
                        currencyDataCV.put(symbol,
                                responseGetRate.getQuote(symbol));

                        // Log update success/fail result
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    resultStatus = GcmNetworkManager.RESULT_FAILURE;
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        if (resultStatus == GcmNetworkManager.RESULT_SUCCESS){
            // Update value on stock data
            int updatedRows = this.getContentResolver().update(
                    PortfolioContract.CurrencyData.BULK_UPDATE_URI,
                    currencyDataCV, null, null);
        }
        return resultStatus;
    }

    private String buildSymbolQuery(String[] symbols) {
        String resultQuery = "";

        if (symbols.length == 1) {

            resultQuery = "\"" + symbols[0] + "BRL" + "\"";
        } else {
            for (String symbol : symbols) {
                if (resultQuery.isEmpty()) {
                    resultQuery = "\"" + symbol + "BRL" + "\"";
                } else {
                    resultQuery += ",\"" + symbol + "BRL" + "\"";
                }
            }
        }
        return resultQuery;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sendBroadcast(new Intent(Constants.Receiver.CURRENCY));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.CURRENCY));
    }
}
