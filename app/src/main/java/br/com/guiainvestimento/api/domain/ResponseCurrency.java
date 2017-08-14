package br.com.guiainvestimento.api.domain;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.domain.Currency;

@SuppressWarnings("unused")
public class ResponseCurrency {

    @SerializedName("query")
    private ResponseCurrency.Result mResult;

    /**
     * Get list of Currencies attributes present in response
     * @return - List of Currencies
     */
    public List<Currency> getDividendQuotes() {
        List<Currency> result = new ArrayList<>();
        List<Currency> currencyQuotes =  mResult.getQuotes();
        for (Currency currency : currencyQuotes){
            if (currency.getDate() != null
                    && currency.getRate() != null) {
                result.add(currency);
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

        private List<Currency> getQuotes() {
            Gson gson = new Gson();
            String rawJson = gson.toJson(mRawQuote);

            // Api return two different types of response - one if there is only one element in the
            // list, and if there is more than one. A JSON list is returned.
            if(mCount == 1){
                Quote quote = gson.fromJson(rawJson, Quote.class);
                List<Currency> dividendList = new ArrayList<Currency>();
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

        @SerializedName("rate")
        private List<Currency> mDividendQuotes = new ArrayList<>();

        public List<Currency> getDividendQuotes() {
            return mDividendQuotes;
        }
    }

    /**
     * Inner class - Quote response mapping
     */
    private class Quote {

        @SerializedName("rate")
        private Currency mDividendQuote;

        private Currency getDividendQuote() {
            return mDividendQuote;
        }
    }
}
