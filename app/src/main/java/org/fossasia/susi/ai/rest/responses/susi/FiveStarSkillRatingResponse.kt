package org.fossasia.susi.ai.rest.responses.susi

data class FiveStarSkillRatingResponse(
    val ratings: Stars? = null,
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String = ""
)
