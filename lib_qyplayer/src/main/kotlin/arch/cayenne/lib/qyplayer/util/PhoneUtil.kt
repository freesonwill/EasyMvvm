package arch.cayenne.lib.qyplayer.util

import android.os.Build

object PhoneUtil {

    fun isStrangePhone() = ("mx5".equals(Build.DEVICE, ignoreCase = true)
            || "Redmi Note2".equals(Build.DEVICE, ignoreCase = true)
            || "Z00A_1".equals(Build.DEVICE, ignoreCase = true)
            || "hwH60-L02".equals(Build.DEVICE, ignoreCase = true)
            || "hermes".equals(Build.DEVICE, ignoreCase = true)
            || ("V4".equals(Build.DEVICE, ignoreCase = true) && "Meitu".equals(
        Build.MANUFACTURER,
        ignoreCase = true
    ))
            || ("m1metal".equals(
        Build.DEVICE,
        ignoreCase = true
    ) && "Meizu".equals(Build.MANUFACTURER, ignoreCase = true)));
}