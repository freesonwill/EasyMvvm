package arch.cayenne.lib.skin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.ISkinnableImageBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizImageImpl


class SkinnableImageView : AppCompatImageView, ISkinnableBiz{
    private var mPaint: Paint? = null
    private var mRectF: RectF? = null
    private var mBitmapShader: BitmapShader? = null
    private lateinit var biz:ISkinnableImageBiz
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
        biz = SkinnableBizImageImpl(this)
        biz.initView(context, attrs, defStyleAttr)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectF = RectF()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        biz.setImageResource(resId)
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

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if(biz.getRadius().toInt() != 0){
            val drawable = drawable
            if (drawable == null) {
                super.onDraw(canvas)
                return
            }
            try {
                val bitmap = (drawable as BitmapDrawable).bitmap
                mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                mPaint?.setShader(mBitmapShader)
                mRectF?.set(0f,0f,width.toFloat(),height.toFloat())
                canvas.drawRoundRect(mRectF!!, biz.getRadius(), biz.getRadius(), mPaint!!)
            } catch (e: ClassCastException) {
                super.onDraw(canvas)
            }
        }else{
            super.onDraw(canvas)
        }
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun forceUpdateSkin() {
        biz.forceUpdateSkin()
    }

}