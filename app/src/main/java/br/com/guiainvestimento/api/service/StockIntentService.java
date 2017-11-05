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
import java.util.logging.LogRecord;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.domain.ResponseStock;
import br.com.guiainvestimento.api.domain.ResponseStocks;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Stock;
import retrofit2.Call;
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
            int success = this.addStockTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
            if (success == GcmNetworkManager.RESULT_SUCCESS){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_stocks), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // try the Bolsa Financeira backup API
                int backupSuccess = this.backupAddStockTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                if (backupSuccess == GcmNetworkManager.RESULT_SUCCESS){
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
    }

    private int addStockTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StockService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(StockIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            // Make the request and parse the result
            StockService service = retrofit.create(StockService.class);

            String[] symbols = params.getExtras().getString(StockIntentService.ADD_SYMBOL).split(",");

            // Prepare the query to be added in YQL (Yahoo API)
            String query = "select * from yahoo.finance.quotes where symbol in ("
                    + buildQuery(symbols) + ")";
            if(symbols.length == 1) {
                Call<ResponseStock> call;
                Response<ResponseStock> response;
                ResponseStock responseGetStock;
                int count = 0;
                do {
                    call = service.getStock(query);
                    response = call.execute();
                    responseGetStock = response.body();
                    count++;
                } while (response.code() == 400 && count < 20);

                if(response.isSuccessful() && responseGetStock.getStockQuotes() != null && !responseGetStock.getStockQuotes().isEmpty() &&
                        responseGetStock.getStockQuotes().get(0).getLastTradePriceOnly() != null) {

                    // Remove .SA (Brazil stocks) from symbol to match the symbol in Database
                    String tableSymbol = responseGetStock.getStockQuotes().get(0).getSymbol();
                    if( (tableSymbol.substring(tableSymbol.length() - 3, tableSymbol.length()).equals(".SA"))) {
                        tableSymbol = tableSymbol.substring(0, tableSymbol.length() -3);
                    }

                    // Prepare the data of the current price to update the StockData table
                    ContentValues stockDataCV = new ContentValues();
                    stockDataCV.put(tableSymbol,
                            responseGetStock.getStockQuotes().get(0).getLastTradePriceOnly());

                    // Update value on stock data
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.StockData.BULK_UPDATE_URI,
                            stockDataCV, null, null);
                    // Log update success/fail result
                    if (updatedRows > 0) {
                        // Update Stock Portfolio
                        mSuccess = true;
                    }
                    // Success request
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    return GcmNetworkManager.RESULT_FAILURE;
                }
            }else{
                Call<ResponseStocks> call;
                Response<ResponseStocks> response;
                ResponseStocks responseGetStocks;
                int count = 0;
                do {
                    call = service.getStocks(query);
                    response = call.execute();
                    responseGetStocks = response.body();
                    count++;
                } while (response.code() == 400 && count < 20);

                if(response.isSuccessful() && responseGetStocks != null && responseGetStocks.getStockQuotes() != null && !responseGetStocks.getStockQuotes().isEmpty()) {
                    String tableSymbol = "";
                    ContentValues stockDataCV = new ContentValues();
                    for(Stock stock : responseGetStocks.getStockQuotes()) {
                        // Remove .SA (Brazil stocks) from symbol to match the symbol in Database
                        tableSymbol = stock.getSymbol();
                        if ((tableSymbol.substring(tableSymbol.length() - 3, tableSymbol.length()).equals(".SA"))) {
                            tableSymbol = tableSymbol.substring(0, tableSymbol.length() - 3);
                        }
                        stockDataCV.put(tableSymbol,
                                stock.getLastTradePriceOnly());
                    }

                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.StockData.BULK_UPDATE_URI,
                            stockDataCV, null, null);
                    // Success request
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    return GcmNetworkManager.RESULT_FAILURE;
                }
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }

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

                if (response.isSuccessful() && responseGetStock != null && responseGetStock != "" && !responseGetStock.trim().isEmpty()) {
                    String[] arrayGetStock = responseGetStock.split(",");
                    // Prepare the data of the current price to update the StockData table
                    if (arrayGetStock.length > 9) {
                        stockDataCV.put(symbol, arrayGetStock[9]);
                    }
                } else {
                    // symbol not updated automaticly
                }
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

    private String buildQuery(String[] symbols) {
        String resultQuery = "";

        if (symbols.length == 1) {

            resultQuery = "\"" + symbols[0] + ".SA" + "\"";
        } else {
            for (String symbol : symbols) {
                if (resultQuery.isEmpty()) {
                    resultQuery = "\"" + symbol + ".SA" + "\"";
                } else {
                    resultQuery += ",\"" + symbol + ".SA" + "\"";
                }
            }

        }

        return resultQuery;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sendBroadcast(new Intent(Constants.Receiver.STOCK));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));
    }
}