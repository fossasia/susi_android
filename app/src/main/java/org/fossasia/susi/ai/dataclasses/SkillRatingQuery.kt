package org.fossasia.susi.ai.dataclasses

/**
 *
 * Created by arundhati24 on 08/07/2018
 */
data class SkillRatingQuery(
    val model: String = "",
    val group: String = "",
    val language: String = "",
    val skill: String = "",
    val rating: String = ""
)
