package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

class SpeakerAuthResponse {
    @SerializedName("auth")
    var auth: String? = null

    @SerializedName("authentication")
    var authentication: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null
}