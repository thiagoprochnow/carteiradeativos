package br.com.carteira.api.service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

import br.com.carteira.api.domain.ResponseDividend;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DividendIntentService extends IntentService {

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";

    // Log variable
    private static final String LOG_TAG = DividendIntentService.class.getSimpleName();

    /**
     * Constructor matching super is needed
     */
    public DividendIntentService() {
        super(DividendIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try{
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL) && intent.hasExtra(START_DATE) && intent.hasExtra(END_DATE)) {
                this.addDividendTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
            }else{
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL, START_DATE, END_DATE");
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }

    }

    private int addDividendTask(TaskParams params) {

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StockService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            // Make the request and parse the result
            StockService service = retrofit.create(StockService.class);

            String[] symbols = params.getExtras().getString(DividendIntentService.ADD_SYMBOL).split(",");
            String startDate = params.getExtras().getString(DividendIntentService.START_DATE);
            String endDate = params.getExtras().getString(DividendIntentService.END_DATE);

            // Prepare the query to be added in YQL (Yahoo API)
            String query = "select * from yahoo.finance.dividendhistory where symbol in ("
                    + buildSymbolQuery(symbols) + ")" + buildDateQuery(startDate, endDate);
            Log.d(LOG_TAG, "Response log test:" +
                    "\nquery: " + query);

            Call<ResponseDividend> call = service.getDividend(query);
            Response<ResponseDividend> response = call.execute();
            ResponseDividend responseGetStock = response.body();

            if(responseGetStock.getDividendQuotes() != null) {
                Log.d(LOG_TAG, "Response log test:" +
                        "\nquery: " + query +
                        "");
                // TODO - implement DB logic to update
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private String buildSymbolQuery(String[] symbols) {
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

    private String buildDateQuery(String startDate, String endDate) {
        String resultQuery = "";
        if(!startDate.isEmpty())
            resultQuery += "and startDate = \"" + startDate + "\"";
        if(!endDate.isEmpty())
            resultQuery += "and endDate = \"" + startDate + "\"";

        return resultQuery;
    }
}
