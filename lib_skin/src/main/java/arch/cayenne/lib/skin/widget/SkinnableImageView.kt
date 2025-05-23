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
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableImageHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


class SkinnableImageView : AppCompatImageView{
    private val backgroundHelper = SkinnableBackGroundHelper(this)
    private val imageHelper = SkinnableImageHelper(this)
    private var mPaint: Paint? = null
    private var mRectF: RectF? = null
    private var mBitmapShader: BitmapShader? = null
    private val sportSkinManager: SkinnableManager by inject(SkinnableManager::class.java)

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
        this.findViewTreeLifecycleOwner()?.lifecycleScope?.apply {
            launch {
                sportSkinManager.skinFlow.collect {
                    backgroundHelper.updateSkin()
                    imageHelper.updateSkin()
                }
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundHelper.loadFromAttributes(attrs, defStyleAttr)
        imageHelper.loadFromAttributes(attrs, defStyleAttr)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectF = RectF()
    }


    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        backgroundHelper.setSrcId(resId)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        imageHelper.setSrcId(resId)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if(imageHelper.getRadius().toInt() != 0){
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
                canvas.drawRoundRect(mRectF!!, imageHelper.getRadius(), imageHelper.getRadius(), mPaint!!)
            } catch (e: ClassCastException) {
                super.onDraw(canvas)
            }
        }else{
            super.onDraw(canvas)
        }
    }

}