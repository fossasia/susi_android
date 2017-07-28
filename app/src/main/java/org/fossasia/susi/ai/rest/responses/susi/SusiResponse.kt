package org.fossasia.susi.ai.rest.responses.susi

import java.util.ArrayList

/**
 * <h1>Kotlin Data class to parse retrofit response from susi client.</h1>
 */

class SusiResponse (

    val client_id: String? = null,

    val query: String = "",

    val query_date: String = "",

    val answer_date: String = "",

    val answer_time: Int = 0,

    val count: Int = 0,

    val answers: List<Answer> = ArrayList(),

    val session: Session? = null

)
