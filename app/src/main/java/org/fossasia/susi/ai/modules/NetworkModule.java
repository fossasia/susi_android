package org.fossasia.susi.ai.modules;

import org.fossasia.susi.ai.rest.SusiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saurabh on 10/10/16.
 */
@Module
public class NetworkModule {

    @Provides
    @Singleton
    public SusiService getSusiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.asksusi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SusiService.class);
    }
}
