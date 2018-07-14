package org.fossasia.susi.ai.rest;

/**
 * @author : codedsun
 * Created on 08/07/18
 */

import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Class for Retorfit
 */
public class RetrofitInstance {

    private static RetrofitInstance retrofitInstance = null;
    private Retrofit retrofit;

    private RetrofitInstance() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //Must maintain the order of interceptors here, logging needs to be last.
        httpClient.addInterceptor(new TokenInterceptor());
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(PrefManager.getSusiRunningBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public static Retrofit getRetrofit() {
        if (retrofitInstance == null) {
            retrofitInstance = new RetrofitInstance();
        }
        return retrofitInstance.retrofit;
    }
}
