package arch.cayenne.module.home.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.StyleRes
import androidx.viewbinding.ViewBinding

/**
 * HomeCalendarPopupWindow ，使用 ViewBinding 和 Builder 模式。
 * @param VB ViewBinding 的泛型類型
 * @property context Context
 * @property binding 透過 bindingInflater 實例化的 ViewBinding 物件
 * @property popupWindow 實際的 PopupWindow 實例
 */
open class HomeCalendarPopupWindow<VB : ViewBinding> protected constructor(
    val context: Context,
    bindingInflater: (LayoutInflater) -> VB, // 用於實例化 Binding
    builder: Builder<VB> // 接收 Builder 以應用配置
) {
    // 1. 實例化 ViewBinding
    val binding: VB = bindingInflater(LayoutInflater.from(context))

    // 2. 創建 PopupWindow
    private val popupWindow: PopupWindow = PopupWindow(
        binding.root, // content view
        builder.width,
        builder.height,
        builder.isFocusable
    )

    init {

        // 3. 應用 Builder 中的其他配置
        popupWindow.isOutsideTouchable = builder.isOutsideTouchable
        if (!builder.isOutsideTouchable && !builder.isFocusable) {
            // 為了讓 isOutsideTouchable 生效，通常需要一個背景
            // 如果你沒有在 XML 設定背景，這裡給一個透明背景
            if (popupWindow.background == null) {
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
        builder.animationStyle?.let { popupWindow.animationStyle = it }
        builder.onDismissListener?.let { popupWindow.setOnDismissListener(it) }
        builder.elevation?.let { popupWindow.elevation = it }
        // 其他 PopupWindow 的屬性可以按需添加

        // 預設設定
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        // popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    }

    /**
     * 顯示 PopupWindow，作為錨點 View 的下拉。
     * @param anchor 錨點 View
     * @param xOff X 軸偏移量
     * @param yOff Y 軸偏移量
     * @param gravity 對齊方式 (相對於錨點)
     */
    open fun showAsDropDown(anchor: View, xOff: Int = 0, yOff: Int = 0, gravity: Int = Gravity.NO_GRAVITY) {
        if (!popupWindow.isShowing) {
            popupWindow.showAsDropDown(anchor, xOff, yOff, gravity)
        }
    }

    /**
     * 關閉 PopupWindow。
     */
    open fun dismiss() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * Builder 類別用於構建 BasePopupWindow
     */
    open class Builder<VB : ViewBinding>(
        protected val context: Context,
        private val bindingInflater: (LayoutInflater) -> VB
    ) {
        internal var width: Int = WindowManager.LayoutParams.MATCH_PARENT
        internal var height: Int = WindowManager.LayoutParams.WRAP_CONTENT
        internal var isFocusable: Boolean = true
        internal var isOutsideTouchable: Boolean = true
        @StyleRes
        internal var animationStyle: Int? = null
        internal var onDismissListener: PopupWindow.OnDismissListener? = null
        internal var elevation: Float? = null

        open fun build(): HomeCalendarPopupWindow<VB> {
            return HomeCalendarPopupWindow(context, bindingInflater, this)
        }
    }
}