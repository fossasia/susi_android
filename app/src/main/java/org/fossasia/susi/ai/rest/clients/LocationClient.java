package org.fossasia.susi.ai.rest.clients;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * <h1>Class to get retrofit client to get location of user using his public ip address.</h1>
 *
 * Created by chiragw15 on 6/12/16.
 */
public class LocationClient {
    private static final String BASE_URL = "http://ipinfo.io";
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
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
