package org.fossasia.susi.ai.rest.responses.susi

import java.io.Serializable

/**
 *
 * @author arundhati24
 */
class Feedback : Serializable {
    var feedback: String? = ""
    var email: String? = ""
    var timestamp: String? = ""
}