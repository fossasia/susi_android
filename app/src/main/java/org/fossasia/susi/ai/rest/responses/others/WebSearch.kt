package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */

data class WebSearch(
    @SerializedName("Heading")
    var heading: String? = null,
    @SerializedName("RelatedTopics")
    var relatedTopics: List<RelatedTopics>? = null
)
