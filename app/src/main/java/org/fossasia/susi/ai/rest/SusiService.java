package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.rest.model.LoginResponse;
import org.fossasia.susi.ai.rest.model.SignUpResponse;
import org.fossasia.susi.ai.rest.model.SusiBaseUrls;
import org.fossasia.susi.ai.rest.model.SusiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by saurabh on 1/10/16.
 */

public interface SusiService {

    @GET(BaseUrl.SUSI_SERVICES_URL + "/config_susi.json")
    Call<SusiBaseUrls> getSusiBaseUrls();

    @GET("/susi/chat.json")
    Call<SusiResponse> getSusiResponse(@Query("q") String query,
                                       @Query("timezoneOffset") int timezoneOffset);

    @POST("/aaa/signup.json")
    Call<SignUpResponse> signUp(@Query("signup") String email,
                                @Query("password") String password);

    @POST("/aaa/login.json?type=access-token")
    Call<LoginResponse> login(@Query("login") String email,
                              @Query("password") String password);
}
