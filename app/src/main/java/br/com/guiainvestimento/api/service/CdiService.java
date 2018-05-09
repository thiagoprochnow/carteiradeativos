package br.com.guiainvestimento.api.service;

import java.util.List;

import br.com.guiainvestimento.domain.Cdi;
import br.com.guiainvestimento.domain.Ipca;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface CdiService {
    String BASE_URL = "http://35.199.123.90";

    @GET("getcdi/{timestamp}")
    Call<List<Cdi>> getCdi(@Path("timestamp") String timestamp);

    @GET("getipca")
    Call<List<Ipca>> getIpca();
}