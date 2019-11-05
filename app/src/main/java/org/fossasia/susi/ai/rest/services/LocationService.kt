package org.fossasia.susi.ai.rest.services

import org.fossasia.susi.ai.rest.responses.others.LocationResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * <h1>Retrofit service to get location of user using his/her public ip address.</h1>
 *
 */
interface LocationService {
    /**
     * Gets location using ip.
     *
     * @return the location using ip
     */
    @get:GET("json")
    val locationUsingIP: Call<LocationResponse>
}
