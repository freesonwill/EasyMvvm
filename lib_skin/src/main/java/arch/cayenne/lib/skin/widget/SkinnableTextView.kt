package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.ISkinnableTextBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizTextImpl
import java.util.Locale


class SkinnableTextView : AppCompatTextView, ISkinnableBiz {
    private lateinit var biz:ISkinnableTextBiz

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
        biz.onAttachedToWindow()

    }

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        biz = SkinnableBizTextImpl(this)
        biz.initView(context, attrs, defStyleAttr)
    }

    override fun setTextAppearance(resId: Int) {
        biz.setTextAppearance(resId)
    }

    @Deprecated("Deprecated in Java")
    override fun setTextAppearance(context: Context, resId: Int) {
        super.setTextAppearance(context, resId)
        biz.setTextAppearance(context,resId)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int,
        @DrawableRes top: Int,
        @DrawableRes end: Int,
        @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        biz.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
    }

    /**
     * 动态代码创建时，获取TexColor ResId
     * */
     fun setTextColorRes(@ColorRes color: Int) {
         biz.setTextColorRes(color)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        @DrawableRes left: Int,
        @DrawableRes top: Int,
        @DrawableRes right: Int,
        @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        biz.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
    }

    fun setTextRes(@StringRes stringRes:Int,vararg formatArgs:Any = emptyArray()){
       biz.setTextRes(stringRes,*formatArgs)
    }

    fun updateLanguage(locale: Locale) {
        biz.updateLanguage(locale)
    }

    fun setFontWeight(weight: Int) {
        biz.setFontWeight(weight)
    }

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        biz.setBackgroundResource(resId)
    }

    override fun setTintColorRes(@ColorRes resId: Int){
        biz.setTintColorRes(resId)
    }

    override fun setForegroundRes(@AnyRes resId: Int){
        biz.setForegroundRes(resId)
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun updateSkin(msgType: SkinMsgType) {
        biz.updateSkin(msgType)
    }
}
