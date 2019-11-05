package org.fossasia.susi.ai.rest.responses.susi

data class ListSkillMetricsResponse(
    val session: Session? = null,
    val accepted: Boolean = false,
    val message: String = "",
    val group: String = "",
    val model: String = "",
    val language: String = "",
    val metrics: Metrics? = null
)
