package org.telegram.messenger.third_party.recycler_view

object RecyclerViewBridge {
    lateinit var dp: (Float) -> Int
    lateinit var runOnUIThread: (Runnable) -> Unit
}
