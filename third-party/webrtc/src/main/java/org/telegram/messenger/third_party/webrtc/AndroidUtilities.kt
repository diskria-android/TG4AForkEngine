package org.telegram.messenger.third_party.webrtc

internal object AndroidUtilities {

    @JvmStatic
    fun runOnUIThread(runnable: Runnable) {
        WebRTCBridge.runOnUIThread(runnable)
    }

    @JvmStatic
    fun cancelRunOnUIThread(runnable: Runnable) {
        WebRTCBridge.cancelRunOnUIThread(runnable)
    }
}
