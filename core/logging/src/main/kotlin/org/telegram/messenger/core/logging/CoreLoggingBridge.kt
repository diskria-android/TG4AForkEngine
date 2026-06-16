package org.telegram.messenger.core.logging

import java.io.File

object CoreLoggingBridge {
    lateinit var getLogsDir: () -> File?
}
