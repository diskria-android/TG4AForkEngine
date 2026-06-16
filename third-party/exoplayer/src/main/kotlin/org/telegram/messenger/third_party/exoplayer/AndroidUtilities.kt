package org.telegram.messenger.third_party.exoplayer

internal object AndroidUtilities {

    @JvmStatic
    fun formatFileSize(bytes: Long): String? = ExoplayerBridge.formatFileSize(bytes)
}
