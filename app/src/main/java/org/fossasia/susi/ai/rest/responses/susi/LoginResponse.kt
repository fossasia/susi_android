package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose

/**
 * <h1>Kotlin Data class to parse retrofit response from login endpoint susi client.</h1>

 * Created by saurabh on 12/10/16.
 */

class LoginResponse {


    @Expose
    val message: String? = null


    @Expose
    val session: Session? = null


    @Expose
    val validSeconds: Long = 0


    @Expose
    var accessToken: String? = null
        internal set

}
