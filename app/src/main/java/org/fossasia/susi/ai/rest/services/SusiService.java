package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.clients.BaseUrl;
import org.fossasia.susi.ai.rest.responses.susi.ForgotPasswordResponse;
import org.fossasia.susi.ai.rest.responses.susi.LoginResponse;
import org.fossasia.susi.ai.rest.responses.susi.SignUpResponse;
import org.fossasia.susi.ai.rest.responses.susi.SusiBaseUrls;
import org.fossasia.susi.ai.rest.responses.susi.SusiResponse;
import org.fossasia.susi.ai.rest.responses.susi.MemoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface SusiService {

    @GET(BaseUrl.SUSI_SERVICES_URL + "/config_susi.json")
    Call<SusiBaseUrls> getSusiBaseUrls();

    @GET("/susi/chat.json")
    Call<SusiResponse> getSusiResponse(@Query("timezoneOffset") int timezoneOffset,
                                       @Query("longitude") double longitude,
                                       @Query("latitude") double latitude,
                                       @Query("geosource") String geosource,
                                       @Query("language") String language,
                                       @Query("q") String query);

    @GET("/susi/memory.json")
    Call<MemoryResponse> getChatHistory();

    @POST("/aaa/signup.json")
    Call<SignUpResponse> signUp(@Query("signup") String email,
                                @Query("password") String password);

    @POST("/aaa/login.json?type=access-token")
    Call<LoginResponse> login(@Query("login") String email,
                              @Query("password") String password);

    @POST("/aaa/recoverpassword.json")
    Call<ForgotPasswordResponse> forgotPassword(@Query("forgotemail") String email);
}
