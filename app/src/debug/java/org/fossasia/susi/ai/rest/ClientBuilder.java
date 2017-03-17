package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //Must maintain the order of interceptors here, logging needs to be last.
        httpClient.addInterceptor(new TokenInterceptor());
        httpClient.addInterceptor(logging);

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(PrefManager.getSusiRunningBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
            init();
        } catch (IllegalArgumentException e ){
            e.printStackTrace();
        }
    }

    public SusiService getSusiApi() {
        return susiService;
    }
}
