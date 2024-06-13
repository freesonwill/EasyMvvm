package com.xcjh.base_lib.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Picture
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xcjh.base_lib.R
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.manager.KtxActivityManger
import java.io.File
import java.io.FileOutputStream
import java.util.*


/**
 * 任务完成提示
 * isDeep是否是深色模式，默认是浅色，当isDeep=true的时候是深色界面使用
 */
@SuppressLint("WrongConstant", "MissingInflatedId")
fun myToast(whiteStr: String?, yellowStr: String? = null,isDeep:Boolean=false,gravity:Int=Gravity.CENTER) {
    Handler(Looper.getMainLooper()).post {
        val view: View = LayoutInflater.from(appContext).inflate(R.layout.view_toast_my_task, null)
        val tvMsg = view.findViewById<View>(R.id.tvToast) as TextView
        val llToastBe = view.findViewById<View>(R.id.llToastBe) as LinearLayout
        var txtColor=ContextCompat.getColor(tvMsg.context, R.color.white)
        if(isDeep){
            txtColor=ContextCompat.getColor(tvMsg.context, R.color.white)
            llToastBe.background=ContextCompat.getDrawable(llToastBe.context,R.drawable.shape_4_ffffff)
        }
        SpanUtil.create()
            .addForeColorSection(whiteStr, txtColor)
            .addForeColorSection(
                yellowStr ?: "",
                ContextCompat.getColor(tvMsg.context, R.color.successColor)
            )
            .showIn(tvMsg) //显示到控件TextView中
        val toast = Toast(appContext)
        // toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, DisplayUtils.dp2px(50f))
        toast.setGravity( gravity, 0,200)
        toast.duration = 5000
        toast.view = view
        toast.show()
    }
    /* val view: View = LayoutInflater.from(appContext).inflate(R.layout.view_toast_my_task, null)
     val tvMsg = view.findViewById<View>(R.id.tvToast) as TextView
     SpanUtil.create()
         .addForeColorSection(whiteStr, ContextCompat.getColor(tvMsg.context, R.color.white))
         .addForeColorSection(
             yellowStr ?: "",
             ContextCompat.getColor(tvMsg.context, R.color.successColor)
         )
         .showIn(tvMsg) //显示到控件TextView中
     val toast = Toast(appContext)
     // toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, DisplayUtils.dp2px(50f))
     toast.setGravity( Gravity.CENTER, 0,0)
     toast.duration = Toast.LENGTH_LONG
     toast.view = view
     toast.show()*/
}


   /* val view: View = LayoutInflater.from(appContext).inflate(R.layout.view_toast_my_task, null)
    val tvMsg = view.findViewById<View>(R.id.tvToast) as TextView
    SpanUtil.create()
        .addForeColorSection(whiteStr, ContextCompat.getColor(tvMsg.context, R.color.white))
        .addForeColorSection(
            yellowStr ?: "",
            ContextCompat.getColor(tvMsg.context, R.color.successColor)
        )
        .showIn(tvMsg) //显示到控件TextView中
    val toast = Toast(appContext)
    // toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, DisplayUtils.dp2px(50f))
    toast.setGravity( Gravity.CENTER, 0,0)
    toast.duration = Toast.LENGTH_LONG
    toast.view = view
    toast.show()*/



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

fun getXXPermissions(activity: Activity,action: () -> Unit = {}){
    XXPermissions.with(activity)
        .permission(Permission.READ_MEDIA_IMAGES)
        .permission(Permission.ACCESS_MEDIA_LOCATION)
       // .permission(Permission.READ_EXTERNAL_STORAGE)
      //  .permission(Permission.WRITE_EXTERNAL_STORAGE)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) {
                    action.invoke()
                }else{
                    XXPermissions.startPermissionActivity(activity, permissions);
                }
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                super.onDenied(permissions, never)
                // gotoAppDetailIntent(this@LoginActivity)
                XXPermissions.startPermissionActivity(activity, permissions);
            }
        })
}

/**
 * 是否获取推送权限
 */
fun getXXPermissionsPush(activity: Activity,action: () -> Unit = {}){
    XXPermissions.with(activity)
        .permission(Permission.POST_NOTIFICATIONS)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) {
                    action.invoke()
                }else{
                    XXPermissions.startPermissionActivity(activity, permissions)
                }
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                super.onDenied(permissions, never)
                // gotoAppDetailIntent(this@LoginActivity)
                XXPermissions.startPermissionActivity(activity, permissions)
            }
        })
}
/**
 * 是否获取电池优化
 */
fun getXXPermissionsBattery(activity: Activity,action: () -> Unit = {}){
    XXPermissions.with(activity)
        .permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (all) {
                    action.invoke()
                }else{
                    XXPermissions.startPermissionActivity(activity, permissions)
                }
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                super.onDenied(permissions, never)
                // gotoAppDetailIntent(this@LoginActivity)
                XXPermissions.startPermissionActivity(activity, permissions)
            }
        })
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



