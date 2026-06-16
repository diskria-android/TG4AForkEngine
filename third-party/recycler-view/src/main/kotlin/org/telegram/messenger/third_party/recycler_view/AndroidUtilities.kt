package org.telegram.messenger.third_party.recycler_view

internal object AndroidUtilities {

    @JvmStatic
    fun dp(value: Float): Int = RecyclerViewBridge.dp(value)

    @JvmStatic
    fun runOnUIThread(runnable: Runnable) {
        RecyclerViewBridge.runOnUIThread(runnable)
    }
}
