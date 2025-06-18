package arch.cayenne.lib.skin.widget.helper

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType
import java.util.Locale

open class SkinnableNumberPickerHelper(mView: NumberPicker) : SkinnableHelper(mView) {

    private var textColorResId = INVALID_ID
    private var textSizeSp = -1f

    override val mView: NumberPicker
        get() = super.mView as NumberPicker

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val context = mView.context

        val a = context.obtainStyledAttributes(attrs, R.styleable.SportNumberPickerHelper, defStyleAttr, 0)
        val textAppearanceId = a.getResourceId(R.styleable.SportNumberPickerHelper_android_textAppearance, INVALID_ID)
        if (textAppearanceId != INVALID_ID) {
            val ta = context.obtainStyledAttributes(textAppearanceId, R.styleable.SportNumberPickerTextAppearance)
            if (ta.hasValue(R.styleable.SportNumberPickerTextAppearance_android_textColor)) {
                textColorResId = ta.getResourceId(R.styleable.SportNumberPickerTextAppearance_android_textColor, INVALID_ID)
            }
            if (ta.hasValue(R.styleable.SportNumberPickerTextAppearance_android_textSize)) {
                val textSize = ta.getDimensionPixelSize(R.styleable.SportNumberPickerTextAppearance_android_textSize, INVALID_ID)
                if (textSize != INVALID_ID) {
                    val fontScale = context.resources.configuration.fontScale
                    val density = context.resources.displayMetrics.density
                    val textSizeInSp = (textSize / density) / fontScale

                    if (textSize > 0) {
                        textSizeSp = textSizeInSp
                    }
                }
            }
            ta.recycle()
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


    private fun applyTextColorResource() {
        if (checkResourceIdValid(textColorResId)) {
            val color = resourcesManager.getColor(mView.context, textColorResId)
            if (Build.VERSION.SDK_INT >= 29) {
                mView.textColor = color
            } else {
                applyToEditTextViews {
                    it.setHintTextColor(color)
                    it.setTextColor(color)
                }
                try {
                    val pickerFields = NumberPicker::class.java.declaredFields
                    for (field in pickerFields) {
                        if (field.name == "mSelectorWheelPaint") {
                            field.isAccessible = true
                            field.get(mView)?.let { paint ->
                                paint.javaClass.getMethod("setColor", Int::class.java).invoke(paint, color)
                            }
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mView.invalidate()
        }
    }

    private fun applyTextSizeResource() {
        if (textSizeSp > 0f) {
//            if (Build.VERSION.SDK_INT >= 29) {
//                mView.textSize = textSizeSp
//            } else {
                applyToEditTextViews {
                    it.textSize = textSizeSp
                }
                try {
                    val pickerFields = NumberPicker::class.java.declaredFields
                    for (field in pickerFields) {
                        if (field.name == "mSelectorWheelPaint") {
                            field.isAccessible = true
                            field.get(mView)?.let { paint ->
                                val density = mView.context.resources.displayMetrics.density
                                val textSizeInPx = textSizeSp * density
                                paint.javaClass.getMethod("setTextSize", Float::class.java).invoke(paint, textSizeInPx)
                            }
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//            }
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

