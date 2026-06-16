package org.telegram.messenger.third_party.webrtc

import android.content.Context

object ApplicationLoader {
    @JvmStatic
    @get:JvmName("applicationContext")
    val applicationContext: Context
        get() = WebRTCBridge.ApplicationLoader.applicationContext()
}
