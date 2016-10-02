package org.fossasia.susi.ai.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saurabh on 1/10/16.
 * <p>
 * Singleton class to get Susi client.
 */

public class ClientBuilder {
    private static SusiService service;

    private ClientBuilder() {

    }

    public static SusiService getSusiService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.asksusi.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(SusiService.class);
        }
        return service;
    }
}
