package org.fossasia.susi.ai.rest.responses.susi

/**
 * <h1>Kotlin Data class to parse identity object in retrofit response from susi client.</h1>
 */

class Identity (

    val name: String? = null,

    val type: String? = null,

    val anonymous: Boolean = false
)
