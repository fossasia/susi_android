package org.fossasia.susi.ai.rest.responses.susi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by meeera on 30/6/17.
 */

class Settings {

    @SerializedName("speech_always")
    @Expose
    val speechAlways: Boolean = false

    @SerializedName("enter_send")
    @Expose
    val enterSend: Boolean = false

    @SerializedName("speech_output")
    @Expose
    val speechOutput: Boolean = true

    @SerializedName("mic_input")
    @Expose
    val micInput: Boolean = true

}
