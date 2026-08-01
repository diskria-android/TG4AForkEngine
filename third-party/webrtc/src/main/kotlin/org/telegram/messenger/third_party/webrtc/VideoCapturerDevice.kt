package org.telegram.messenger.third_party.webrtc

import android.media.projection.MediaProjection

object VideoCapturerDevice {
    @JvmStatic
    fun getMediaProjection(): MediaProjection? =
        WebRTCBridge.VideoCapturerDevice.getMediaProjection()
}
