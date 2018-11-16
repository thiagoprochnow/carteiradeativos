package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.api.domain.ResponseCurrencyBackup;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface CurrencyService {
    String BASE_URL = "http://api.promasters.net.br";

    @GET("/cotacao/v1/valores")
    Call<ResponseCurrencyBackup> getCurrencyQuote(@Query("moedas") String currency, @Query("alt") String alt);

    // Add here other API requests
}