package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * <h1>Kotlin Data class to parse retrofit response from susi client.</h1>
 */

class SusiResponse {

    @SerializedName("client_id")
    @Expose
    val clientId: String? = null

    @SerializedName("query")
    @Expose
    val query: String? = null

    @SerializedName("query_date")
    @Expose
    val queryDate: String? = null

    @SerializedName("answer_date")
    @Expose
    val answerDate: String? = null

    @SerializedName("answer_time")
    @Expose
    val answerTime: Int = 0

    @SerializedName("count")
    @Expose
    val count: Int = 0

    @SerializedName("answers")
    @Expose
    val answers: List<Answer> = ArrayList()

    @SerializedName("session")
    @Expose
    val session: Session? = null

}
