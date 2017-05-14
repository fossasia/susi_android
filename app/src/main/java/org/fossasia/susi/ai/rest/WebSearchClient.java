package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.rest.model.WebSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mayank on 12-12-2016.
 */

public interface WebSearchClient {

    @GET("/?format=json&pretty=1")
    Call<WebSearch> getresult(@Query("q") String query);
}
