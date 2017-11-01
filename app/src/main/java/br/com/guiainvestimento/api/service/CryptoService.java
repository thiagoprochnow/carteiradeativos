package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.api.domain.ResponseCrypto;
import br.com.guiainvestimento.api.domain.ResponseCurrency;
import br.com.guiainvestimento.api.domain.ResponseStock;
import br.com.guiainvestimento.api.domain.ResponseStockIncome;
import br.com.guiainvestimento.api.domain.ResponseStocks;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface CryptoService {
    String BASE_URL = "https://www.mercadobitcoin.net";

    @GET("/api/{crypto}/ticker/")
    Call<ResponseCrypto> getCrypto(@Path("crypto") String crypto);

}