package br.com.guiainvestimento.api.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.domain.Fund;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class FundCnpjService {
    private Context context;

    public static final String BASE_URL = "http://35.199.123.90/";
    private final String username = "mainuser";
    private final String password = "user1133";
    private static Retrofit retrofit = null;

    public FundCnpjService(Context contextIn){
        context = contextIn;

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        OkHttpClient mClient = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(username, password))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build();
    }

    public interface FundService {
        @GET("getfundcnpj/{symbol}")
        Call<List<Fund>> getFundData(@Path("symbol") String symbol);
    }

    public void getFundData(String symbol){

        retrofit.create(FundService.class).getFundData(symbol)
                .enqueue(new Callback<List<Fund>>() {

                    @Override
                    public void onResponse(Call<List<Fund>> call,
                                           Response<List<Fund>> response) {
                        List<String> str = new ArrayList<String>();
                        if(response.isSuccessful()) {
                            List<Fund> funds = response.body();
                            if(funds != null && funds.size() > 0) {
                                for (Fund s : funds) {
                                    str.add(s.getCnpj() + "\r\n" + s.getNome());
                                }
                            } else {
                                str.add("");
                            }
                        } else {
                            str.add("");
                        }

                        AutoCompleteTextView fundNameView =
                                (AutoCompleteTextView)((Activity)context).findViewById(R.id.inputCnpj);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                R.layout.dropdown_custom, str.toArray(new String[0]));
                        fundNameView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<Fund>> call, Throwable t) {
                        Log.d("aaa","bbb");
                    }
                });
    }


    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials)
                    .header("Accept", "applicaton/json")
                    .build();
            return chain.proceed(authenticatedRequest);
        }
    }
}