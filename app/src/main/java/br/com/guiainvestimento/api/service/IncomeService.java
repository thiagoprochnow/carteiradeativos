package br.com.guiainvestimento.api.service;

import java.util.List;

import br.com.guiainvestimento.domain.Cdi;
import br.com.guiainvestimento.domain.Income;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface IncomeService {
    String BASE_URL = "http://35.199.123.90";

    @GET("getprovento/{symbol}/{timestamp}")
    Call<List<Income>> getProvento(@Path("symbol") String symbol, @Path("timestamp") String timestamp);
}