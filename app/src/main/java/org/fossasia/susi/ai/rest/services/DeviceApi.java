package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.responses.others.SpeakerAuthResponse;
import org.fossasia.susi.ai.rest.responses.others.SpeakerConfigResponse;
import org.fossasia.susi.ai.rest.responses.others.SpeakerWifiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface DeviceApi {

    @GET("/wifi_credentials")
    Call<SpeakerWifiResponse> wifiCredentials(@Query("wifissid") String ssid,
                                              @Query("wifipassd") String pass);


    @GET("/config")
    Call<SpeakerConfigResponse> ttSSettings(@QueryMap Map<String, String> query);


    @GET("/auth")
    Call<SpeakerAuthResponse> authCredentials(@Query("choice") String choice,
                                              @Query("email") String email,
                                              @Query("password") String password);

}
