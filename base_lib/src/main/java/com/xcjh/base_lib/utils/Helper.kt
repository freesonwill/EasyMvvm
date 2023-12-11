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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xcjh.base_lib.R
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.manager.KtxActivityManger
import com.xcjh.base_lib.utils.glide.GlideApp
import com.xcjh.base_lib.utils.glide.GlideCircleWithBorder
import com.xcjh.base_lib.utils.glide.RoundTransform
import java.io.File
import java.io.FileOutputStream
import java.util.*


/**
 * 任务完成提示
 */
@SuppressLint("WrongConstant")
fun myToast(whiteStr: String?, yellowStr: String? = null) {
    Handler(Looper.getMainLooper()).post {
        val view: View = LayoutInflater.from(appContext).inflate(R.layout.view_toast_my_task, null)
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
        toast.setGravity( Gravity.CENTER, 0,200)
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


/**
 * 加载网络图片
 *
 */
fun loadImage(
    context: Context,
    url: String?,
    image: ImageView,
    placeholder: Int = R.drawable.default_background,
) {
    GlideApp.with(context).load(url)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .centerCrop()
        .placeholder(placeholder)
        .skipMemoryCache(false)//开启会闪烁
        .dontAnimate()
        .into(image)
}
/**
 * 默认全圆角矩形无边框
 *
 * @param context
 * @param url
 * @param image
 * @param radius dp
 * @param position  0b1111 全圆角 0b0000 全直角
 * @param borderColor
 * @param borderWidth 0无边框
 */
fun loadBorderImg(
    context: Context?,
    url: String?,
    image: ImageView,
    placeholder: Int = R.drawable.default_background,
    //  error: Int = R.drawable.default_cover,
    radius: Int = 0,
    borderColor: String = "#00000000",
    borderWidth: Float = 0f,//dp
    position: Int = 0b1111,
) {
    Glide.with(context!!).load(url)
        .apply(
            RequestOptions()
                .placeholder(placeholder)
                //.error(error)
                .transform(
                    RoundTransform(
                        context, radius, borderWidth,
                        Color.parseColor(borderColor),
                        position
                    )
                )
                .dontAnimate()
        )
        /* .apply(
             RequestOptions().transforms( CenterCrop(), RoundedCorners(radius))
         )*/
        .into(image)
}
/**
 * 圆形图片
 * 默认无边框
 */
fun loadCircleImage(
    context: Context?,
    url: String?,
    image: ImageView,
    placeholder: Int = R.drawable.default_background,
    borderColor: String = "#00000000",
    borderWidth: Float = 0f,//dp

) {
    //带白色边框的圆形图片
    //带白色边框的圆形图片
    Glide.with(context!!).asBitmap()
        .placeholder(placeholder)
        .load(url)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .transform(
            GlideCircleWithBorder(borderWidth, Color.parseColor(borderColor))
        )
        .into(image)
}
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

/**
 * 保存bitmap到相册 ==================================================
 */
fun saveBitmapToAlbum(activity:Activity,bitmap: Bitmap) {
    val appDir = File(activity.externalCacheDir, "image")
    if (!appDir.exists()) {
        appDir.mkdir()
    }
    val fileName = System.currentTimeMillis().toString() + ".jpg"
    val file = File(appDir, fileName)
    try {
        val fos = FileOutputStream(file)
        fos.use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
        }
        //把文件插入到系统相册
        MediaStore.Images.Media.insertImage(
            activity.contentResolver,
            file.absolutePath,
            fileName,
            null
        )
        //通知图库更新
        //activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        activity.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(File(file.getPath()))
            )
        )
        myToast(appContext.getString(R.string.down_ok))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
/**
 * 下载bitmap
 */
fun downImage(bitmap: Bitmap) {
    val uri: Uri = Uri.parse(MediaStore.Images.Media.insertImage(appContext.contentResolver, bitmap, "IMG" + Calendar.getInstance().time, null))
    var intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = "image/*" //设置分享内容的类型
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent = Intent.createChooser(intent, "分享")
    myToast(appContext.getString(R.string.down_ok))
    // KtxActivityManger.currentActivity?.startActivity(intent)
}

/**
 * 高斯模糊
 */
//使用RenderScript
fun rsBlur(
    context: Context,
    scaledBitmap: Bitmap,
    radius: Float,
): Bitmap {
    //创建RenderScript
    val renderScript = RenderScript.create(context)
    //创建Allocation
    val input = Allocation.createFromBitmap(
        renderScript,
        scaledBitmap,
        Allocation.MipmapControl.MIPMAP_NONE,
        Allocation.USAGE_SCRIPT
    )
    val output = Allocation.createTyped(renderScript, input.type)

    //创建ScriptIntrinsic
    val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    intrinsicBlur.setInput(input)
    intrinsicBlur.setRadius(radius)
    intrinsicBlur.forEach(output)
    output.copyTo(scaledBitmap)
    renderScript.destroy()
    return scaledBitmap
}
fun captureScreen(activity: Activity): Bitmap {
    activity.window.decorView.destroyDrawingCache() //先清理屏幕绘制缓存(重要)
    activity.window.decorView.isDrawingCacheEnabled = true
    var bmp = activity.window.decorView.drawingCache
    //获取原图尺寸
    var originalW = bmp.width + 10
    var originalH = bmp.height
    //对原图进行缩小，提高下一步高斯模糊的效率
    bmp = Bitmap.createScaledBitmap(bmp, originalW / 2, originalH / 2, false)
    return bmp
}
