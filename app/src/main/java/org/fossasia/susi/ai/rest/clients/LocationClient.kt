package org.fossasia.susi.ai.rest.clients

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * <h1>Class to get retrofit client to get location of user using his public ip address.</h1>
 */
object LocationClient {
    private const val BASE_URL = "https://ipinfo.io"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}
