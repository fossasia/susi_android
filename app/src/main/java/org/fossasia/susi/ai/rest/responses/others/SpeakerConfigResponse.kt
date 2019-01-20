package org.fossasia.susi.ai.rest.responses.others

data class SpeakerConfigResponse(
    val configuration: String,
    val hotword: String,
    val stt: String,
    val tts: String,
    val wake: String
)