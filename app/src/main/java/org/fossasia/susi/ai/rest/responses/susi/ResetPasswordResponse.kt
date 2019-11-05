package org.fossasia.susi.ai.rest.responses.susi

data class ResetPasswordResponse(
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String? = null
)
