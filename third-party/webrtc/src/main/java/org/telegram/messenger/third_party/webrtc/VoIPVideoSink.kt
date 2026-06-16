package org.telegram.messenger.third_party.webrtc

import org.webrtc.VideoSink

interface VoIPVideoSink {
    fun removeTarget(target: VideoSink)
    fun removeBackground(background: VideoSink)
}
