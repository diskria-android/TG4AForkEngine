package org.telegram.messenger.third_party.webrtc

object SharedConfig {
    @JvmStatic
    @get:JvmName("disableVoiceAudioEffects")
    val disableVoiceAudioEffects: Boolean
        get() = WebRTCBridge.SharedConfig.disableVoiceAudioEffects()
}
