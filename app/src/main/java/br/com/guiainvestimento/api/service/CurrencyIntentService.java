package br.com.guiainvestimento.api.service;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.domain.ResponseCurrencyBackup;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.receiver.CurrencyReceiver;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyIntentService extends IntentService {

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    public static final String API_KEY = "FXVK1K9EYIJHIOEX";
    public static final String FUNCTION = "CURRENCY_EXCHANGE_RATE";
    private String mResult = "";
    private String mType;
    Handler mHandler;

    // Log variable
    private static final String LOG_TAG = CurrencyIntentService.class.getSimpleName();

    /**
     * Constructor matching super is needed
     */
    public CurrencyIntentService() {
        super(CurrencyIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mHandler = new Handler();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try{
            // Only calls the service if the symbol is present
            if (intent.hasExtra(ADD_SYMBOL)) {
                mType = ADD_SYMBOL;
                int success = this.addCurrencyTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                if (success == GcmNetworkManager.RESULT_SUCCESS){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_currency), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_currency), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else{
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL, CONSULT_SYMBOL");
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }

    }

    private int addCurrencyTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CurrencyService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ContentValues currencyDataCV = new ContentValues();
        try {
            // Make the request and parse the result
            CurrencyService service = retrofit.create(CurrencyService.class);

            String[] symbols = params.getExtras().getString(CurrencyIntentService.ADD_SYMBOL).split(",");

            Call<ResponseCurrencyBackup> call;
            Response<ResponseCurrencyBackup> response;
            ResponseCurrencyBackup responseGetRate;

            for (String symbol : symbols) {
                call = service.getCurrencyQuote(FUNCTION, symbol, "BRL", API_KEY);
                response = call.execute();
                responseGetRate = response.body();

                if (response.isSuccessful() && responseGetRate.getQuote(symbol) != null && responseGetRate.getQuote(symbol) != "") {
                    // Prepare the data of the current price to update the StockData table
                    currencyDataCV.put(symbol, responseGetRate.getQuote(symbol));
                } else {
                    // Error updating symbol
                }
            }
            if (currencyDataCV.size() > 0) {
                // Update value on stock data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.CurrencyData.BULK_UPDATE_URI,
                        currencyDataCV, null, null);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mType == ADD_SYMBOL) {
            CurrencyReceiver currencyReceiver = new CurrencyReceiver(this);
            currencyReceiver.updateCurrencyPortfolio();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.CURRENCY));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(this);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
        }
    }
}
