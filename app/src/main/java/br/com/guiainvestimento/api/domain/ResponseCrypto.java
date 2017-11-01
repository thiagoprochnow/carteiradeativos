package br.com.guiainvestimento.api.domain;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.domain.Crypto;
import br.com.guiainvestimento.domain.Currency;

@SuppressWarnings("unused")
public class ResponseCrypto {

    @SerializedName("ticker")
    private Crypto mResult;

    /**
     * Get list of Currencies attributes present in response
     * @return - List of Currencies
     */
    public List<Crypto> getCryptoQuote() {
        List<Crypto> result = new ArrayList<>();
        result.add(mResult);
        return result;
    }
}
