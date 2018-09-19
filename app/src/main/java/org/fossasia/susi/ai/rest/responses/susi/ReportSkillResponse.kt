package org.fossasia.susi.ai.rest.responses.susi

/**
 * Response received on reporting a skill successfully
 *
 * @author : codedsun
 * Created on 04/09/18
 */
data class ReportSkillResponse(
        val feedback: String,
        val accepted: String,
        val message: String)