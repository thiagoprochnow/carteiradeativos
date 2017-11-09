package br.com.guiainvestimento.api.service;


import android.app.IntentService;
import android.content.Intent;

public class StockIncomeIntentService extends IntentService {

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";

    // Log variable
    private static final String LOG_TAG = StockIncomeIntentService.class.getSimpleName();

    /**
     * Constructor matching super is needed
     */
    public StockIncomeIntentService() {
        super(StockIncomeIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
