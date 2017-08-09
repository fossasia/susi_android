package org.fossasia.susi.ai.rest.clients

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 * Created by chiragw15 on 14/8/17.
 */
class hotwordTrainingClient {
    val BASE_URL = "https://snowboy.kitt.ai"
    lateinit var retrofit: Retrofit

    fun getClient(): Retrofit {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit
    }
}