package br.com.carteira.api.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.carteira.domain.Fii;

/**
 * Domain class that will map the response of a getStock request made to YAHOO Api.
 * This mapping will only be used for one symbol request, otherwise ResponseStocks should be used
 */
@SuppressWarnings("unused")
public class ResponseFii {

    @SerializedName("query")
    private Result mResult;

    /**
     * Get list of Fiis
     * @return - List of Fiis
     */
    public List<Fii> getFiiQuotes() {
        List<Fii> result = new ArrayList<>();
        if (mResult != null && mResult.getQuote() != null) {
            result.add(mResult.getQuote().getFiiQuote());
        }
        return result;
    }

    /**
     * Inner class - Result response mapping
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
        private Fii mFiiQuote;

        private Fii getFiiQuote() {
            return mFiiQuote;
        }
    }
}
