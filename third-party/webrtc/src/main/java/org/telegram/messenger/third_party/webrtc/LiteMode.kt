package org.telegram.messenger.third_party.webrtc

object LiteMode {
    @JvmField
    val FLAG_CALLS_ANIMATIONS: Int = WebRTCBridge.LiteMode.FLAG_CALLS_ANIMATIONS()

    @JvmStatic
    fun isEnabled(flag: Int): Boolean = WebRTCBridge.LiteMode.isLiteModeFlagEnabled(flag)
}
