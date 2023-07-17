package com.xcjh.base_lib.utils.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.renderscript.RenderScript
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import java.lang.Exception

/**
 * @author Administrator
 */
@SuppressLint("AppCompatCustomView")
class BlurBGImageView : ImageView {
    var overlay: Bitmap? = null
    var scaleFactor = 2
    var radius = 8

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @JvmName("setScaleFactor1")
    fun setScaleFactor(scaleFactor: Int) {
        this.scaleFactor = scaleFactor
    }

    fun refreshBG(bgView: View) {
        bgView.isDrawingCacheEnabled = true
        bgView.buildDrawingCache()
        var bitmap1: Bitmap? = null
        try {
            bitmap1 = getBitmap(bgView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (bitmap1 != null) {
            blur(bitmap1, this, radius.toFloat()) //模糊处理
            bitmap1.recycle()
        }
        bgView.isDrawingCacheEnabled = false //清除缓存
    }

    private fun blur(bkg: Bitmap, view: ImageView, radius: Float) {
        if (overlay != null) {
            overlay!!.recycle()
        }
        overlay =
            Bitmap.createScaledBitmap(bkg, bkg.width / scaleFactor, bkg.height / scaleFactor, false)
        overlay = blur(context, overlay, radius) //高斯模糊
        view.setImageBitmap(overlay)
    }

    private fun blur(context: Context, image: Bitmap?, radius: Float): Bitmap {
        val rs = RenderScript.create(context)
        val outputBitmap = Bitmap.createBitmap(image!!.width, image.height, Bitmap.Config.ARGB_8888)
        val `in` = Allocation.createFromBitmap(rs, image)
        val out = Allocation.createFromBitmap(rs, outputBitmap)
        val intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        intrinsicBlur.setRadius(radius)
        intrinsicBlur.setInput(`in`)
        intrinsicBlur.forEach(out)
        out.copyTo(outputBitmap)
        image.recycle()
        rs.destroy()
        return outputBitmap
    }

    private fun getBitmap(view: View): Bitmap? {
        //获取屏幕整张图片
        var bitmap = view.drawingCache
        if (bitmap != null) {

            //需要截取的长和宽
            val outWidth = this.width
            val outHeight = this.height

            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            val viewLocationArray = IntArray(2)
            getLocationOnScreen(viewLocationArray)
            val listLocationArray = IntArray(2)
            view.getLocationOnScreen(listLocationArray)

            //从屏幕整张图片中截取指定区域
            bitmap = Bitmap.createBitmap(
                bitmap,
                viewLocationArray[0] - listLocationArray[0],
                viewLocationArray[1] - listLocationArray[1],
                outWidth,
                outHeight
            )
        }
        return bitmap
    }
}