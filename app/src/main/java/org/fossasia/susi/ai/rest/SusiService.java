package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.rest.model.LoginResponse;
import org.fossasia.susi.ai.rest.model.SignUpResponse;
import org.fossasia.susi.ai.rest.model.SusiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by saurabh on 1/10/16.
 */

public interface SusiService {
    @GET("/susi/chat.json")
    Call<SusiResponse> getSusiResponse(@Query("q") String query);

    @POST("/aaa/signup.json")
    Call<SignUpResponse> signUp(@Query("signup") String email,
                                @Query("password") String password);

    @POST("/aaa/login.json?type=access-token")
    Call<LoginResponse> login(@Query("login") String email,
                              @Query("password") String password);
}
