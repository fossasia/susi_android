package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from location client.</h1>
 *
 * Created by chiragw15 on 6/12/16.
 */

class LocationResponse {

    @SerializedName("ip")
    var ip: String? = null

    @SerializedName("hostname")
    var hostname: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("region")
    var region: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("loc")
    var loc: String = ""

    @SerializedName("org")
    var org: String? = null

    @SerializedName("postal")
    var postal: String? = null
}
