package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * <h1>Kotlin Data class to parse retrofit response from susi client.</h1>
 */

class SusiResponse (

    val clientId: String? = null,
    val query: String = "",
    val queryDate: String = "",
    val answerDate: String = "",
    val answerTime: Int = 0,
    val count: Int = 0,
    val answers: List<Answer> = ArrayList(),
    val session: Session? = null

    )
