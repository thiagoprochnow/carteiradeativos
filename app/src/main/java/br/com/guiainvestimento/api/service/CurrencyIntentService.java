package br.com.guiainvestimento.api.service;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

import br.com.guiainvestimento.api.domain.ResponseCurrency;
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

    // Log variable
    private static final String LOG_TAG = CurrencyIntentService.class.getSimpleName();

    /**
     * Constructor matching super is needed
     */
    public CurrencyIntentService() {
        super(CurrencyIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try{
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL)) {
                this.addCurrencyTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
            }else{
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL");
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }

    }

    private int addCurrencyTask(TaskParams params) {

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

            Call<ResponseCurrency> call = service.getCurrency(query);
            Response<ResponseCurrency> response = call.execute();
            ResponseCurrency responseGetRate = response.body();
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
                    if (updatedRows > 0) {
                    }


                }
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
