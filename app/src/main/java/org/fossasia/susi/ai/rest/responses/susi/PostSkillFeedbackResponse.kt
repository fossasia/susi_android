package org.fossasia.susi.ai.rest.responses.susi

/**
 *
 * Created by arundhati24 on 25/06/2018
 */
data class PostSkillFeedbackResponse (
    val feedback: String? = "",
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String = ""
)