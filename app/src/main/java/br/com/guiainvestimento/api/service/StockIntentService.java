package br.com.guiainvestimento.api.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.util.logging.Handler;

import br.com.guiainvestimento.api.domain.ResponseStock;
import br.com.guiainvestimento.api.domain.ResponseStocks;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Stock;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */

public class StockIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = StockIntentService.class.getSimpleName();

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
    protected void onHandleIntent(Intent intent) {

        // Only calls the service if the symbol is present
        if (intent.hasExtra(ADD_SYMBOL)) {
            this.addStockTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
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
                if(response.isSuccessful() && responseGetStock.getStockQuotes() != null &&
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

                if(response.isSuccessful() && responseGetStocks.getStockQuotes() != null) {
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
                }
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
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