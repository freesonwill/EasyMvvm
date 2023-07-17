package com.xcjh.base_lib.utils.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class CircleImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private var mXfermode: Xfermode? = null
    private lateinit var mPaint: Paint

    constructor(context: Context) : this(context, null) {}

    init {
        mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mPaint = Paint()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        //定义画布的大小，一般情况下，宽跟高是相等的，如果不相等，取小的值。
        val r = if (height > width) width else height
        //创建一张空白的图片
        val bitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888)
        //创建Canvas：如果把Canvas比作画师，那么bitmap就好一张画纸。
        val tempCanvas = Canvas(bitmap)

        //定义背景颜色
        mPaint.color = Color.WHITE
        tempCanvas.drawRect(0f, 0f, r.toFloat(), r.toFloat(), mPaint)
        val drawable = drawable
        if (drawable != null) {
            val drawableW = drawable.intrinsicWidth
            val drawableH = drawable.intrinsicHeight
            //压缩图片：等比例压缩
            val drawableMin = if (drawableH > drawableW) drawableW else drawableH
            val scale = r / drawableMin.toFloat()
            drawable.setBounds(0, 0, (scale * drawableW).toInt(), (scale * drawableH).toInt())
            //第一层...目标层
            drawable.draw(tempCanvas)
        }
        mPaint.reset()
        mPaint.setFilterBitmap(true)
        mPaint.setAntiAlias(true)
        mPaint.setXfermode(mXfermode)
        //第二层...遮罩层
        tempCanvas.drawBitmap(makeSrc(r), 0f, 0f, mPaint)
        mPaint.setXfermode(null)
        //将画好的bitmap图片直接显示出来就行了
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    /**
     * 将该Bitmap作为遮罩层（圆）
     * @param w
     * @param h
     * @return
     */
    private fun makeSrc(r: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        val p = Paint()
        p.color = Color.WHITE
        p.isAntiAlias = true //消除锯齿
        c.drawCircle((r / 2).toFloat(), (r / 2).toFloat(), (r / 2).toFloat(), p)
        return bitmap
    }
}