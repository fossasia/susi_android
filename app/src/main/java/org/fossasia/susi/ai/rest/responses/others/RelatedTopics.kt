package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse related topics object in retrofit response from websearch client.</h1>
 *
 * Created by mayank on 09-12-2016.
 */

data class RelatedTopics(
    @SerializedName("FirstURL")
    var url: String? = null,
    @SerializedName("Text")
    var text: String? = null,
    @SerializedName("Icon")
    var icon: WebIcon? = null,
    @SerializedName("Result")
    var result: String? = null
)
