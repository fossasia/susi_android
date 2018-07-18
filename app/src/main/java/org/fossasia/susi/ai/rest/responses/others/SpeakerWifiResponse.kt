package org.fossasia.susi.ai.rest.responses.others

import com.google.gson.annotations.SerializedName

class SpeakerWifiResponse {

    @SerializedName("wifi")
    var wifi: String? = null

    @SerializedName("wifi_ssid")
    var wifi_ssid: String? = null

    @SerializedName("wifi_password")
    var wifi_password: String? = null
}