package org.fossasia.susi.ai.rest.interceptors;

import org.fossasia.susi.ai.helper.PrefManager;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * <h1>The type Token interceptor.</h1>
 * <h2>Interceptor to append access token to the request(if exists) on runtime.</h2>
 *
 * @author saurabh
 */
public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (PrefManager.getToken() != null) {
            HttpUrl url = chain.request().url();
            HttpUrl newUrl = url.newBuilder().addQueryParameter("access_token", PrefManager.getToken()).build();
            return chain.proceed(chain.request().newBuilder().url(newUrl).build());
        }
        return chain.proceed(chain.request());
    }
}
