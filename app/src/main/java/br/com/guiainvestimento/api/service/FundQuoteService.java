package br.com.guiainvestimento.api.service;

import java.util.List;

import br.com.guiainvestimento.domain.FundQuote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface responsible for detailing the GET's URL
 */
public interface FundQuoteService {
    String BASE_URL = "http://35.199.123.90";

    @GET("getfundquotes/{cnpj}/{data}")
    Call<List<FundQuote>> getFundQuotes(@Path(value="cnpj", encoded=true) String cnpj, @Path("data") String data);
}