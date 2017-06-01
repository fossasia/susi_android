package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor;
import org.fossasia.susi.ai.rest.services.SusiService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saurabh on 1/10/16.
 * <p>
 * Singleton class to get Susi client.
 */

public class ClientBuilder {

    private static Retrofit retrofit;
    private static SusiService susiService;

    public ClientBuilder() {
        createSusiService();
    }

    private static void init() {
        susiService = createApi(SusiService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static void createSusiService() {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new TokenInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(PrefManager.getSusiRunningBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        init();
    }

    public SusiService getSusiApi() {
        return susiService;
    }
}
