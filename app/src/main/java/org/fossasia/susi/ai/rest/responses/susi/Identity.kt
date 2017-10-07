package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 * <h1>Kotlin Data class to parse identity object in retrofit response from susi client.</h1>
 */

class Identity {


    @Expose
    val name: String? = null


    @Expose
    val type: String? = null


    @Expose
    val anonymous: Boolean = false
}
