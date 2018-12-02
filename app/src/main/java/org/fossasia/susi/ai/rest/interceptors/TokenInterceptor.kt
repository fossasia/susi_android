package org.fossasia.susi.ai.rest.interceptors

import org.fossasia.susi.ai.helper.PrefManager

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

/**
 * <h1>The type Token interceptor.</h1>
 * <h2>Interceptor to append access token to the request(if exists) on runtime.</h2>
 */
class TokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (PrefManager.token != null) {
            val url = chain.request().url()
            val newUrl = url.newBuilder().addQueryParameter("access_token", PrefManager.token).build()
            return chain.proceed(chain.request().newBuilder().url(newUrl).build())
        }
        return chain.proceed(chain.request())
    }
}
