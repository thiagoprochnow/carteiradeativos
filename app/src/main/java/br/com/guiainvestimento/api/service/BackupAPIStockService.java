package br.com.guiainvestimento.api.service;

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
public interface BackupAPIStockService {
    String BASE_URL = "http://datafeed.bolsafinanceira.com";

    @GET("/cgi-bin/quote.cgi")
    Call<String> getStockBackupAPI(@Query("token") String token, @Query("symbol") String symbol);

    // Add here other API requests
}