package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse meta data object in retrofit response from susi client.</h1>
 */

class Metadata {

    @SerializedName("count")
    @Expose
    val count: Int = 0

}
