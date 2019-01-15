package br.com.guiainvestimento.api.domain;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.domain.Currency;

@SuppressWarnings("unused")
public class ResponseCurrencyBackup {

    @SerializedName("Realtime Currency Exchange Rate")
    private Result mResult;

    private static final String LOG_TAG = ResponseCurrencyBackup.class.getSimpleName();

    /**
     * Get list of Currencies attributes present in response
     * @return - List of Currencies
     */
    public String getQuote(String symbol) {
        if (mResult != null && mResult.getCurrencyQuote() != null && mResult.getCurrencyQuote() != "") {
            return mResult.getCurrencyQuote();
        }
        return "";
    }

    /**
     * Inner class - Result response mapping
     */
    private class Result {

        @SerializedName("5. Exchange Rate")
        private String mCurrencyQuote;

        private String getCurrencyQuote() {
            return mCurrencyQuote;
        }
    }

    /**
     * Inner class - Quote response mapping
     */
    private class Quote {

        @SerializedName("5. Exchange Rate")
        private String mCurrencyQuote;

        private String getCurrencyQuote() {
            return mCurrencyQuote;
        }
    }
}
