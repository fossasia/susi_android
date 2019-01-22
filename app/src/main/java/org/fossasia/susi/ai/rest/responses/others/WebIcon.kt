package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse web icon object in retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */

data class WebIcon(
    @SerializedName("URL")
    var url: String? = null
)
