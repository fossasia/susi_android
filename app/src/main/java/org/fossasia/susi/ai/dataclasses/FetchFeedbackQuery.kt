package org.fossasia.susi.ai.dataclasses

/**
 *
 * Created by arundhati24 on 05/07/2018
 */
data class FetchFeedbackQuery(
        val model: String,
        val group: String,
        val language: String,
        val skill: String
)