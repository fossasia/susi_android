package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.responses.others.WebSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * <h1>Retrofit service to get websearch results.</h1>
 *
 * Created by mayank on 12-12-2016.
 */
public interface WebSearchService {

    /**
     * Gets .
     *
     * @param query the query
     * @return the
     */
    @GET("/?format=json&pretty=1")
    Call<WebSearch> getresult(@Query("q") String query);
}
