package arch.cayenne.lib.skin.widget.helper

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.DrawableRes
import arch.cayenne.lib.skin.R

open class SkinnableTextHelper(mView: TextView) : SkinnableHelper(mView) {

    var textColorResId = INVALID_ID
    private var mTextColorHintResId = INVALID_ID
    private var mDrawableBottomResId = INVALID_ID
    private var mDrawableLeftResId = INVALID_ID
    private var mDrawableRightResId = INVALID_ID
    private var mDrawableTopResId = INVALID_ID
    private var mTextResId = INVALID_ID
    override val mView: TextView
        get() = super.mView as TextView

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val context = mView.context

        // First read the TextAppearance style id
        var a = context.obtainStyledAttributes(attrs, R.styleable.SportTextHelper, defStyleAttr, 0)
        val ap = a.getResourceId(R.styleable.SportTextHelper_android_textAppearance, INVALID_ID)

        if (a.hasValue(R.styleable.SportTextHelper_android_drawableLeft)) {
            mDrawableLeftResId =
                a.getResourceId(R.styleable.SportTextHelper_android_drawableLeft, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextHelper_android_drawableTop)) {
            mDrawableTopResId =
                a.getResourceId(R.styleable.SportTextHelper_android_drawableTop, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextHelper_android_drawableRight)) {
            mDrawableRightResId =
                a.getResourceId(R.styleable.SportTextHelper_android_drawableRight, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextHelper_android_drawableEnd)) {
            mDrawableRightResId =
                a.getResourceId(R.styleable.SportTextHelper_android_drawableEnd, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextHelper_android_drawableBottom)) {
            mDrawableBottomResId =
                a.getResourceId(R.styleable.SportTextHelper_android_drawableBottom, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextHelper_android_text)) {
            mTextResId = a.getResourceId(R.styleable.SportTextHelper_android_text, INVALID_ID)
        }
        a.recycle()

        if (ap != INVALID_ID) {
            a = context.obtainStyledAttributes(ap, R.styleable.SportTextAppearance)
            if (a.hasValue(R.styleable.SportTextAppearance_android_textColor)) {
                textColorResId =
                    a.getResourceId(R.styleable.SportTextAppearance_android_textColor, INVALID_ID)
            }
            if (a.hasValue(R.styleable.SportTextAppearance_android_textColorHint)) {
                mTextColorHintResId = a.getResourceId(
                    R.styleable.SportTextAppearance_android_textColorHint, INVALID_ID
                )
            }
            a.recycle()
        }

        // Now read the style's values
        a = context.obtainStyledAttributes(attrs, R.styleable.SportTextAppearance, defStyleAttr, 0)
        if (a.hasValue(R.styleable.SportTextAppearance_android_textColor)) {
            textColorResId =
                a.getResourceId(R.styleable.SportTextAppearance_android_textColor, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextAppearance_android_textColorHint)) {
            mTextColorHintResId = a.getResourceId(
                R.styleable.SportTextAppearance_android_textColorHint, INVALID_ID
            )
        }
        a.recycle()
        updateSkin()
    }

    fun onSetTextAppearance(context: Context, resId: Int) {
        val a = context.obtainStyledAttributes(resId, R.styleable.SportTextAppearance)
        if (a.hasValue(R.styleable.SportTextAppearance_android_textColor)) {
            textColorResId =
                a.getResourceId(R.styleable.SportTextAppearance_android_textColor, INVALID_ID)
        }
        if (a.hasValue(R.styleable.SportTextAppearance_android_textColorHint)) {
            mTextColorHintResId =
                a.getResourceId(R.styleable.SportTextAppearance_android_textColorHint, INVALID_ID)
        }
        a.recycle()
        applyTextColorResource()
        applyTextColorHintResource()
    }

    private fun applyTextColorHintResource() {
        if (checkResourceIdValid(mTextColorHintResId)) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                resourcesManager.getColorStateList(mView.context, mTextColorHintResId)
                    ?.let { mView.setHintTextColor(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun applyTextColorResource() {
        if (checkResourceIdValid(textColorResId)) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                resourcesManager.getColorStateList(mView.context, textColorResId)
                    ?.let { mView.setTextColor(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    open fun onSetCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int,
        @DrawableRes top: Int,
        @DrawableRes end: Int,
        @DrawableRes bottom: Int
    ) {
        mDrawableLeftResId = start
        mDrawableTopResId = top
        mDrawableRightResId = end
        mDrawableBottomResId = bottom
        applyCompoundDrawablesRelativeResource()
    }

    fun onSetCompoundDrawablesWithIntrinsicBounds(
        @DrawableRes left: Int,
        @DrawableRes top: Int,
        @DrawableRes right: Int,
        @DrawableRes bottom: Int
    ) {
        mDrawableLeftResId = left
        mDrawableTopResId = top
        mDrawableRightResId = right
        mDrawableBottomResId = bottom
        applyCompoundDrawablesResource()
    }

    protected open fun applyCompoundDrawablesRelativeResource() {
        applyCompoundDrawablesResource()
    }

    private fun applyCompoundDrawablesResource() {
        var drawableLeft: Drawable? = null
        var drawableTop: Drawable? = null
        var drawableRight: Drawable? = null
        var drawableBottom: Drawable? = null
        if (checkResourceIdValid(mDrawableLeftResId)) {
            drawableLeft = resourcesManager.getDrawable(mView.context, mDrawableLeftResId)
        }
        if (checkResourceIdValid(mDrawableTopResId)) {
            drawableTop = resourcesManager.getDrawable(mView.context, mDrawableTopResId)
        }
        if (checkResourceIdValid(mDrawableRightResId)) {
            drawableRight = resourcesManager.getDrawable(mView.context, mDrawableRightResId)
        }
        if (checkResourceIdValid(mDrawableBottomResId)) {
            drawableBottom =
                resourcesManager.getDrawable(mView.context, mDrawableBottomResId)
        }
        if (mDrawableLeftResId != INVALID_ID
            || mDrawableTopResId != INVALID_ID
            || mDrawableRightResId != INVALID_ID
            || mDrawableBottomResId != INVALID_ID
        ) {
            mView.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                drawableTop,
                drawableRight,
                drawableBottom
            )
        }
    }

    override fun updateSkin() {
        applyCompoundDrawablesRelativeResource()
        applyTextColorResource()
        applyTextColorHintResource()
    }

    override fun updateLanguage(languageCode:String) {
        if(checkResourceIdValid(mTextResId)){
            mView.text = resourcesManager.getTextResourceText(mView.context,mTextResId,languageCode)
        }
    }
}