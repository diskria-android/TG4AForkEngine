package org.telegram.messenger.third_party.webrtc

object LivePlayer {
    @JvmStatic
    @get:JvmName("recording")
    val recording: Any? get() = WebRTCBridge.LivePlayer.recording()
}
