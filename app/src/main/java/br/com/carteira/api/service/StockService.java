package br.com.carteira.api.service;

import br.com.carteira.api.domain.ResponseStock;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface StockService {
    String BASE_URL = "https://query.yahooapis.com";

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseStock> getStock(@Query("q") String query);

    // Add here other API requests
}