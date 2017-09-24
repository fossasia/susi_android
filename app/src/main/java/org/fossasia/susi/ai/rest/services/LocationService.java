package org.fossasia.susi.ai.rest.services;

import org.fossasia.susi.ai.rest.responses.others.LocationResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * <h1>Retrofit service to get location of user using his/her public ip address.</h1>
 *
 * Created by chiragw15 on 6/12/16.
 */
public interface LocationService {
    /**
     * Gets location using ip.
     *
     * @return the location using ip
     */
    @GET("json")
    Call<LocationResponse> getLocationUsingIP();
}
