package arch.cayenne.lib.skin.widget.helper

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType

open class SkinnableNumberPickerHelper(mView: NumberPicker) : SkinnableHelper(mView) {

    private var textColorResId = INVALID_ID
    private var textSizeSp = -1f

    override val mView: NumberPicker
        get() = super.mView as NumberPicker

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val context = mView.context

        val a = context.obtainStyledAttributes(attrs, R.styleable.SportNumberPickerHelper, defStyleAttr, 0)
        if (a.hasValue(R.styleable.SportNumberPickerHelper_android_textAppearance)) {
            val textAppearanceId = a.getResourceId(R.styleable.SportNumberPickerHelper_android_textAppearance, -1)

            if (textAppearanceId != -1) {
                val typedArray = context.obtainStyledAttributes(textAppearanceId, androidx.appcompat.R.styleable.TextAppearance)

                val textColor = typedArray.getColor(androidx.appcompat.R.styleable.TextAppearance_android_textColor, -1)
                val textSize = typedArray.getDimensionPixelSize(androidx.appcompat.R.styleable.TextAppearance_android_textSize, -1)

                typedArray.recycle()

                if (textColor > 0) {
                    textColorResId = textColor
                }

                val fontScale = context.resources.configuration.fontScale
                val density = context.resources.displayMetrics.density
                val textSizeInSp = (textSize / density) / fontScale

                if (textSize > 0) {
                    textSizeSp = textSizeInSp
                }
            }
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
        if (textSizeSp > 0f) {
            if (Build.VERSION.SDK_INT >= 29) {
                mView.textSize = 17f
            } else {
                applyToEditTextViews { it.textSize = 17f }
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

    @SuppressLint("ClickableViewAccessibility")
    private fun disableEditTextInteraction() {
        applyToEditTextViews {
            it.isFocusable = false
            it.isClickable = false
            it.isLongClickable = false
            it.isCursorVisible = false
            it.keyListener = null // 禁止鍵盤輸入
            it.setBackgroundColor(Color.TRANSPARENT)
            it.setOnTouchListener { _, _ -> false }
        }
    }
}

