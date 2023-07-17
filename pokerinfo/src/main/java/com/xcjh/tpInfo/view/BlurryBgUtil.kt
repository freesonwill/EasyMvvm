package com.xcjh.tpInfo.view

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread


/**
 * @Description: java类作用描述
 * @author: 小张
 * @date: 2023/2/10
 */
object BlurryBgUtil {
    private var originalW = 0
    private var originalH = 0
    private fun captureScreen(activity: Activity): Bitmap {
        activity.window.decorView.destroyDrawingCache() //先清理屏幕绘制缓存(重要)
        activity.window.decorView.isDrawingCacheEnabled = true
        var bmp = activity.window.decorView.drawingCache
        //获取原图尺寸
        originalW = bmp.width + 10
        originalH = bmp.height
        //对原图进行缩小，提高下一步高斯模糊的效率
        bmp = Bitmap.createScaledBitmap(bmp, originalW / 2, originalH / 2, false)
        return bmp
    }

    fun setScreenBgLight(context: Activity) {
        val window: Window? = context.window
        val lp: WindowManager.LayoutParams
        if (window != null) {
            lp = window.getAttributes()
            lp.dimAmount = 0.1f
            window.setAttributes(lp)
        }
    }

    fun handleBlur(context: Activity, dialogBg: ImageView, mHandler: Handler) {
        var bp: Bitmap? = captureScreen(context)
        bp = blur(bp, context) //对屏幕截图模糊处理
        //将模糊处理后的图恢复到原图尺寸并显示出来
        bp = Bitmap.createScaledBitmap(bp, originalW, originalH, false)
        dialogBg.setImageBitmap(bp)
        dialogBg.setVisibility(View.VISIBLE)
        //防止UI线程阻塞，在子线程中让背景实现淡入效果
        asyncRefresh(true, dialogBg, mHandler)
    }

    fun asyncRefresh(`in`: Boolean, dialogBg: ImageView, mHandler: Handler) {
        //淡出淡入效果的实现
        if (`in`) {    //淡入效果
            Thread {
                var i = 0
                while (i < 256) {
                    refreshUI(i, dialogBg) //在UI线程刷新视图
                    try {
                        Thread.sleep(4)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    i += 5
                }
            }.start()
        } else {    //淡出效果
            Thread {
                var i = 255
                while (i >= 0) {
                    refreshUI(i, dialogBg) //在UI线程刷新视图
                    try {
                        Thread.sleep(4)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    i -= 5
                }
                //当淡出效果完毕后发送消息给mHandler把对话框背景设为不可见
                mHandler.sendEmptyMessage(0)
            }.start()
        }
    }

    fun refreshUI(i: Int, dialogBg: ImageView) {
        runOnUiThread { dialogBg.setImageAlpha(i) }
    }

    fun hideBlur(dialogBg: ImageView, mHandler: Handler) {
        //把对话框背景隐藏
        asyncRefresh(false, dialogBg, mHandler)
        System.gc()
    }

    fun blur(bitmap: Bitmap?, activity: Activity?): Bitmap {
        //使用RenderScript对图片进行高斯模糊处理
        val output = Bitmap.createBitmap(bitmap!!) // 创建输出图片
        val rs = RenderScript.create(activity) // 构建一个RenderScript对象
        val gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)) //
        // 创建高斯模糊脚本
        val allIn = Allocation.createFromBitmap(rs, bitmap) // 开辟输入内存
        val allOut = Allocation.createFromBitmap(rs, output) // 开辟输出内存
        val radius = 10f //设置模糊半径
        gaussianBlue.setRadius(radius) // 设置模糊半径，范围0f<radius<=25f
        gaussianBlue.setInput(allIn) // 设置输入内存
        gaussianBlue.forEach(allOut) // 模糊编码，并将内存填入输出内存
        allOut.copyTo(output) // 将输出内存编码为Bitmap，图片大小必须注意
        rs.destroy()
        //rs.releaseAllContexts(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
        return output
    }
}