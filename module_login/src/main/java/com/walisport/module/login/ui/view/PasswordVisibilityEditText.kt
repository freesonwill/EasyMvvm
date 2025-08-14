package com.walisport.module.login.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.graphics.drawable.DrawableCompat
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableEditText
import com.walisport.module.login.R

/**
 * @author: ricky.chang
 * @date: 2025/8/11 下午4:00
 * @description:密碼在「可見」和「隱藏」狀態之間切換
 */
open class PasswordVisibilityEditText : SkinnableEditText{

    private var eyeIcon: Drawable? = null
    private var eyeOffIcon: Drawable? = null
    @get:Synchronized
    @set:Synchronized
    private var isPasswordVisible = false
    private var mOnFocusChangeListener: OnFocusChangeListener? = null
    private var mOnTouchListener: OnTouchListener? = null
    override fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener
    }

    override fun setOnTouchListener(onTouchListener: OnTouchListener) {
        mOnTouchListener = onTouchListener
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }
    private fun init(context: Context) {
        val eyeOpenDrawable = SkinnableResourceManager.getDrawable(context, R.drawable.ic_eye_open)?.let {
            DrawableCompat.wrap(it).also { wrapped ->
                DrawableCompat.setTint(wrapped, currentHintTextColor)
            }
        }
        eyeIcon = InsetDrawable(eyeOpenDrawable, 0, 0, 8.dp2px, 0).apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        val eyeCloseDrawable = SkinnableResourceManager.getDrawable(context, R.drawable.ic_eye_closed)?.let {
            DrawableCompat.wrap(it).also { wrapped ->
                DrawableCompat.setTint(wrapped, currentHintTextColor)
            }
        }
        eyeOffIcon = InsetDrawable(eyeCloseDrawable, 0, 0, 8.dp2px, 0).apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        // 初始狀態為隱藏密碼
        updateIcon()
    }

    private fun updateIcon() {
        val icon = if (isPasswordVisible) eyeIcon else eyeOffIcon
        // 將圖示設定在 EditText 的右側
        setCompoundDrawablesRelative(null, null, icon, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            // compoundDrawables[2] 代表右側的圖示
            val drawableRight = compoundDrawablesRelative[2]
            if (drawableRight != null) {
                // 檢查點擊位置是否在右側圖示的範圍內
                // event.rawX 是螢幕上的絕對座標
                // right 是 View 右側邊緣相對於 View 左側的座標
                // drawableRight.bounds.width() 是圖示的寬度
                if (event.rawX >= (right - drawableRight.bounds.width())) {
                    togglePasswordVisibility()
                    // 返回 true 表示事件已被消費
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        // 切換 TransformationMethod
        transformationMethod = if (isPasswordVisible) {
            null // 顯示明文
        } else {
            PasswordTransformationMethod.getInstance() // 隱藏密碼
        }
        // 將游標移到文字末尾
        setSelection(text?.length ?: 0)
        updateIcon()
    }
}