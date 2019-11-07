package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String? = null,
    val session: Session? = null,
    @SerializedName("valid_seconds")
    val validSeconds: Long = 0,
    @SerializedName("access_token")
    var accessToken: String? = null
)
