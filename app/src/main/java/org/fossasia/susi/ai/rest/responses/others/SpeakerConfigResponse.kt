package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

class SpeakerConfigResponse {

    @SerializedName("configuration")
    var configuration: String? = null

    @SerializedName("hotword")
    var hotword: String? = null

    @SerializedName("stt")
    var stt: String? = null

    @SerializedName("tts")
    var tts: String? = null

    @SerializedName("wake")
    var wake: String? = null
}