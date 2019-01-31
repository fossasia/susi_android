package org.fossasia.susi.ai.rest.responses.susi

import java.util.ArrayList

data class Answer(
    var data: List<Map<String, String>> = ArrayList(),
    val metadata: Metadata? = null,
    val actions: List<Action> = ArrayList(),
    val skills: List<String> = ArrayList()
)
