package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import arch.cayenne.lib.common.databinding.NotificationBadgeViewBinding

// 我們使用 FrameLayout 作為容器
class NotificationBadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // 宣告內部的 Views
    private var mBinding: NotificationBadgeViewBinding =
        NotificationBadgeViewBinding.inflate(LayoutInflater.from(context), this, true)


    // --- Public 方法 ---
    // 允許你在程式碼中動態設定文字
    fun setNotificationCount(count: Int) {
        with(mBinding.notificationText) {
            if (count > 0) {
                text = count.toString()
                visibility = VISIBLE
            } else {
                text = ""
                visibility = GONE // 如果沒有文字就隱藏
            }
        }
    }

    // 允許你在程式碼中動態設定文字顏色
    fun setTextColor(color: Int) {
        mBinding.notificationText.setTextColor(color)
    }

}