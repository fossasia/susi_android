package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from login endpoint susi client.</h1>

 * Created by saurabh on 12/10/16.
 */

class LoginResponse {

    val message: String? = null

    val session: Session? = null

    val valid_seconds: Long = 0

    var access_token: String? = null
        internal set

}
