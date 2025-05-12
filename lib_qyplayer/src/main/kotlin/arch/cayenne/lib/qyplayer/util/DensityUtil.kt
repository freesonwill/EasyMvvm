package arch.cayenne.lib.qyplayer.util

import android.content.Context

/**
 * 手机分辨率工具类
 */
object DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位转成 px（像素）
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px（像素）单位转成 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}