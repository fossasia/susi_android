package org.fossasia.susi.ai.rest.responses.susi

import java.util.ArrayList

/**
 * <h1>Kotlin Data class to parse answer in retrofit response from susi client.</h1>
 */

data class Answer(

        var data: List<Map<String, String>> = ArrayList(),

        val metadata: Metadata? = null,

        val actions: List<Action> = ArrayList(),

        val skills: List<String> = ArrayList()
)
