package br.com.carteira.api.domain;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.carteira.domain.Stock;

/**
 * Domain class that will map the response of a getStocks request made to YAHOO Api.
 * Unfortunately the response of one stock is different from the response of a multiple stock
 * resquest, being necessary to create two types of mappings.
 */
@SuppressWarnings("unused")
public class ResponseStocks {

    @SerializedName("query")
    private Result mResult;

    /**
     * Get list of Stocks that have Name and LastTradePrice attributes present in response
     * @return - List of Stocks
     */
    public List<Stock> getStockQuotes() {
        List<Stock> result = new ArrayList<>();
        List<Stock> stockQuotes = mResult.getQuotes().getStockQuotes();
        for (Stock quote : stockQuotes){
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
        private List<Stock> mStockQuotes = new ArrayList<>();

        public List<Stock> getStockQuotes() {
            return mStockQuotes;
        }
    }
}
