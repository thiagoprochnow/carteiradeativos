package br.com.guiainvestimento.api.service;

import java.util.List;

import br.com.guiainvestimento.domain.FiiQuote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface FiiService {
    String BASE_URL = "http://www.alphavantage.co";

    @GET("/query")
    Call<String> getFii(@Query("function") String function, @Query("symbol") String symbol, @Query("apikey") String apiKey);
}