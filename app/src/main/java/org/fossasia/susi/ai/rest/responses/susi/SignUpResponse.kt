package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from sign up endpoint susi client.</h1>

 * Created by saurabh on 12/10/16.
 */

class SignUpResponse {

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("session")
    @Expose
    val session: Session? = null

}
