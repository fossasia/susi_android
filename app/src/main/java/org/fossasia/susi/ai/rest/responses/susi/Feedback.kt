package org.fossasia.susi.ai.rest.responses.susi

/**
 *
 * @author arundhati24
 */
data class Feedback(
        var feedback: String? = "",
        var email: String? = "",
        var timestamp: String? = ""
)