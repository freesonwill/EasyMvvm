package com.cn.game.sdk2.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.cn.game.sdk2.R
import com.xcjh.base_lib.ModuleInitializer

object ToastUtil {
    private val LOADED_TOAST_TYPEFACE: Typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
    private var currentTypeface: Typeface = LOADED_TOAST_TYPEFACE
    private var textSize: Int = 16 // in SP
    private var tintIcon: Boolean = true
    private var allowQueue: Boolean = false
    private var lastToast: Toast? = null

    @JvmOverloads
    fun showToastNormal(message: CharSequence, duration: Int = Toast.LENGTH_SHORT,gravity: Int=Gravity.CENTER) {
        val context = ModuleInitializer.application
        val toast = showToastCustom(context, message, null, getColor(context, android.R.color.transparent), getColor(context, R.color.white), duration, true, false,gravity)
        toast.show()
    }

    @JvmOverloads
    fun showToastWarning(message: CharSequence, duration: Int = Toast.LENGTH_SHORT,gravity: Int=Gravity.CENTER) {
        val context = ModuleInitializer.application
        val toast = showToastCustom(context, message, ContextCompat.getDrawable(context, R.drawable.ic_tips), getColor(context, android.R.color.transparent), getColor(context, R.color.white), duration, true, false,gravity)
        toast.show()
    }

    @JvmOverloads
    fun showToastError(message: CharSequence, duration: Int = Toast.LENGTH_SHORT,gravity: Int=Gravity.CENTER) {
        val context = ModuleInitializer.application
        val toast = showToastCustom(context, message, ContextCompat.getDrawable(context, R.drawable.icon_svg_close_white), getColor(context, android.R.color.transparent), getColor(context, R.color.white), duration, true, false,gravity)
        toast.show()
    }

    @JvmOverloads
    fun showToastSuccess(message: CharSequence, duration: Int = Toast.LENGTH_SHORT,gravity: Int=Gravity.CENTER) {
        val context = ModuleInitializer.application
        val toast = showToastCustom(context, message, ContextCompat.getDrawable(context, R.drawable.icon_svg_check_white), getColor(context, android.R.color.transparent), getColor(context, R.color.white), duration, true, false,gravity)
        toast.show()
    }


    @JvmOverloads
    fun showToastCustom(context: Context, message: CharSequence, icon: Drawable?,
                        @ColorInt tintColor: Int,
                        @ColorInt textColor: Int, duration: Int,
                        withIcon: Boolean, shouldTint: Boolean,
                        gravity: Int = Gravity.CENTER
    ): Toast {
        val context = context.applicationContext
        val currentToast = Toast.makeText(context, "", duration)
        val toastLayout: View = LayoutInflater.from(context).inflate(R.layout.toast_layout, null)
        val toastIcon = toastLayout.findViewById<ImageView>(R.id.toast_icon)

        val toastTextView = toastLayout.findViewById<TextView>(R.id.toast_text)
        if (!withIcon) {
            toastIcon.visibility = View.GONE
        }
        if (null != icon) {
            toastIcon.setImageDrawable(icon)
            toastIcon.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(message)) {
            toastTextView.text = message
        }
        if( shouldTint ){
            toastIcon.imageTintList = ColorStateList.valueOf(tintColor)
        }
        toastTextView.setTextColor(textColor)
        toastTextView.setTypeface(currentTypeface)
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        currentToast.view = toastLayout

        if (!allowQueue) {
            if (lastToast != null) {
                cancelToast()
            }
            lastToast = currentToast
            val task = Runnable { lastToast = null }
            currentToast.view!!.tag = task
            currentToast.view!!.postDelayed(task, duration.toLong())
        }
        currentToast.setGravity(gravity, 0, 0)
        return currentToast
    }

    /**
     * 取消toast
     */
    fun cancelToast() {
        if (lastToast != null) {
            lastToast!!.cancel()
            (lastToast?.view?.tag as Runnable).let { r ->
                lastToast?.view?.removeCallbacks(r)
                lastToast?.view?.tag = null
            }
            lastToast = null
        }
    }
}