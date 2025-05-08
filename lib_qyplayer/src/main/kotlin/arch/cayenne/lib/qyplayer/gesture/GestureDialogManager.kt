package arch.cayenne.lib.qyplayer.gesture

import android.app.Activity
import android.view.View
import com.supucloud.qyplayer.demo.ScreenMode

/**
 * 手势对话框的管理器。
 */
class GestureDialogManager(private var mActivity: Activity) {
    //seek手势对话框
    private var mSeekDialog: SeekDialog? = null

    //亮度对话框
    private var mBrightnessDialog: BrightnessDialog? = null

    //音量对话框
    private var mVolumeDialog: VolumeDialog? = null

    //当前屏幕默认为小屏
    private var mCurrentScreenMode: ScreenMode = ScreenMode.SMALL

    /**
     * 显示seek对话框
     *
     * @param parent         显示在哪个view的中间
     * @param targetPosition seek的位置
     */
    fun showSeekDialog(parent: View, targetPosition: Int) {
        if (mSeekDialog == null) {
            mSeekDialog = SeekDialog(mActivity, targetPosition)
        }
        mSeekDialog?.let {
            if (!it.isShowing) {
                it.show(parent)
                it.updatePosition(targetPosition)
            }
        }
    }

    /**
     * 更新seek进度。 在手势滑动的过程中调用。
     *
     * @param duration        时长
     * @param currentPosition 当前位置
     * @param deltaPosition   滑动位置
     */
    fun updateSeekDialog(duration: Long, currentPosition: Long, deltaPosition: Long) {
        val targetPosition =
            mSeekDialog!!.getTargetPosition(duration, currentPosition, deltaPosition)
        mSeekDialog!!.updatePosition(targetPosition)
    }

    /**
     * 隐藏seek对话框
     *
     * @return 最终的seek位置，用于实际的seek操作
     */
    fun dismissSeekDialog(): Int {
        var seekPosition = -1

        mSeekDialog?.let {
            if (it.isShowing) {
                seekPosition = it.finalPosition
                it.dismiss()
            }
        }
        mSeekDialog = null
        //返回最终的seek位置，用于实际的seek操作
        return seekPosition
    }


    /**
     * 显示亮度对话框
     * @param parent 显示在哪个view中间
     */
    fun showBrightnessDialog(parent: View?, currentBrightness: Int) {
        if (mBrightnessDialog == null) {
            mBrightnessDialog = BrightnessDialog(mActivity, currentBrightness)
        }

        mBrightnessDialog?.let {
            if (!it.isShowing) {
                it.setScreenMode(mCurrentScreenMode)
                it.show(parent!!)
                it.updateBrightness(currentBrightness)
            }
        }
    }

    /**
     * 更新亮度值
     * @param changePercent 亮度变化百分比
     * @return 最终的亮度百分比
     */
    fun updateBrightnessDialog(changePercent: Int): Int {
        val targetBrightnessPercent = mBrightnessDialog!!.getTargetBrightnessPercent(changePercent)
        mBrightnessDialog!!.updateBrightness(targetBrightnessPercent)
        return targetBrightnessPercent
    }

    /**
     * 隐藏亮度对话框
     */
    fun dismissBrightnessDialog() {
        mBrightnessDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        mBrightnessDialog = null
    }

    fun initDialog(activity: Activity, currentPercent: Float) {
        this.mActivity = activity
        if (mVolumeDialog == null) {
            mVolumeDialog = VolumeDialog(activity, currentPercent)
        }
    }

    /**
     * 显示音量对话框
     * @param parent  显示在哪个view中间
     * @param currentPercent 当前音量百分比
     */
    fun showVolumeDialog(parent: View, currentPercent: Float) {
        if (mVolumeDialog == null) {
            mVolumeDialog = VolumeDialog(mActivity, currentPercent)
        }

        mVolumeDialog?.let {
            if (!it.isShowing) {
                it.setScreenMode(mCurrentScreenMode)
                it.show(parent)
                it.updateVolume(currentPercent)
            }
        }
    }

    val isVolumeDialogIsShow: Boolean
        get() {
            return mVolumeDialog?.isShowing ?: false
        }

    /**
     * 更新音量
     * @param changePercent 变化的百分比
     * @return 最终的音量百分比
     */
    fun getTargetVolume(changePercent: Int): Float {
        return mVolumeDialog?.getTargetVolume(changePercent) ?: 0f
    }

    fun updateVolumeDialog(volume: Float) {
        mVolumeDialog?.updateVolume(volume)
    }

    /**
     * 关闭音量对话框
     */
    fun dismissVolumeDialog() {
        mVolumeDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        mVolumeDialog = null
    }

    fun setCurrentScreenMode(currentScreenMode: ScreenMode) {
        this.mCurrentScreenMode = currentScreenMode
    }
}
