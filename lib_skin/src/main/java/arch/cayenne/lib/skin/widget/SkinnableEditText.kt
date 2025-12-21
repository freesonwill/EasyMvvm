package arch.cayenne.lib.skin.widget

import android.content.Context
import android.content.res.TypedArray
import android.text.SpannableString
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.ISkinnableTextBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizTextImpl

open class SkinnableEditText : AppCompatEditText, ISkinnableBiz {
    private lateinit var biz:ISkinnableTextBiz
    //hint光标间距
    private var hintCursorGap: Int = 0

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        biz.onAttachedToWindow()
    }

    final override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        biz = SkinnableBizTextImpl(this)
        biz.initView(context,attrs,defStyleAttr)

        // 读取自定义属性
        /*var ta: TypedArray? = null
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.SkinnableEditText)
            val gap = ta.getDimensionPixelSize(R.styleable.SkinnableEditText_hintCursorGap, 0)
            hintCursorGap = gap
            applyHintGap(hint, gap)
        } finally {
            ta?.recycle()
        }*/
    }


    override fun setTextAppearance(resId: Int) {
        biz.setTextAppearance(context, resId)
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

    /**
     * 动态代码创建时，获取TexColor ResId
     * */
    fun setTextColorRes(@ColorRes color: Int) {
        biz.setTextColorRes(color)
    }

    fun setTextHitRes(@StringRes stringRes:Int,vararg formatArg:Any = emptyArray()){
        biz.setHintRes(stringRes,*formatArg)
    }


    override fun setBackgroundResource(@DrawableRes resId: Int) {
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

    //通过这个hintCursor设置hint，hintCursorGap才会有效果
    var hintCursor: CharSequence
        get( ) = hint
        set(v){
            applyHintGap(v,hintCursorGap)
        }

    private fun applyHintGap(v:CharSequence?, gapPx:Int){
        if (gapPx <= 0 || v.isNullOrEmpty()) return
        val ss = SpannableString(v)
        /**
         * LeadingMarginSpan.Standard 是一个文本样式 (Span) ，用于为段落添加前导缩进（左侧空白）。它可以设置首行与后续行的不同缩进，从而在不改变控件内边距的情况下，让文本整体右移或仅让首行右移。
         * 构造参数：LeadingMarginSpan.Standard(first, rest)
         * first：首行缩进像素值
         * rest：除首行外的其余行缩进像素值
         * 适用范围：可用于 TextView/EditText 的文本或 hint，不影响光标位置，只影响展示
         */
        ss.setSpan(LeadingMarginSpan.Standard(gapPx, 0), 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        this.hint = ss
    }
}
