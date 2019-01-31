package org.fossasia.susi.ai.rest.responses.susi

data class ReportSkillResponse(
    val feedback: String,
    val accepted: String,
    val message: String
)