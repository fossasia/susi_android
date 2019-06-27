package org.fossasia.susi.ai.rest.responses.susi

data class DeleteFeedbackResponse(
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String? = null
)