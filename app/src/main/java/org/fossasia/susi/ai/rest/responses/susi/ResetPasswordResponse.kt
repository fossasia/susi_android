package org.fossasia.susi.ai.rest.responses.susi

/**
 * Created by meeera on 4/8/17.
 */
data class ResetPasswordResponse(
        val session: Session? = null,
        val accepted: Boolean = false,
        val message: String? = null
)