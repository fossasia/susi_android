package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from login endpoint susi client.</h1>

 * Created by saurabh on 12/10/16.
 */

class LoginResponse {

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("session")
    @Expose
    val session: Session? = null

    @SerializedName("valid_seconds")
    @Expose
    val validSeconds: Long = 0

    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null
        internal set

}
