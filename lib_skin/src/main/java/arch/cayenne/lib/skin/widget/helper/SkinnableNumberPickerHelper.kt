package arch.cayenne.lib.skin.widget.helper

import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType

open class SkinnableNumberPickerHelper(mView: NumberPicker) : SkinnableHelper(mView) {

    private var textColorResId = INVALID_ID
    private var textSizePx = -1f

    override val mView: NumberPicker
        get() = super.mView as NumberPicker

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val context = mView.context

        val a = context.obtainStyledAttributes(attrs, R.styleable.SportNumberPickerHelper, defStyleAttr, 0)
        if (a.hasValue(R.styleable.SportNumberPickerHelper_android_textColor)) {
            textColorResId = a.getResourceId(R.styleable.SportNumberPickerHelper_android_textColor, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportNumberPickerHelper_android_textSize)) {
            val rawSize = a.getDimensionPixelSize(R.styleable.SportNumberPickerHelper_android_textSize, -1)
            if (rawSize > 0) textSizePx = rawSize.toFloat()
        }
        a.recycle()

        updateSkin(SkinMsgType.SELF)
    }

    override fun updateSkin(msgType: SkinMsgType) {
        if(checkSkinName(msgType)){
            return
        }
        applyTextColorResource()
        applyTextSizeResource()
        disableEditTextInteraction()
    }

    override fun updateLanguage(languageCode: String) {
        // NumberPicker 本身沒文字資源，一般不需處理
    }

    fun updateTextColor() {
        applyTextColorResource()
    }

    private fun applyTextColorResource() {
        if (checkResourceIdValid(textColorResId)) {
            val color = resourcesManager.getColor(mView.context, textColorResId)
            if (Build.VERSION.SDK_INT >= 29) {
                mView.textColor = color
            } else {
                applyToEditTextViews {
                    it.setTextColor(color)
                }
            }
            mView.invalidate()
        }
    }

    private fun applyTextSizeResource() {
        if (textSizePx > 0f) {
            if (Build.VERSION.SDK_INT >= 29) {
                mView.textSize = textSizePx
            } else {
                applyToEditTextViews { it.textSize = textSizePx }
            }
        }
    }

    private fun applyToEditTextViews(action: (EditText) -> Unit) {
        for (i in 0 until mView.childCount) {
            val child = mView.getChildAt(i)
            if (child is EditText) {
                action(child)
            }
        }
    }

    private fun disableEditTextInteraction() {
        applyToEditTextViews {
            it.isFocusable = false
            it.isClickable = false
            it.isLongClickable = false
            it.isCursorVisible = false
            it.keyListener = null // 禁止鍵盤輸入
            it.setBackgroundColor(Color.TRANSPARENT)
            it.setOnTouchListener { v, event -> false }
        }
    }
}

