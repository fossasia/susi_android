package org.fossasia.susi.ai.rest.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface DeviceService {

    @GET("/wifi_credentials/{wifissid}/{wifipassid}")
    Call<String> wifiCredentials(@Path("wifissid") String ssid,
                                     @Path("wifipassd") String pass);


    @GET("/config/{stt}/{tts}/{hotword}/{wake}")
    Call<String> ttSSettings(@Path("stt") String speechToText,
                                @Path("tts") String textToSpeech,
                                @Path("hotword") String hotword,
                                @Path("wake") String wake);


    @GET("/auth/{choice}/{email}/{password}")
    Call<String> authCredentials(@Path("choice") String choice,
                                     @Path("email") String email,
                                     @Path("password") String password);

}
