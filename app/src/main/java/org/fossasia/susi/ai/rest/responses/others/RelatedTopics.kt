package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>POJO class to parse related topics object in retrofit response from websearch client.</h1>

 * Created by mayank on 09-12-2016.
 */

class RelatedTopics

(@SerializedName("FirstURL")
 @Expose
 var url: String?,

 @SerializedName("Text")
 @Expose
 var text: String?,

 @SerializedName("Icon")
 @Expose
 var icon: WebIcon?) {

    @SerializedName("Result")
    @Expose
    var result: String? = null
}
