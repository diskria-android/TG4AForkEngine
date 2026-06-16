package org.telegram.messenger.core.logging

import java.io.File

internal object AndroidUtilities {

    @JvmStatic
    fun getLogsDir(): File? = CoreLoggingBridge.getLogsDir()
}
