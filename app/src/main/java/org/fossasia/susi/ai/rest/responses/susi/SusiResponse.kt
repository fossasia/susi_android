package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class SusiResponse(
    @SerializedName("client_id")
    val clientId: String? = null,
    val query: String = "",
    @SerializedName("query_date")
    val queryDate: String = "",
    @SerializedName("answer_date")
    val answerDate: String = "",
    @SerializedName("answer_time")
    val answerTime: Int = 0,
    val count: Int = 0,
    val answers: List<Answer> = ArrayList(),
    val session: Session? = null
)
