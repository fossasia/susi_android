package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 * <h1>Kotlin Data class to parse action types in retrofit response from susi client.</h1>
 */

class Action {


    @Expose
    val delay: Long = 0


    @Expose
    val expression: String = ""


    @Expose
    val type: String = "answer"


    @Expose
    val anchorLink: String? = null


    @Expose
    val anchorText: String? = null


    @Expose
    val query: String = ""


    @Expose
    val latitude: Double = 0.toDouble()


    @Expose
    val longitude: Double = 0.toDouble()


    @Expose
    val zoom: Double = 0.toDouble()


    @Expose
    val count: Int = 0


    @Expose
    val language: String = "en"

}
