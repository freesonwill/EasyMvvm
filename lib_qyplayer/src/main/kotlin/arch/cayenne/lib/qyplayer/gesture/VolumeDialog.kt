package arch.cayenne.lib.qyplayer.gesture

import android.app.Activity
import com.supucloud.qyplayer.demo.R

/**
 * 手势滑动的音量提示框。
 */
class VolumeDialog(context: Activity, percent: Float) : BaseGestureDialog(context) {
    private var initVolume = 0f

    init {
        initVolume = percent
        mImageView.setImageResource(R.drawable.ic_volume)
        updateVolume(percent)
    }

    /**
     * 更新音量值
     * @param percent 音量百分比
     */
    fun updateVolume(percent: Float) {
        mTextView.text = "${percent.toInt()}%"
        mImageView.setImageLevel(percent.toInt())
    }

    /**
     * 获取最后的音量
     * @param changePercent 变化的百分比
     * @return 最后的音量
     */
    fun getTargetVolume(changePercent: Int): Float {
        var newVolume = initVolume - changePercent
        if (newVolume > 100) {
            newVolume = 100f
        } else if (newVolume < 0) {
            newVolume = 0f
        }
        return newVolume
    }

    companion object {
        private val TAG: String = VolumeDialog::class.java.simpleName
    }
}
