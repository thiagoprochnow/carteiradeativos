package br.com.carteira.api.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.carteira.domain.Dividend;

/**
 * Domain class that will map the response of a getDividend request made to YAHOO Api.
 */
@SuppressWarnings("unused")
public class ResponseStockIncome {


    @SerializedName("query")
    private ResponseStockIncome.Result mResult;

    /**
     * Get list of Dividends that have DAte and Dividends attributes present in response
     * @return - List of Stocks
     */
    public List<Dividend> getDividendQuotes() {
        List<Dividend> result = new ArrayList<>();
        List<Dividend> dividendQuotes = mResult.getQuotes().getDividendQuotes();
        for (Dividend dividend : dividendQuotes){
            if (dividend.getDate() != null
                    && dividend.getDividends() != null) {
                result.add(dividend);
            }
        }
        return result;
    }

    /**
     *  Inner class - Result response mapping
     */
    private class Result {

        @SerializedName("results")
        private ResponseStockIncome.Quotes mQuote;

        @SerializedName("count")
        private int mCount;

        private ResponseStockIncome.Quotes getQuotes() {
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
        private List<Dividend> mDividendQuotes = new ArrayList<>();

        public List<Dividend> getDividendQuotes() {
            return mDividendQuotes;
        }
    }
}
