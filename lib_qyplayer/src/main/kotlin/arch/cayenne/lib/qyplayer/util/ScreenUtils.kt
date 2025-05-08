package arch.cayenne.lib.qyplayer.util

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.supucloud.qyplayer.log.L

/**
 * 屏幕相关的操作类
 */
object ScreenUtils {
    /**
     * 获取宽度
     *
     * @param mContext 上下文
     * @return 宽度值，px
     */
    fun getWidth(mContext: Context): Int {
        val displayMetrics = DisplayMetrics()
        (mContext.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 获取高度
     *
     * @param mContext 上下文
     * @return 高度值，px
     */
    fun getHeight(mContext: Context): Int {
        val displayMetrics = DisplayMetrics()
        (mContext.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 是否在屏幕右侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    fun isInRight(mContext: Context, xPos: Int): Boolean {
        return (xPos > getWidth(mContext) / 2)
    }

    /**
     * 是否在屏幕左侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    fun isInLeft(mContext: Context, xPos: Int): Boolean {
        return (xPos < getWidth(mContext) / 2)
    }

    /**
     * 是否在View的右侧
     * @param view      要判断的View
     * @param xPos      位置x坐标
     * @return          true:是 false:不是
     */
    fun isInRight(view: View, xPos: Int): Boolean {
        return (xPos > view.measuredWidth / 2)
    }

    /**
     * 是否在View的左侧
     * @param view      要判断的View
     * @param xPos      位置x坐标
     * @return          true:是 false:不是
     */
    fun isInLeft(view: View, xPos: Int): Boolean {
        return (xPos < view.measuredWidth / 2)
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height = resources.getDimensionPixelSize(resourceId)
        return height
    }

    /**
     * Get the current brightness
     *
     * 获取系统当前亮度
     */
    private fun getSystemBrightness(activity: Activity): Float {
        var value = 0f
        val cr = activity.contentResolver
        try {
            value =
                (Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS).toFloat()) / 255.0f
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 获取当前Activity亮度
     */
    fun getActivityBrightness(activity: Activity): Float {
        val screenBrightness = activity.window.attributes.screenBrightness
        if (screenBrightness < 0) {
            return getSystemBrightness(activity)
        }
        return screenBrightness
    }

    fun updateBright(activity: Activity, progress: Int) {
        val window = activity.window
        val params = window.attributes
        params.screenBrightness = progress * 1.0f / 100
        if (params.screenBrightness > 1.0f) {
            params.screenBrightness = 1.0f
        }
        if (params.screenBrightness <= 0.01f) {
            params.screenBrightness = 0.01f
        }

        window.attributes = params
    }

    fun updateVolumeProgress(audioManager: AudioManager, progress: Int, max: Int): Int {
        val percentage = progress.toFloat() / max

        L.e("percentage: $percentage")

        if (percentage < 0 || percentage > 1) return progress

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newVolume = (percentage * maxVolume).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)

        L.e("maxVolume: $maxVolume, newVolume: $newVolume, max: $max")
        return ((newVolume.toFloat() / maxVolume) * max).toInt()
    }

    fun getCurrentVolumeProgress(audioManager: AudioManager, max: Int): Int {
        val curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        val percentage = curVolume.toFloat() / maxVolume

        val progress = (percentage * max).toInt()

        L.e("curVolume: $curVolume, maxVolume: $maxVolume, percentage: $percentage, progress: $progress, max: $max")
        return progress
    }
}
