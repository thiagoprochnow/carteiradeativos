package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.api.domain.ResponseCurrencyBackup;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface CurrencyService {
    String BASE_URL = "https://www.alphavantage.co";

    @GET("/query")
    Call<ResponseCurrencyBackup> getCurrencyQuote(@Query("function") String function, @Query("from_currency") String from, @Query("to_currency") String to, @Query("apikey") String key);

    // Add here other API requests
}