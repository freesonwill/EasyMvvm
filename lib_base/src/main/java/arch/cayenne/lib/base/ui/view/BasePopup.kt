package arch.cayenne.lib.base.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.viewbinding.ViewBinding

/**
 * @author: hamigua
 * @date: 2025/4/24 13:54
 * @description: PopupWindow 简单封装（基类）
 */
class BasePopup<VB : ViewBinding> private constructor(
    private val context: Context,
    private val bindingInflater: (LayoutInflater) -> VB,
    private val width: Int,
    private val height: Int,
    private val isFocusable: Boolean,
    private val isOutsideTouchable: Boolean,
    private val animationStyleRes: Int?,
    private val onShow: ((VB) -> Unit)?,
    private val onDismiss: (() -> Unit)?,
) {

    private var popupWindow: PopupWindow? = null
    private var binding: VB? = null


    fun show(anchor: View, gravity: Int = Gravity.NO_GRAVITY, xOffset: Int = 0, yOffset: Int = 0) {
        if (popupWindow == null) {
            val inflater = LayoutInflater.from(context)
            binding = bindingInflater(inflater)
            popupWindow = PopupWindow(binding?.root, width, height, isFocusable).apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                isOutsideTouchable = this@BasePopup.isOutsideTouchable
                this@BasePopup.animationStyleRes?.let {
                    animationStyle = it
                }
                binding?.let { onShow?.invoke(it) }
                showAsDropDown(anchor, xOffset, yOffset, gravity)
                setOnDismissListener {
                    this@BasePopup.dismiss()
                }
            }
        }
    }

    private fun dismiss() {
        popupWindow?.dismiss()
        onDismiss?.invoke()
    }

    class Builder<VB : ViewBinding>(private val context: Context, private val bindingInflater: (LayoutInflater) -> VB) {
        private var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        private var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        private var isFocusable: Boolean = true
        private var isOutsideTouchable: Boolean = true
        private var animationStyle: Int? = null
        private var onShow: ((VB) -> Unit)? = null
        private var onDismiss: (() -> Unit)? = null

        fun setWidth(width: Int) = apply { this.width = width }
        fun setHeight(height: Int) = apply { this.height = height }
        fun setFocusable(isFocusable: Boolean) = apply { this.isFocusable = isFocusable }
        fun setOutsideTouchable(isOutsideTouchable: Boolean) = apply { this.isOutsideTouchable = isOutsideTouchable }
        fun setAnimationStyle(animationStyle: Int) = apply { this.animationStyle = animationStyle }
        fun setOnShowListener(onShow: (VB) -> Unit) = apply { this.onShow = onShow }
        fun setOnDismissListener(onDismiss: () -> Unit) = apply { this.onDismiss = onDismiss }

        fun build(): BasePopup<VB> {
            return BasePopup(context, bindingInflater, width, height, isFocusable, isOutsideTouchable, animationStyle, onShow, onDismiss)
        }
    }
}