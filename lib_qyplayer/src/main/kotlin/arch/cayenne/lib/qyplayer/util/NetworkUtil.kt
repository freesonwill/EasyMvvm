package arch.cayenne.lib.qyplayer.util

import android.annotation.SuppressLint

object NetworkUtil {
    @SuppressLint("DefaultLocale")
    fun formatSpeed(kbps: Long): String {
        return when {
            kbps < 1024 -> "%d KB/s".format(kbps)
            kbps < 1024 * 1024 -> "%.2f MB/s".format(kbps.div(1024.0))
            else -> "%.2f GB/s".format(kbps.div(1024.0 * 1024.0))
        }
    }
}