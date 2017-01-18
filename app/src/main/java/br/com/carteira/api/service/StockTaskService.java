package br.com.carteira.api.service;

import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

import br.com.carteira.api.domain.ResponseStock;
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
            if (params.getExtras().getString(StockIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            // Prepare the query to be added in YQL (Yahoo API)
            String query = "select * from yahoo.finance.quotes where symbol in (\""
                    + params.getExtras().getString(StockIntentService.ADD_SYMBOL) + "\")";


            // Make the request and parse the result
            StockService service = retrofit.create(StockService.class);
            Call<ResponseStock> call = service.getStock(query);
            Response<ResponseStock> response = call.execute();
            ResponseStock responseGetStock = response.body();

            // Debug test log - the return will be changed for multiple symbols
            if(responseGetStock.getStockQuotes() != null) {
                Log.d(LOG_TAG, "Response log test:" +
                        "\nquery: " + query +
                        "\nsymbol: " + responseGetStock.getStockQuotes().get(0).getSymbol() +
                        "\nname: " + responseGetStock.getStockQuotes().get(0).getName() +
                        "\nbid: " + responseGetStock.getStockQuotes().get(0).getBid() +
                        "\nchange: " + responseGetStock.getStockQuotes().get(0).getChange() +
                        "\nchange in percent: " + responseGetStock.getStockQuotes().get(0).getChangeInPercent() +
                        "\nday low: " + responseGetStock.getStockQuotes().get(0).getDaysLow() +
                        "\nday high: " + responseGetStock.getStockQuotes().get(0).getDaysHigh() +
                        "");
            }

            // Success request
            resultStatus = GcmNetworkManager.RESULT_SUCCESS;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }

}