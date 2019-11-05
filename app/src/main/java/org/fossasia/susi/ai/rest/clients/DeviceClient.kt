package org.fossasia.susi.ai.rest.clients

import org.fossasia.susi.ai.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeviceClient {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BuildConfig.SPEAKER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
}
