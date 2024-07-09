package com.xcjh.base_lib2.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Picture
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.cn.game.sdk2.R
import com.xcjh.base_lib2.appContext
import com.xcjh.base_lib2.manager.KtxActivityManger
import java.util.*


//5bcf148dcbd6cd46
//3e5ca2f776766455
@SuppressLint("HardwareIds")
fun getUUID(): String? {
    val androidID = Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
    //"uuid=$androidID".loge("=====")
    return androidID
}

/**
 * 分享bitmap
 */
fun shareImage(bitmap: Bitmap) {
    val uri: Uri = Uri.parse(MediaStore.Images.Media.insertImage(appContext.contentResolver, bitmap, "IMG" + Calendar.getInstance().time, null))
    var intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = "image/*" //设置分享内容的类型
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent = Intent.createChooser(intent, "分享")
    KtxActivityManger.currentActivity?.startActivity(intent)
}
fun shareText(context: Context, shareText: String?) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, shareText)
    context.startActivity(Intent.createChooser(intent, "分享"))
}
/**
 * View转Bitmap==================================================
 */
fun loadBitmapFromView(v: View): Bitmap {
    v.height.toString().loge("height====")
    val w = v.width
    val h = v.height
   /* val w = 400
    val h = 200*/
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val c =  Canvas(bmp)
    c.drawPicture(Picture())
    c.drawColor(Color.WHITE)
    /** 如果不设置canvas画布为白色，则生成透明  */
    v.layout(0, 0, w, h)
    v.draw(c)
    return bmp
}
/**
 * View转Bitmap
 */
fun convertViewToBitmap(view: View): Bitmap {
    view.destroyDrawingCache()
    view.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    //view.isDrawingCacheEnabled = true
    return view.drawingCache
}

/**
 * 截取scrollview的屏幕
 * @param scrollView
 * @return
 */
fun getBitmapByView(scrollView: ScrollView): Bitmap {
    var h = 0
    var bitmap: Bitmap? = null
    // 获取scrollview实际高度
    for (i in 0 until scrollView.childCount) {
        h += scrollView.getChildAt(i).height
        scrollView.getChildAt(i).setBackgroundColor(
            Color.parseColor("#ffffff")
        )
    }
    // 创建对应大小的bitmap
    bitmap = Bitmap.createBitmap(
        scrollView.width, h,
        Bitmap.Config.RGB_565
    )
    val canvas = Canvas(bitmap)
    scrollView.draw(canvas)
    return bitmap
}

/**
 * view转bitmap
 *
 * @param view view
 * @return Bitmap
 */
 fun createBitmapByView(activity: Activity,view: View): Bitmap {
    //计算设备分辨率
    view.measuredHeight.toString().loge("====")
    val manager: WindowManager = activity.windowManager
    val metrics = DisplayMetrics()
    manager.defaultDisplay.getMetrics(metrics)
    val width = metrics.widthPixels
    val height = 10000

    //测量使得view指定大小
    val measureWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val measureHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
    view.measure(measureWidth, measureHeight)
    //调用layout方法布局后，可以得到view的尺寸
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(ContextCompat.getColor(activity,R.color.white_ee))
    view.draw(canvas)
    return bitmap
}



