package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import arch.cayenne.lib.common.databinding.ViewRectangleTextBinding

class RectangleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // 宣告內部的 Views
    private var mBinding: ViewRectangleTextBinding =
        ViewRectangleTextBinding.inflate(LayoutInflater.from(context), this, true)

    // --- Public 方法 ---
    // 允許你在程式碼中動態設定文字
    fun setTitle(title: String) {
        with(mBinding.notificationText) {
            if (title.isNotEmpty()) {
                text = title
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