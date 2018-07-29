package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse action types in retrofit response from susi client.</h1>
 */

data class Action(

        val delay: Long = 0,

        val expression: String = "",

        val type: String = "",

        @SerializedName("link")
        val anchorLink: String? = null,

        @SerializedName("text")
        val anchorText: String? = null,

        val query: String = "",

        val latitude: Double = 0.toDouble(),

        val longitude: Double = 0.toDouble(),

        val zoom: Double = 0.toDouble(),

        val count: Int = 0,

        val language: String = "en",

        var columns: Map<String, Any>? = null,

        var identifier: String = ""
)
