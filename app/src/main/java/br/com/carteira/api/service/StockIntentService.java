package br.com.carteira.api.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Walter on 11/01/2017.
 */

public class StockIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = StockIntentService.class.getSimpleName();

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

        StockTaskService stockTaskService = new StockTaskService(this);

        // Only calls the service if the symbol is present
        if (intent.hasExtra(ADD_SYMBOL)) {
            stockTaskService.onRunTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
        }
    }
}