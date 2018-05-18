package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.Expose

/**
 * <h1>Kotlin Data class to parse related topics object in retrofit response from websearch client.</h1>
 *
 * Created by mayank on 09-12-2016.
 */

class RelatedTopics {

    @Expose
    var url: String? = null

    @Expose
    var text: String? = null


    @Expose
    var icon: WebIcon? = null


    @Expose
    var result: String? = null
}
