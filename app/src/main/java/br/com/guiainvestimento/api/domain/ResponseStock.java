package br.com.guiainvestimento.api.domain;

import com.google.gson.annotations.SerializedName;

import br.com.guiainvestimento.domain.StockQuote;

/**
 * Domain class that will map the response of a getStock request made to YAHOO Api.
 * This mapping will only be used for one symbol request, otherwise ResponseStocks should be used
 */
@SuppressWarnings("unused")
public class ResponseStock {
    /**
     * Get list of Stocks
     * @return - List of Stocks
     */
    public StockQuote getStockQuotes() {
        return null;
    }
}
