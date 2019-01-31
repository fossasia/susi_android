package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.SerializedName

data class Settings(
    @SerializedName("speechOutputAlways")
    val speechAlways: Boolean = false,
    @SerializedName("enterAsSend")
    val enterSend: Boolean = false,
    @SerializedName("speechOutput")
    val speechOutput: Boolean = true,
    @SerializedName("micInput")
    val micInput: Boolean = true,
    @SerializedName("prefLanguage")
    val language: String = "default"
)
