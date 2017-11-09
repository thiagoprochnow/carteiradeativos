package br.com.guiainvestimento.api.service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

import br.com.guiainvestimento.api.domain.ResponseStockIncome;
import br.com.guiainvestimento.domain.Dividend;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
