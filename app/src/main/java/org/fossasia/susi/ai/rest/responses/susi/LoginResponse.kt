package org.fossasia.susi.ai.rest.responses.susi


/**
 * <h1>Kotlin Data class to parse retrofit response from login endpoint susi client.</h1>

 * Created by saurabh on 12/10/16.
 */

class LoginResponse {

    val message: String? = null

    val session: Session? = null


    val validSeconds: Long = 0


    var accessToken: String? = null
        internal set

}
