package br.com.guiainvestimento.api.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.domain.Fii;

/**
 * Domain class that will map the response of a getStocks request made to YAHOO Api.
 * Unfortunately the response of one stock is different from the response of a multiple stock
 * resquest, being necessary to create two types of mappings.
 */
@SuppressWarnings("unused")
public class ResponseFiis {

    @SerializedName("query")
    private Result mResult;

    /**
     * Get list of Fiis that have Name and LastTradePrice attributes present in response
     * @return - List of Fiis
     */
    public List<Fii> getFiiQuotes() {
        List<Fii> result = new ArrayList<>();
        List<Fii> fiiQuotes = mResult.getQuotes().getFiiQuotes();
        for (Fii quote : fiiQuotes){
            if (quote.getLastTradePriceOnly() != null
                    && quote.getName() != null) {
                result.add(quote);
            }
        }
        return result;
    }

    /**
     *  Inner class - Result response mapping
     */
    private class Result {

        @SerializedName("results")
        private Quotes mQuote;

        @SerializedName("count")
        private int mCount;

        private Quotes getQuotes() {
            return mQuote;
        }

        public int getCount() {
            return mCount;
        }
    }

    /**
     * Inner class - Quote response mapping
     */
    public class Quotes {

        @SerializedName("quote")
        private List<Fii> mFiiQuotes = new ArrayList<>();

        public List<Fii> getFiiQuotes() {
            return mFiiQuotes;
        }
    }
}
