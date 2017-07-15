package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */

class WebSearch {

 @SerializedName("Heading")
 @Expose
 var heading: String? = null

 @SerializedName("RelatedTopics")
 @Expose
 var relatedTopics: List<RelatedTopics>? = null

}
