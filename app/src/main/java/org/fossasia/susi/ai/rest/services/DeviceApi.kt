package org.fossasia.susi.ai.rest.services

import org.fossasia.susi.ai.rest.responses.others.AddRoomResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerAuthResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerConfigResponse
import org.fossasia.susi.ai.rest.responses.others.SpeakerWifiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface DeviceApi {

    @GET("/wifi_credentials")
    fun wifiCredentials(
        @Query("wifissid") ssid: String,
        @Query("wifipassd") pass: String
    ): Call<SpeakerWifiResponse>

    @GET("/config")
    fun ttSSettings(@QueryMap query: Map<String, String>): Call<SpeakerConfigResponse>

    @GET("/auth")
    fun authCredentials(
        @Query("choice") choice: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<SpeakerAuthResponse>

    @GET("/speaker_config")
    fun roomDetails(
        @Query("room_name") room_name: String
    ): Call<AddRoomResponse>
}
