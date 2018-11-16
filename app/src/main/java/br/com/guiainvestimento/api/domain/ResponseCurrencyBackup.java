package br.com.guiainvestimento.api.domain;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.domain.Currency;

@SuppressWarnings("unused")
public class ResponseCurrencyBackup {

    @SerializedName("valores")
    private Result mResult;

    private static final String LOG_TAG = ResponseCurrencyBackup.class.getSimpleName();

    /**
     * Get list of Currencies attributes present in response
     * @return - List of Currencies
     */
    public String getQuote(String symbol) {
        if (mResult != null && mResult.getQuote(symbol) != null && mResult.getQuote(symbol) != "") {
            return mResult.getQuote(symbol);
        }
        return "";
    }

    /**
     * Inner class - Result response mapping
     */
    private class Result {

        @SerializedName("USD")
        private Quote mUSD;

        @SerializedName("EUR")
        private Quote mEUR;

        private String getQuote(String symbol) {
            if (symbol.equalsIgnoreCase("USD")){
                if (mUSD != null && mUSD.getCurrencyQuote() != null && mUSD.getCurrencyQuote() != ""){
                    return mUSD.getCurrencyQuote();
                }
            } else if (symbol.equalsIgnoreCase("EUR")) {
                if (mEUR != null && mEUR.getCurrencyQuote() != null && mEUR.getCurrencyQuote() != ""){
                    return mEUR.getCurrencyQuote();
                }
            }
            return "";
        }
    }

    /**
     * Inner class - Quote response mapping
     */
    private class Quote {

        @SerializedName("valor")
        private String mCurrencyQuote;

        private String getCurrencyQuote() {
            return mCurrencyQuote;
        }
    }
}
