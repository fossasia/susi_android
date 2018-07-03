package org.fossasia.susi.ai.rest.clients;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceClient {
    private static Retrofit retrofit = null;

    /**
     * Gets client.
     *
     * @return the client
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            String SUSI_SPEAKER_BASE_URL = "http://10.0.0.1:5000";
            retrofit = new Retrofit.Builder()
                    .baseUrl(SUSI_SPEAKER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
