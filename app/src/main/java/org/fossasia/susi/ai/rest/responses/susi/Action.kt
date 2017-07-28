package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse action types in retrofit response from susi client.</h1>
 */

class Action {

    @SerializedName("delay")
    @Expose
    val delay: Long = 0

    @SerializedName("expression")
    @Expose
    val expression: String = ""

    @SerializedName("type")
    @Expose
    val type: String = "answer"

    @SerializedName("link")
    @Expose
    val anchorLink: String? = null

    @SerializedName("text")
    @Expose
    val anchorText: String? = null

    @SerializedName("query")
    @Expose
    val query: String = ""

    @SerializedName("latitude")
    @Expose
    val latitude: Double = 0.toDouble()

    @SerializedName("longitude")
    @Expose
    val longitude: Double = 0.toDouble()

    @SerializedName("zoom")
    @Expose
    val zoom: Double = 0.toDouble()

    @SerializedName("count")
    @Expose
    val count: Int = 0

    @SerializedName("language")
    @Expose
    val language: String = "en"

}
