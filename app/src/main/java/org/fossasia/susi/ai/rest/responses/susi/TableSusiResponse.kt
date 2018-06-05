package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class TableSusiResponse {

    @SerializedName("client_id")
    @Expose
    val clientId: String? = null

    @SerializedName("query")
    @Expose
    val query: String = ""

    @SerializedName("query_date")
    @Expose
    val queryDate: String = ""

    @SerializedName("answer_date")
    @Expose
    val answerDate: String = ""

    @SerializedName("answer_time")
    @Expose
    val answerTime: Int = 0

    @SerializedName("count")
    @Expose
    val count: Int = 0

    @SerializedName("answers")
    @Expose
    val answers: List<TableAnswer> = ArrayList()

    @SerializedName("session")
    @Expose
    val session: Session? = null
}