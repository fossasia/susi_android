package org.fossasia.susi.ai.rest.responses.susi

data class GetRatingByUserResponse(
    val ratings: Ratings? = null,
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String = ""
)