package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import java.util.*

/**
 * <h1>Kotlin Data class to parse retrofit response from susi client.</h1>
 */

class SusiResponse {


    @Expose
    val clientId: String? = null


    @Expose
    val query: String = ""


    @Expose
    val queryDate: String = ""


    @Expose
    val answerDate: String = ""


    @Expose
    val answerTime: Int = 0


    @Expose
    val count: Int = 0


    @Expose
    val answers: List<Answer> = ArrayList()


    @Expose
    val session: Session? = null

}
