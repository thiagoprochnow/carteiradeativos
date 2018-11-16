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
    String BASE_URL = "http://webfeeder.cedrofinances.com.br";

    @POST("SignIn")
    Call<String> getConnection(@Query("login") String login, @Query("password") String password);

    @GET("services/quotes/quote/{symbol}")
    Call<FiiQuote> getFii(@Path("symbol") String symbol);

    @GET("services/quotes/quote/{symbol}")
    Call<List<FiiQuote>> getFiis(@Path(value="symbol", encoded = true) String symbol);
}