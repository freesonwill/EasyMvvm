package arch.cayenne.module.betslip.ui.helper

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.betslip.databinding.ItemTipsLayoutBinding

class BetTipsHelper {
    private var popupWindow: PopupWindow? = null
    private var mBinding: ItemTipsLayoutBinding? = null

    companion object {
        private const val SHOW_TIME = 3_000L
    }

    fun showTips(attachView: View) {
        // 確保之前的 PopupWindow 已經關閉
        dismissTips()
        mBinding = ItemTipsLayoutBinding.inflate(LayoutInflater.from(attachView.context)).apply {
            // 建立 PopupWindow
            popupWindow = PopupWindow(
                this.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                isOutsideTouchable = true
            }

            // 先進行測量
            this.root.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val arrowMarginStart = ivTopArrow.layoutParams as ViewGroup.MarginLayoutParams
            val arrowCenterX = arrowMarginStart.marginStart + (ivTopArrow.measuredWidth / 2)

            Log.d("abcd", "+++ $arrowCenterX, ${ivTopArrow.measuredWidth}, ${arrowMarginStart.marginStart}")

            // 計算 PopupWindow 的顯示位置
            val offX = -(arrowCenterX - attachView.width / 2)

            // 顯示 PopupWindow
            popupWindow?.showAsDropDown(attachView, offX, 0)

            this.root.postDelayed({ dismissTips() }, SHOW_TIME)
        }
    }

    private fun dismissTips() {
        popupWindow?.dismiss()
        popupWindow = null
        mBinding = null
    }
} 