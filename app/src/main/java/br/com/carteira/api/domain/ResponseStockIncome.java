package br.com.carteira.api.domain;

import com.google.gson.Gson;
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
        List<Dividend> dividendQuotes =  mResult.getQuotes();
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

        @SerializedName("count")
        private int mCount;

        @SerializedName("results")
        private Object mRawQuote;

        private List<Dividend> getQuotes() {
            Gson gson = new Gson();
            String rawJson = gson.toJson(mRawQuote);

            // Api return two different types of response - one if there is only one element in the
            // list, and if there is more than one. A JSON list is returned.
            if(mCount == 1){
                Quote quote = gson.fromJson(rawJson, Quote.class);
                List<Dividend> dividendList = new ArrayList<Dividend>();
                dividendList.add(quote.getDividendQuote());
                return dividendList;
            }else{
                Quotes quotes = gson.fromJson(rawJson, Quotes.class);
                return quotes.getDividendQuotes();
            }
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

    /**
     * Inner class - Quote response mapping
     */
    private class Quote {

        @SerializedName("quote")
        private Dividend mDividendQuote;

        private Dividend getDividendQuote() {
            return mDividendQuote;
        }
    }
}
