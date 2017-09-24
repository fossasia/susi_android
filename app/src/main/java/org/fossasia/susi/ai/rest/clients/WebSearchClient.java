package org.fossasia.susi.ai.rest.clients;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <h1>Class to get retrofit client to get websearch results.</h1>
 *
 * Created by mayank on 12-12-2016.
 */
public class WebSearchClient {

    private static final String BASE_URL = "http://api.duckduckgo.com";
    private static Retrofit retrofit = null;

    /**
     * Gets client.
     *
     * @return the client
     */
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}