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

    // 新增变量，控制内圈左右边界的偏移量（像素）
    var innerCircleOffset: Float = 50f // 可在外部设置

    fun setBitmaps(bit: Bitmap?){
        this.bitmap = bit
        bit?.let { setImage(ImageSource.bitmap(it)) }
    }


    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2f
        val centerX = width / 2f
        val centerY = height / 2f
        val left = centerX - radius + borderPaint.strokeWidth / 2 + innerCircleOffset
        val right = centerX + radius - borderPaint.strokeWidth / 2 - innerCircleOffset
        val circleRadius = (right - left) / 2f

        // 先绘制原图内容到画布
        super.onDraw(canvas)

        // --------- 优化磨砂卡顿：只对缩略图做模糊 ---------
        val blurSize = 200 // 模糊层缩略图尺寸，越小越省资源
        val blurBitmap = Bitmap.createBitmap(blurSize, blurSize, Bitmap.Config.ARGB_8888)
        val blurCanvas = Canvas(blurBitmap)
        blurCanvas.scale(blurSize / width, blurSize / height)
        super.onDraw(blurCanvas)

        // 高斯模糊缩略图
        val rs = android.renderscript.RenderScript.create(context)
        val input = android.renderscript.Allocation.createFromBitmap(rs, blurBitmap)
        val output = android.renderscript.Allocation.createTyped(rs, input.type)
        val script = android.renderscript.ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs))
        script.setRadius(18f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(blurBitmap)
        rs.destroy()

        // 放大模糊图层到原尺寸
        val saveLayer = canvas.saveLayer(0f, 0f, width, height, null)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(
            blurBitmap,
            null,
            android.graphics.RectF(0f, 0f, width, height),
            paint
        )
        // 用DST_OUT模式抠出圈内区域
        val maskPath = Path().apply {
            addCircle(centerX, centerY, circleRadius, Path.Direction.CW)
        }
        val clearPaint = Paint().apply {
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_OUT)
            isAntiAlias = true
        }
        canvas.drawPath(maskPath, clearPaint)
        canvas.restoreToCount(saveLayer)

        // 叠加半透明黑色遮罩（圈外）
        val saveLayer2 = canvas.saveLayer(0f, 0f, width, height, null)
        val outsidePaint = Paint().apply {
            color = 0x80000000.toInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width, height, outsidePaint)
        val clearPaint2 = Paint().apply {
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, circleRadius, clearPaint2)
        canvas.restoreToCount(saveLayer2)

        // 绘制白色描边
        canvas.drawCircle(centerX, centerY, circleRadius, borderPaint)
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
            // 计算圆心和半径，支持 innerCircleOffset
            val centerX = width.toFloat() / 2f
            val centerY = height.toFloat() / 2f
            val radius = min(width.toFloat(), height.toFloat()) / 2f - borderPaint.strokeWidth / 2 - innerCircleOffset
            val outputSize = (radius * 2).toInt()
            // 创建输出 Bitmap，尺寸为圆的直径
            val outputBitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            val paint = Paint().apply { isAntiAlias = true }

            // 保存画布状态
            val saveCount = canvas.save()

            // 移动画布，使圆心对齐到输出 Bitmap 的中心
            canvas.translate(-centerX + radius, -centerY + radius)
            // 应用旋转
            canvas.rotate(abs(rotationAngle), centerX, centerY)

            // 裁剪圆形区域
            val clipPath = Path().apply {
                addCircle(centerX, centerY, radius, Path.Direction.CW)
            }
            canvas.clipPath(clipPath)

            // 绘制 SubsamplingScaleImageView 的内容
            draw(canvas)

            // 恢复画布状态
            canvas.restoreToCount(saveCount)

            // 绘制白色描边（在输出 Bitmap 边缘）
            canvas.drawCircle(radius, radius, radius, borderPaint)

            return outputBitmap
        } catch (e: Exception) {
            return null
        }
    }
}