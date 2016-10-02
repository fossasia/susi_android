package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.rest.model.SusiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by saurabh on 1/10/16.
 */

public interface SusiService {
    @GET("/susi/chat.json")
    Call<SusiResponse> getSusiResponse(@Query("q") String query);
}
