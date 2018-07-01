package org.fossasia.susi.ai.rest.clients;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceClient {
    public static final String SUSI_SPEAKER_BASE_URL = "http://192.168.0.5:5000";
    private static Retrofit retrofit = null;

    /**
     * Gets client.
     *
     * @return the client
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SUSI_SPEAKER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
