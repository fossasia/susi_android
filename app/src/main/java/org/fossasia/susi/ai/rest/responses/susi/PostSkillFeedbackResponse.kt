package org.fossasia.susi.ai.rest.responses.susi

data class PostSkillFeedbackResponse(
    val feedback: String? = "",
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String = ""
)
