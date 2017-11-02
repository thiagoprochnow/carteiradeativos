package br.com.guiainvestimento.api.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface BackupAPIFiiService {
    String BASE_URL = "http://datafeed.bolsafinanceira.com";

    @GET("/cgi-bin/quote.cgi")
    Call<String> getFiiBackupAPI(@Query("token") String token, @Query("symbol") String symbol);

    // Add here other API requests
}