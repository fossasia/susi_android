package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.Expose

/**
 * <h1>Kotlin Data class to parse retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */

class WebSearch {


 @Expose
 var heading: String? = null


 @Expose
 var relatedTopics: List<RelatedTopics>? = null

}
