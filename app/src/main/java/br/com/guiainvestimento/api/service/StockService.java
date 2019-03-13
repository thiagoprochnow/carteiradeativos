package br.com.guiainvestimento.api.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface StockService {
    String BASE_URL = "http://www.alphavantage.co";

    @GET("/query")
    Call<String> getStock(@Query("function") String function, @Query("symbol") String symbol, @Query("apikey") String apiKey);
}