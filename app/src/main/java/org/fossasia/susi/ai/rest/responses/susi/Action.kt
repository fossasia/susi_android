package org.fossasia.susi.ai.rest.responses.susi

/**
 * <h1>Kotlin Data class to parse action types in retrofit response from susi client.</h1>
 */

class Action (

    val delay: Long = 0,

    val expression: String = "",

    val type: String = "answer",

    val link: String? = null,

    val text: String? = null,

    val query: String = "",

    val latitude: Double = 0.toDouble(),

    val longitude: Double = 0.toDouble(),

    val zoom: Double = 0.toDouble(),

    val count: Int = 0

)
