package org.fossasia.susi.ai.rest

import org.fossasia.susi.ai.helper.PrefManager
import org.fossasia.susi.ai.rest.interceptors.TokenInterceptor

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        //Must maintain the order of interceptors here, logging needs to be last.
        httpClient.addInterceptor(TokenInterceptor())
        httpClient.addInterceptor(logging)

        Retrofit.Builder()
                .baseUrl(PrefManager.susiRunningBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
    }
}
