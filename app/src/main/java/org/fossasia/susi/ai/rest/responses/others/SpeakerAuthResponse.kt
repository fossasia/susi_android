package org.fossasia.susi.ai.rest.responses.others

data class SpeakerAuthResponse(
    val auth: String,
    val authentication: String,
    val email: String,
    val password: String? = null
)
