package br.com.guiainvestimento.api.service;

import br.com.guiainvestimento.domain.TreasuryQuote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface TreasuryService {
    String BASE_URL = "http://35.199.123.90";

    @GET("gettesouro/{id}")
    Call<TreasuryQuote> getTreasury(@Path("id") String id);
}