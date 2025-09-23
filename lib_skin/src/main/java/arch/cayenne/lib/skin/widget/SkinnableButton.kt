package arch.cayenne.lib.skin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.ISkinnableTextBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizTextImpl


@SuppressLint("CustomViewStyleable")
class SkinnableButton : AppCompatButton, ISkinnableBiz {
    private lateinit var biz: ISkinnableTextBiz

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
    )
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

    override fun setTextAppearance(context: Context, resId: Int) {
        super.setTextAppearance(context, resId)
        biz.setTextAppearance(context, resId)
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

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        biz.setBackgroundResource(resId)
    }

    fun setHintRes(@StringRes stringRes: Int,vararg formatArgs:Any = emptyArray()){
        biz.setHintRes(stringRes,*formatArgs)
    }

    override fun setTintColorRes(@ColorRes resId: Int){
        biz.setTintColorRes(resId)
    }

    override fun setForegroundRes(@AnyRes resId: Int){
        biz.setForegroundRes(resId)
    }

    /**
     * 动态代码创建时，获取TexColor ResId
     * */
    fun setTextColorRes(@ColorRes color: Int) {
        biz.setTextColorRes(color)
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun forceUpdateSkin() {
        biz.forceUpdateSkin()
    }


}
