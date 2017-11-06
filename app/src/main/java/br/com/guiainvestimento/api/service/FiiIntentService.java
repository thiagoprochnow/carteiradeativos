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
import br.com.guiainvestimento.api.domain.ResponseFii;

import br.com.guiainvestimento.api.domain.ResponseFiis;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Fii;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Service responsible for making the Api request and parser the result.
 * This class will also write the result in the database and update the adapter
 */

public class FiiIntentService extends IntentService {

    // Log variable
    private static final String LOG_TAG = FiiIntentService.class.getSimpleName();
    private final String BFToken = "42745199c91e49ff73706f997cd8f465";

    private boolean mSuccess = false;
    private String mSymbol;
    Handler mHandler;

    // Extras
    public static final String ADD_SYMBOL = "symbol";

    /**
     * Constructor matching super is needed
     */
    public FiiIntentService() {
        super(FiiIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mHandler = new Handler();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Only calls the service if the symbol is present
        if (intent.hasExtra(ADD_SYMBOL)) {
            // try the Bolsa Financeira backup API
            int success = this.backupAddFiiTask(new TaskParams(ADD_SYMBOL, intent.getExtras()));
            if (success == GcmNetworkManager.RESULT_SUCCESS){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.success_updating_fii), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_updating_fii), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
/*
    private int addFiiTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FiiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(FiiIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            // Make the request and parse the result
            FiiService service = retrofit.create(FiiService.class);

            String[] symbols = params.getExtras().getString(FiiIntentService.ADD_SYMBOL).split(",");

            // Prepare the query to be added in YQL (Yahoo API)
            String query = "select * from yahoo.finance.quotes where symbol in ("
                    + buildQuery(symbols) + ")";
            if(symbols.length == 1) {

                Call<ResponseFii> call;
                Response<ResponseFii> response;
                ResponseFii responseGetFii;
                int count = 0;

                do {
                    call = service.getFii(query);
                    response = call.execute();
                    responseGetFii = response.body();
                    count++;
                } while (response.code() == 400 && count < 20);

                if(response.isSuccessful() && responseGetFii.getFiiQuotes() != null && !responseGetFii.getFiiQuotes().isEmpty() &&
                        responseGetFii.getFiiQuotes().get(0).getLastTradePriceOnly() != null) {

                    // Remove .SA (Brazil fiis) from symbol to match the symbol in Database
                    String tableSymbol = responseGetFii.getFiiQuotes().get(0).getSymbol();
                    if( (tableSymbol.substring(tableSymbol.length() - 3, tableSymbol.length()).equals(".SA"))) {
                        tableSymbol = tableSymbol.substring(0, tableSymbol.length() -3);
                    }

                    // Prepare the data of the current price to update the FiiData table
                    ContentValues fiiDataCV = new ContentValues();
                    fiiDataCV.put(tableSymbol,
                            responseGetFii.getFiiQuotes().get(0).getLastTradePriceOnly());

                    // Update value on fii data
                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.FiiData.BULK_UPDATE_URI,
                            fiiDataCV, null, null);
                    // Log update success/fail result
                    if (updatedRows > 0) {
                        // Update Fii Portfolio
                        mSuccess = true;
                    }
                    // Success request
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    return GcmNetworkManager.RESULT_FAILURE;
                }
            }else{
                Call<ResponseFiis> call;
                Response<ResponseFiis> response;
                ResponseFiis responseGetFiis;
                int count = 0;

                do {
                    call = service.getFiis(query);
                    response = call.execute();
                    responseGetFiis = response.body();
                    count++;
                } while (response.code() == 400 && count < 20);

                if(response.isSuccessful() && responseGetFiis != null && responseGetFiis.getFiiQuotes() != null && !responseGetFiis.getFiiQuotes().isEmpty()) {
                    String tableSymbol = "";
                    ContentValues fiiDataCV = new ContentValues();
                    for(Fii fii : responseGetFiis.getFiiQuotes()) {
                        // Remove .SA (Brazil fiis) from symbol to match the symbol in Database
                        tableSymbol = fii.getSymbol();
                        if ((tableSymbol.substring(tableSymbol.length() - 3, tableSymbol.length()).equals(".SA"))) {
                            tableSymbol = tableSymbol.substring(0, tableSymbol.length() - 3);
                        }
                        fiiDataCV.put(tableSymbol,
                                fii.getLastTradePriceOnly());
                    }

                    int updatedRows = this.getContentResolver().update(
                            PortfolioContract.FiiData.BULK_UPDATE_URI,
                            fiiDataCV, null, null);
                    // Success request
                    resultStatus = GcmNetworkManager.RESULT_SUCCESS;
                } else {
                    return GcmNetworkManager.RESULT_FAILURE;
                }
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in request " + e.getMessage());
            e.printStackTrace();
        }
        return resultStatus;
    }
*/
    // This is used whem the yahoo API fails
    // Since yahoo API is very inconsistent it is important to have a paid service backup fallback
    private int backupAddFiiTask(TaskParams params) {

        int resultStatus = GcmNetworkManager.RESULT_FAILURE;

        // Build retrofit base request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackupAPIFiiService.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ContentValues fiiDataCV = new ContentValues();
        try {
            // Validate if Symbol was added
            if (params.getExtras() == null || params.getExtras().getString(FiiIntentService.ADD_SYMBOL).isEmpty()) {
                throw new IOException("Missing Extra ADD_SYMBOL");
            }

            // Make the request and parse the result
            BackupAPIFiiService service = retrofit.create(BackupAPIFiiService.class);

            String[] symbols = params.getExtras().getString(FiiIntentService.ADD_SYMBOL).split(",");
            for (String symbol: symbols) {
                Call<String> call;
                Response<String> response;
                String responseGetFii;

                call = service.getFiiBackupAPI(BFToken, symbol);
                response = call.execute();
                responseGetFii = response.body();

                ContentValues updateFii = new ContentValues();
                if (response.isSuccessful() && responseGetFii != null && responseGetFii != "" && !responseGetFii.trim().isEmpty()) {
                    String[] arrayGetFii = responseGetFii.split(",");
                    // Prepare the data of the current price to update the FiiData table
                    if (arrayGetFii.length > 9) {
                        fiiDataCV.put(symbol, arrayGetFii[9]);
                        updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.UPDATED);
                        updateFii.put(PortfolioContract.FiiData.COLUMN_OPENING_PRICE, arrayGetFii[5]);
                    } else {
                        updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                        updateFii.put(PortfolioContract.FiiData.COLUMN_OPENING_PRICE, 0);
                    }
                } else {
                    // symbol not updated automatically
                    updateFii.put(PortfolioContract.FiiData.COLUMN_UPDATE_STATUS, Constants.UpdateStatus.NOT_UPDATED);
                    updateFii.put(PortfolioContract.FiiData.COLUMN_OPENING_PRICE, 0);
                }

                String updateSelection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
                String[] updatedSelectionArguments = {symbol};
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.FiiData.URI,
                        updateFii, updateSelection, updatedSelectionArguments);
            }

            if (fiiDataCV.size() > 0){
                // Update value on fii data
                int updatedRows = this.getContentResolver().update(
                        PortfolioContract.FiiData.BULK_UPDATE_URI,
                        fiiDataCV, null, null);
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
/*
    private String buildQuery(String[] symbols) {
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
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.sendBroadcast(new Intent(Constants.Receiver.FII));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FII));
    }
}