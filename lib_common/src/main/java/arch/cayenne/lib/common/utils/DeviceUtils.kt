package arch.cayenne.lib.common.utils

/**
 *
 * @date: 2025/12/24 17:35
 * @description:
 */
object DeviceUtils {
    fun isHuaweiDevice(): Boolean {
        val manufacturer = android.os.Build.MANUFACTURER
        return manufacturer.equals("HUAWEI", ignoreCase = true) ||
                manufacturer.equals("HONOR", ignoreCase = true)
    }
}