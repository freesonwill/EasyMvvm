package com.walisport.module.login.ui.view

import android.content.Context
import android.util.AttributeSet
import arch.cayenne.lib.common.ui.view.ClearableEditText
import com.walisport.module.login.R

/**
 * @author: ricky.chang
 * @date: 2025/7/21 下午2:23
 * @description:
 */
class StatefulEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : ClearableEditText(context, attrs, defStyleAttr) {

    private var isErrorState: Boolean = false

    companion object {
        // 定義自訂狀態的陣列
        private val STATE_ERROR = intArrayOf(R.attr.state_error)
    }

    /**
     * 設定或取得錯誤狀態
     */
    fun setErrorState(isError: Boolean) {
        if (isErrorState != isError) {
            isErrorState = isError
            // 狀態改變時，刷新 Drawable 的狀態
            refreshDrawableState()
        }
    }

    /**
     * 覆寫此方法，將我們的自訂狀態合併到系統的狀態列表中
     */
    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        // 如果沒有錯誤，就回傳預設的狀態
        if (!isErrorState) {
            return super.onCreateDrawableState(extraSpace)
        }

        // 如果有錯誤，先取得預設狀態
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        // 將我們的自訂狀態合併進去
        mergeDrawableStates(drawableState, STATE_ERROR)
        return drawableState
    }
}
