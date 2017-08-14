package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.api.domain.ResponseFii;
import br.com.guiainvestimento.api.domain.ResponseFiis;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface FiiService {
    String BASE_URL = "https://query.yahooapis.com";

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseFii> getFii(@Query("q") String query);

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseFiis> getFiis(@Query("q") String query);

    // Add here other API requests
}