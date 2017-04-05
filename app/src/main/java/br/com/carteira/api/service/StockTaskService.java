package br.com.carteira.api.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
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
 * Task Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */
public class StockTaskService extends GcmTaskService {

    // Log variable
    private static final String LOG_TAG = StockTaskService.class.getSimpleName();

    private Context mContext;

    public StockTaskService(Context context) {
        mContext = context;
    }

    @Override
    public int onRunTask(TaskParams params) {

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
                            "\nquery: " + responseGetStock.getStockQuotes().get(0).getSymbol().substring(responseGetStock.getStockQuotes().get(0).getSymbol().length() - 3, responseGetStock.getStockQuotes().get(0).getSymbol().length() ) +
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

                    // Update value on stock data
                    int updatedRows = mContext.getContentResolver().update(
                            PortfolioContract.StockData.URI,
                            stockDataCV, updateSelection, updatedSelectionArguments);
                    // Log update success/fail result
                    if (updatedRows > 0) {
                        Log.d(LOG_TAG, "updateStockData successfully updated");
                        // Update Stock Portfolio
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

}