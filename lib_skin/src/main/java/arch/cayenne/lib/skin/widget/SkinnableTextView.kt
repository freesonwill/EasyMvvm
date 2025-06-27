package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper


class SkinnableTextView : AppCompatTextView {
    private val textHelper = SkinnableTextHelper(this)
    private val backgroundTintHelper = SkinnableBackGroundHelper(this)
    private val flowHelper = SkinnableViewFlowHelper()

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow(findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundTintHelper.updateSkin()
            textHelper.updateSkin()
        }
        flowHelper.startLanguageFlow {
            textHelper.updateLanguage(it)
        }

    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        textHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        backgroundTintHelper.setSrcId(resId)
    }

    override fun setTextAppearance(resId: Int) {
        setTextAppearance(context, resId)
    }

    @Deprecated("Deprecated in Java")
    override fun setTextAppearance(context: Context, resId: Int) {
        super.setTextAppearance(context, resId)
        textHelper.onSetTextAppearance(context, resId)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int,
        @DrawableRes top: Int,
        @DrawableRes end: Int,
        @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        textHelper.onSetCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
    }

    /**
     * 动态代码创建时，获取TexColor ResId
     * */
     fun setTextColorRes(@ColorRes color: Int) {
         textHelper.setTextColor(color)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        @DrawableRes left: Int,
        @DrawableRes top: Int,
        @DrawableRes right: Int,
        @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        textHelper.onSetCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
    }

    fun setTextRes(@StringRes stringRes:Int,vararg formatArgs:Any = emptyArray()){
       textHelper.updateText(stringRes,*formatArgs)
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }
}
