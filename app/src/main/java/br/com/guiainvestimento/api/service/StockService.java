package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.domain.StockQuote;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface StockService {
    String BASE_URL = "http://webfeeder.cedrofinances.com.br";

    @POST("SignIn")
    Call<String> getConnection(@Query("login") String login, @Query("password") String password);

    @GET("services/quotes/quote/{symbol}")
    Call<StockQuote> getStock(@Path("symbol") String symbol);
}