package arch.cayenne.module.account.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.util.AttributeSet
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlin.math.min
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.IOException
import kotlin.math.abs

class CircularPhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SubsamplingScaleImageView(context, attrs) {
    private var rotationAngle = 0f // 记录当前旋转角度
    private val clipPath = Path()
    private var bitmap: Bitmap? = null
    private val borderPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt() // 白色
        style = Paint.Style.STROKE
        strokeWidth = 4f // 描边宽度（像素）
        isAntiAlias = true
    }

    fun setBitmaps(bit: Bitmap?){
        this.bitmap = bit
        bit?.let { setImage(ImageSource.bitmap(it)) }
    }


    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2f
        // 裁剪圆形区域
        clipPath.reset()
        clipPath.addCircle(width / 2f, height / 2f, radius - borderPaint.strokeWidth / 2, Path.Direction.CW)
        canvas.clipPath(clipPath)

        // 绘制图像
        super.onDraw(canvas)

        // 恢复画布（避免裁剪影响描边）
        canvas.restoreToCount(canvas.save())

        // 绘制白色描边
        canvas.drawCircle(width / 2f, height / 2f, radius - borderPaint.strokeWidth / 2, borderPaint)
    }


     fun rotate() {
         bitmap?.let {
             var bit = rotateBitmap(it)
             setImage(ImageSource.bitmap(bit))
             this.bitmap = bit
             invalidate()
         }
    }

    /**
     * 旋转图片
     */
   private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postRotate(90f) // 设置旋转角度（顺时针，单位：度）
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    /**
     * 截取圆形控件内容为 Bitmap
     * @return 包含放大、旋转、圆形裁剪和白色描边的 Bitmap，若失败则返回 null
     */
    fun captureCircularContentAsBitmap(): Bitmap? {
        if (!isReady || sWidth == 0 || sHeight == 0 || width == 0 || height == 0) {
            return null
        }

        try {
            // 创建输出 Bitmap，尺寸与控件一致
            val outputBitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(outputBitmap)
            val paint = Paint().apply { isAntiAlias = true }

            // 保存画布状态
            val saveCount = canvas.save()

            // 应用旋转
            canvas.rotate(abs(rotationAngle), width.toFloat() / 2f, height.toFloat() / 2f)

            // 裁剪圆形区域
            clipPath.reset()
            val radius = min(width.toFloat(), height.toFloat()) / 2f - borderPaint.strokeWidth / 2
            clipPath.addCircle(width.toFloat() / 2f, height.toFloat() / 2f, radius, Path.Direction.CW)
            canvas.clipPath(clipPath)

            // 绘制 SubsamplingScaleImageView 的内容
            draw(canvas)

            // 恢复画布状态
            canvas.restoreToCount(saveCount)

            // 绘制白色描边
            canvas.drawCircle(width.toFloat() / 2f, height.toFloat() / 2f, radius, borderPaint)

            return outputBitmap
        } catch (e: Exception) {
            return null
        }
    }
}