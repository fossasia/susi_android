package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.helper.PrefManager;
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor;
import org.fossasia.susi.ai.rest.services.SusiService;

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

    /**
     * Instantiates a new Client builder.
     */
    public ClientBuilder() {
        createSusiService();
    }

    private static void init() {
        susiService = createApi(SusiService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    /**
     * Create susi service.
     */
    public static void createSusiService() {


        try {
            retrofit = RetrofitInstance.getRetrofit();
            susiService = getSusiApi();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets susi api.
     *
     * @return the susi api
     */
    public static SusiService getSusiApi() {
        if(susiService == null){
            init();
        }
        return susiService;
    }
}
