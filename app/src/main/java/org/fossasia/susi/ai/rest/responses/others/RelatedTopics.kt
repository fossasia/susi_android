package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>POJO class to parse related topics object in retrofit response from websearch client.</h1>

 * Created by mayank on 09-12-2016.
 */
class RelatedTopics
/**
 * Instantiates a new Related topics.

 * @param url  the url
 * *
 * @param text the text
 * *
 * @param icon the icon
 */
(@SerializedName("FirstURL")
 @Expose
 /**
  * Gets url.

  * @return the url
  */
 /**
  * Sets url.

  * @param url the url
  */
 var url: String?, @SerializedName("Text")
 @Expose
 /**
  * Gets text.

  * @return the text
  */
 /**
  * Sets text.

  * @param text the text
  */
 var text: String?, @SerializedName("Icon")
 @Expose
 /**
  * Gets icon.

  * @return the icon
  */
 /**
  * Sets icon.

  * @param icon the icon
  */
 var icon: WebIcon?) {

    /**
     * Gets result.

     * @return the result
     */
    /**
     * Sets result.

     * @param result the result
     */
    @SerializedName("Result")
    @Expose
    var result: String? = null
}
