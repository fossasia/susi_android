package org.fossasia.susi.ai.rest.responses.others


/**
 * <h1>Kotlin Data class to parse retrofit response from websearch client.</h1>
 *
 * Created by mayank on 12-12-2016.
 */

class WebSearch (

 var heading: String? = null,
 var relatedTopics: List<RelatedTopics>? = null

)
