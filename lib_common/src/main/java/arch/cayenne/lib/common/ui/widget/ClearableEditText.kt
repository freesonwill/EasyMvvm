package arch.cayenne.lib.common.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import arch.cayenne.lib.common.R

class ClearableEditText : AppCompatEditText, OnTouchListener,
    OnFocusChangeListener, TextWatcher {
    private var clearTextIcon: Drawable? = null
    private var mOnFocusChangeListener: OnFocusChangeListener? = null
    private var mOnTouchListener: OnTouchListener? = null

    @get:Synchronized
    @set:Synchronized
    var isCanClear: Boolean = false

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

    override fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener
    }

    override fun setOnTouchListener(onTouchListener: OnTouchListener) {
        mOnTouchListener = onTouchListener
    }

    private fun init(context: Context) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.clearable_icon)
        val wrappedDrawable = DrawableCompat.wrap(
            drawable!!
        )
        DrawableCompat.setTint(
            wrappedDrawable,
            currentHintTextColor
        )
        clearTextIcon = wrappedDrawable
        clearTextIcon!!.setBounds(
            0, 0, clearTextIcon!!.intrinsicWidth,
            clearTextIcon!!.intrinsicHeight
        )
        setClearIconVisible(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text!!.length > 0)
        } else {
            setClearIconVisible(false)
            isCanClear = true
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener!!.onFocusChange(view, hasFocus)
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val x = motionEvent.x.toInt()
        if (x > width - paddingRight - clearTextIcon!!.intrinsicWidth) {
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (clearTextIcon!!.isVisible) {
                    error = null
                    setText("")
                } else if (isCanClear) {
                    isCanClear = false
                    error = null
                    setText("")
                }
            }
            return true
        } else {
            return mOnTouchListener != null && mOnTouchListener!!.onTouch(
                view,
                motionEvent
            )
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isFocused) {
            setClearIconVisible(s.length > 0)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable) {
    }

    private fun setClearIconVisible(visible: Boolean) {
        clearTextIcon!!.setVisible(visible, false)
        val compoundDrawables = compoundDrawables
        setCompoundDrawables(
            compoundDrawables[0],
            compoundDrawables[1], if (visible) clearTextIcon else null, compoundDrawables[3]
        )
    }
}