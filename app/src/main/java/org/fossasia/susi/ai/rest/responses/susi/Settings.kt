package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 30/6/17.
 */

class Settings {

    @SerializedName("speech_always")
    @Expose
    val speechAlways: String ?= null

    @SerializedName("enter_send")
    @Expose
    val enterSend: String ?= null

    @SerializedName("speech_output")
    @Expose
    val speechOutput: String ?= null

    @SerializedName("mic_input")
    @Expose
    val micInput: String ?= null

    @SerializedName("theme")
    @Expose
    val theme: String ?= null
}
