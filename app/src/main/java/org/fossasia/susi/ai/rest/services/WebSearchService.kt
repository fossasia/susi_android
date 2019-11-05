package org.fossasia.susi.ai.rest.services

import org.fossasia.susi.ai.rest.responses.others.WebSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * <h1>Retrofit service to get websearch results.</h1>
 */
interface WebSearchService {

    /**
     * Gets .
     *
     * @param query the query
     * @return the
     */
    @GET("/?format=json&pretty=1")
    fun getResult(@Query("q") query: String): Call<WebSearch>
}
