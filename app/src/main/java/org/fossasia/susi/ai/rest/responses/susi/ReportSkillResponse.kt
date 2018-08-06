package org.fossasia.susi.ai.rest.responses.susi

/**
 * @author : codedsun
 * Created on 06/08/18
 */
/**
 * Response received on successful report of skill
 */
data class ReportSkillResponse(
        val feedback: String,
        val accepted: String,
        val message: String)