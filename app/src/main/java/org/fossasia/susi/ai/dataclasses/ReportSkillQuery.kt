package org.fossasia.susi.ai.dataclasses

/**
 * @author : codedsun
 * Created on 04/09/18
 */
data class ReportSkillQuery(
    val model: String,
    val group: String,
    val skill: String,
    val feedback: String,
    val accessToken: String
)
