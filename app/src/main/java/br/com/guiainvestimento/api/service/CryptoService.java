package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.api.domain.ResponseCrypto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface CryptoService {
    String BASE_URL = "https://www.mercadobitcoin.net";

    @GET("/api/{crypto}/ticker/")
    Call<ResponseCrypto> getCrypto(@Path("crypto") String crypto);

}