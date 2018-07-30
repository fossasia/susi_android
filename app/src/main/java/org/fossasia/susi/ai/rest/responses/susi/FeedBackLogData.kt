package org.fossasia.susi.ai.rest.responses.susi


data class FeedBackLogData(
        val model: String? = null,
        val group: String? = null,
        val language: String? = null,
        val skill: String? = null,
        val feedback: String? = null,
        val userQuery: String? = null,
        val susiReply: String? = null,
        val countryName: String? = null,
        val countryCode: String? = null,
        val deviceType: String? = null
)