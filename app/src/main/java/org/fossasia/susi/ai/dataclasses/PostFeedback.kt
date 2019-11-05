package org.fossasia.susi.ai.dataclasses

/**
 *
 * Created by arundhati24 on 03/07/2018
 */
data class PostFeedback(
    val model: String,
    val group: String,
    val language: String,
    val skill: String,
    val feedback: String,
    val accessToken: String
)
