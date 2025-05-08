package arch.cayenne.module.home.ui

import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

abstract class PaintDrawable protected constructor() : Drawable() {
    protected var mPaint = Paint()

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = -0x555556
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.setColorFilter(cf)
    }

    @Deprecated("")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}
