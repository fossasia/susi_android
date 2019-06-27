package org.fossasia.susi.ai.dataclasses

data class DeleteFeedbackQuery(
    val model: String,
    val group: String,
    val language: String,
    val skill: String,
    val access_token: String
)