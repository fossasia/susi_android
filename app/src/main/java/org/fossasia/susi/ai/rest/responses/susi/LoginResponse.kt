package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

/**
 * <h1>Kotlin Data class to parse retrofit response from login endpoint susi client.</h1>

 * Created by saurabh on 12/10/16.
 */

data class LoginResponse(
        val message: String? = null,
        val session: Session? = null,
        @SerializedName("valid_seconds")
        val validSeconds: Long = 0,
        @SerializedName("access_token")
        var accessToken: String? = null
)