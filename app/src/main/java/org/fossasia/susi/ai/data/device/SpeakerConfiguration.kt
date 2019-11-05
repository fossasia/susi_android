package org.fossasia.susi.ai.data.device

data class SpeakerConfiguration(
    val stt: String = "",
    val tts: String = "",
    val hotword: String = "",
    val wake: String = ""
)
