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
import br.com.guiainvestimento.api.domain.ResponseCrypto;
import br.com.guiainvestimento.api.domain.ResponseCurrency;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Crypto;
import br.com.guiainvestimento.domain.Currency;
import br.com.guiainvestimento.receiver.CurrencyReceiver;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import br.com.guiainvestimento.receiver.TreasuryReceiver;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CryptoIntentService extends IntentService {

    // Extras
    public static final String ADD_SYMBOL = "symbol";
    private String mResult = "";
    private String mType;
    Handler mHandler;

    // Log variable
    private static final String LOG_TAG = CryptoIntentService.class.getSimpleName();

    /**
     * Constructor matching super is needed
     */
    public CryptoIntentService() {
        super(CryptoIntentService.class.getName());
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
                int success = this.addCryptoTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
                if (success == GcmNetworkManager.RESULT_SUCCESS){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_crypto_currency), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_crypto_currency), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else{
                throw new IOException("Missing one of the following Extras: ADD_SYMBOL");
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }

    }

    private int addCryptoTask(TaskParams params) {
        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CryptoService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            // Make the request and parse the result
            CryptoService service = retrofit.create(CryptoService.class);

            String[] symbols = params.getExtras().getString(CryptoIntentService.ADD_SYMBOL).split(",");
            for (String symbol: symbols) {
                Call<ResponseCrypto> call;
                Response<ResponseCrypto> response;
                ResponseCrypto responseGetRate;
                int count = 0;

                do {
                    call = service.getCrypto(symbol);
                    response = call.execute();
                    responseGetRate = response.body();
                    count++;
                } while (response.code() == 400 && count < 20);
                if (response.isSuccessful() && responseGetRate.getCryptoQuote() != null) {
                    for (Crypto currency : responseGetRate.getCryptoQuote()) {

                        // Prepare the data of the current price to update the StockData table
                        ContentValues currencyDataCV = new ContentValues();
                        currencyDataCV.put(symbol,currency.getQuote());
                        // Update value on stock data
                        int updatedRows = this.getContentResolver().update(
                                PortfolioContract.CurrencyData.BULK_UPDATE_URI,
                                currencyDataCV, null, null);
                        // Log update success/fail result
                    }
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    resultStatus = GcmNetworkManager.RESULT_FAILURE;
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
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
