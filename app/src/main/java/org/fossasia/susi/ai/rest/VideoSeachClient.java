package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.helper.Config;
import org.fossasia.susi.ai.rest.model.VideoSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mayanktripathi on 30/01/17.
 */

public interface VideoSeachClient {

    @GET("/youtube/v3/search?part=snippet&maxResults=1&order=relevance&key="+ Config.YOUTUBE_API_KEY)
    Call<VideoSearch> getVideo(@Query("q") String query);
}
