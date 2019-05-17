package org.fossasia.susi.ai.rest.responses.others

/**
 * <h1>Kotlin Data class to parse retrofit response from location client.</h1>
 *
 * Created by chiragw15 on 6/12/16.
 */

data class LocationResponse(
    var ip: String? = null,
    var hostname: String? = null,
    var city: String? = null,
    var region: String? = null,
    var country: String? = null,
    var loc: String = "",
    var org: String? = null,
    var postal: String? = null
)
