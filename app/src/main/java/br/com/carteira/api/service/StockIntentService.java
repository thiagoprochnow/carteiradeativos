package br.com.carteira.api.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

import br.com.carteira.api.domain.ResponseStock;
import br.com.carteira.api.domain.ResponseStocks;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
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

        Log.d(LOG_TAG, "Stock Intent Service start");

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
            Log.d(LOG_TAG, "Response log test:" +
                    "\nquery: " + query );
            if(symbols.length == 1) {
                Call<ResponseStock> call = service.getStock(query);
                Response<ResponseStock> response = call.execute();
                ResponseStock responseGetStock = response.body();

                if(responseGetStock.getStockQuotes() != null) {
                    Log.d(LOG_TAG, "Response log test:" +
                            "\nquery: " + query +
                            "\nsymbol: " + responseGetStock.getStockQuotes().get(0).getSymbol() +
                            "\nname: " + responseGetStock.getStockQuotes().get(0).getName() +
                            "\nlastPrice: " + responseGetStock.getStockQuotes().get(0).getLastTradePriceOnly() +
                            "\nquery symbol removal: " + responseGetStock.getStockQuotes().get(0).getSymbol().substring(responseGetStock.getStockQuotes().get(0).getSymbol().length() - 3, responseGetStock.getStockQuotes().get(0).getSymbol().length() ) +
                            "");

                    // Remove .SA (Brazil stocks) from symbol to match the symbol in Database
                    String tableSymbol = responseGetStock.getStockQuotes().get(0).getSymbol();
                    if( (tableSymbol.substring(tableSymbol.length() - 3, tableSymbol.length()).equals(".SA"))) {
                        tableSymbol = tableSymbol.substring(0, tableSymbol.length() -3);
                    }

                    // Prepare the data of the current price to update the StockData table
                    ContentValues stockDataCV = new ContentValues();
                    stockDataCV.put(PortfolioContract.StockData.COLUMN_CURRENT_PRICE,
                            responseGetStock.getStockQuotes().get(0).getLastTradePriceOnly());

                    // Prepare query to update stock data
                    String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
                    String[] updatedSelectionArguments = {tableSymbol};
                    mSymbol = tableSymbol;
                    // Update value on stock data
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.StockData.URI,
                            stockDataCV, updateSelection, updatedSelectionArguments);
                    // Log update success/fail result
                    if (updatedRows > 0) {
                        Log.d(LOG_TAG, "updateStockData successfully updated");
                        // Update Stock Portfolio
                        mSuccess = true;
                    }
                }
            }else{
                Call<ResponseStocks> call = service.getStocks(query);
                Response<ResponseStocks> response = call.execute();
                ResponseStocks responseGetStocks = response.body();
                if(responseGetStocks.getStockQuotes() != null) {
                    Log.d(LOG_TAG, "Response log test:" +
                            "\nquery: " + query +
                            "\nsymbol: " + responseGetStocks.getStockQuotes().get(1).getSymbol() +
                            "\nname: " + responseGetStocks.getStockQuotes().get(1).getName() +
                            "\nlastPrice: " + responseGetStocks.getStockQuotes().get(1).getLastTradePriceOnly() +
                            "");
                }
            }

            // Success request
            resultStatus = GcmNetworkManager.RESULT_SUCCESS;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }

    private String buildQuery(String[] symbols) {
        String resultQuery = "";
        if (symbols.length == 1) {

            resultQuery = "\"" + symbols[0] + "\"";
        } else {
            for (String symbol : symbols) {
                if (resultQuery.isEmpty()) {
                    resultQuery = "\"" + symbol + "\"";
                } else {
                    resultQuery += ",\"" + symbol + "\"";
                }
            }

        }

        return resultQuery;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Updates the StockData values
            String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";
            String[] selectionArguments = {mSymbol};

            Cursor queryCursor = this.getContentResolver().query(
                    PortfolioContract.StockData.URI,
                    null, selection, selectionArguments, null);

            if (queryCursor.getCount() > 0){
                queryCursor.moveToFirst();

                // Prepare new values to update StockData table
                int quantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL));
                double currentPrice = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_PRICE));
                double totalBuy = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL));
                double incomeTotal = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_INCOME_TOTAL));
                double currentTotal = quantity*currentPrice;
                double variation = currentTotal - totalBuy;
                double totalGain = currentTotal + incomeTotal - totalBuy;
                double incomeTotalPercent = incomeTotal/totalBuy*100;
                double variationPercent= variation/totalBuy*100;
                double totalGainPercent = totalGain/totalBuy*100;

                ContentValues stockDataCV = new ContentValues();
                stockDataCV.put(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL, currentTotal);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_VARIATION, variation);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_TOTAL_GAIN, totalGain);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_INCOME_TOTAL_PERCENT, incomeTotalPercent);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_VARIATION_PERCENT, variationPercent);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_TOTAL_GAIN_PERCENT, totalGainPercent);

                // Update value on stock data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.StockData.URI,
                        stockDataCV, selection, selectionArguments);

                // Log update success/fail result
                if (updatedRows > 0){
                    Log.d(LOG_TAG, "updateStockData successfully updated");
                    // Send broadcast so StockReceiver can update the rest
                    this.sendBroadcast(new Intent(Constants.Receiver.STOCK));
                } else {
                    Log.d(LOG_TAG, "updateStockData failed update");
                }

            } else{
                Log.d(LOG_TAG, "StockData was not found for symbol: " + mSymbol);
            }
    }
}