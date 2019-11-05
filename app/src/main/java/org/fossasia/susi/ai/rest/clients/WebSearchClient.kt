package org.fossasia.susi.ai.rest.clients

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * <h1>Class to get retrofit client to get websearch results.</h1>
 */
object WebSearchClient {

    private const val BASE_URL = "https://api.duckduckgo.com"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}
