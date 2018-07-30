package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

data class FeedBackLogResponse(
        val model: String? = null,
        val group: String? = null,
        val language: String? = null,
        val skill: String? = null,
        val feedback: String? = null,
        @SerializedName("user_query")
        val userQuery: String? = null,
        @SerializedName("susi_reply")
        val susiReply: String? = null,
        @SerializedName("country_name")
        val countryName: String? = null,
        @SerializedName("country_code")
        val countryCode: String? = null,
        @SerializedName("device_type")
        val deviceType: String? = null
)