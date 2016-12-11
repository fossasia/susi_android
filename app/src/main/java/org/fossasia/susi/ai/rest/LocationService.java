package org.fossasia.susi.ai.rest;

import org.fossasia.susi.ai.rest.model.LocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by chiragw15 on 6/12/16.
 */

public interface LocationService {
    @GET("json")
    Call<LocationResponse> getLocationUsingIP();
}
