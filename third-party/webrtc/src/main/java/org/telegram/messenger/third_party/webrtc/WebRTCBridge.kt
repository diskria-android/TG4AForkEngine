package org.telegram.messenger.third_party.webrtc

import android.content.Context
import android.media.projection.MediaProjection

object WebRTCBridge {
    lateinit var runOnUIThread: (Runnable) -> Unit
    lateinit var cancelRunOnUIThread: (Runnable) -> Unit

    object LiteMode {
        lateinit var FLAG_CALLS_ANIMATIONS: () -> Int
        lateinit var isLiteModeFlagEnabled: (Int) -> Boolean
    }

    object ApplicationLoader {
        lateinit var applicationContext: () -> Context
    }

    object LivePlayer {
        lateinit var recording: () -> Any?
    }

    object SharedConfig {
        lateinit var disableVoiceAudioEffects: () -> Boolean
    }

    object VideoCapturerDevice {
        lateinit var getMediaProjection: () -> MediaProjection?
    }
}
