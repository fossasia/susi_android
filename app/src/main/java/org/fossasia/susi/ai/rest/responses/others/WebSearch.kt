package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>POJO class to parse retrofit response from websearch client.</h1>

 * Created by mayank on 12-12-2016.
 */
class WebSearch
/**
 * Instantiates a new Web search.

 * @param heading       the heading
 * *
 * @param relatedTopics the related topics
 */
(@SerializedName("Heading")
 @Expose
 /**
  * Gets heading.

  * @return the heading
  */
 /**
  * Sets heading.

  * @param heading the heading
  */
 var heading: String?, @SerializedName("RelatedTopics")
 @Expose
 /**
  * Gets related topics.

  * @return the related topics
  */
 /**
  * Sets related topics.

  * @param relatedTopics the related topics
  */
 var relatedTopics: List<RelatedTopics>?)
