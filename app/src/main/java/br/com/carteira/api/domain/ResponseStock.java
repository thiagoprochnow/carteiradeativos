package br.com.carteira.api.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain class that will map the response of a getStock request made to YAHOO Api
 */
@SuppressWarnings("unused")
public class ResponseStock {

    @SerializedName("query")
    private Result mResult;

    /**
     * Get list of Stocks
     * @return - List of Stocks
     */
    public List<Stock> getStockQuotes() {
        // TODO make this method able to return more than one quote if the params have more than one symbol
        List<Stock> result = new ArrayList<>();
        if (mResult != null && mResult.getQuote() != null) {
                result.add(mResult.getQuote().getStockQuote());
        }
        return result;
    }

    /**
     *  Inner class - Result response mapping
     */
    private class Result {

        @SerializedName("results")
        private Quote mQuote;

        @SerializedName("count")
        private int mCount;

        private Quote getQuote() {
            return mQuote;
        }

        public int getCount() {
            return mCount;
        }
    }

    /**
     * Inner class - Quote response mapping
     */
    private class Quote {

        @SerializedName("quote")
        private Stock mStockQuote;

        private Stock getStockQuote() {
            return mStockQuote;
        }
    }
}
