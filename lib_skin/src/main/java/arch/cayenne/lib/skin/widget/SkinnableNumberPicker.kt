package arch.cayenne.lib.skin.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.EditText
import android.widget.NumberPicker
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.helper.SkinnableNumberPickerHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.abs

class SkinnableNumberPicker : NumberPicker, ISkinnableBiz {

    private val numberPickerHelper = SkinnableNumberPickerHelper(this)
    private val skinManager: SkinnableManager by inject(SkinnableManager::class.java)
    private val flowHelper = SkinnableViewFlowHelper()

    // 儲存為 SP 值
    private var centerTextSp: Float = 18f
    private var otherTextSp: Float = 15f
    private var centerTextColor: Int = Color.BLACK
    private var otherTextColor: Int = Color.GRAY


    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        numberPickerHelper.loadFromAttributes(attrs, defStyleAttr)
        hidePickerDivider(this)

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.SkinnableNumberPicker, defStyleAttr, 0)
            try {
                // 先檢查是否有 pickerTextAppearance style
                val textAppearanceResId = a.getResourceId(R.styleable.SkinnableNumberPicker_pickerTextAppearance, 0)
                if (textAppearanceResId != 0) {
                    // 從 style 中讀取屬性
                    val ta = context.obtainStyledAttributes(textAppearanceResId, R.styleable.SkinnableNumberPicker)
                    try {
                        val centerTextPx = ta.getDimension(R.styleable.SkinnableNumberPicker_centerTextSize,
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, centerTextSp, resources.displayMetrics))
                        val otherTextPx = ta.getDimension(R.styleable.SkinnableNumberPicker_otherTextSize,
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, otherTextSp, resources.displayMetrics))
                        
                        centerTextSp = centerTextPx / resources.displayMetrics.scaledDensity
                        otherTextSp = otherTextPx / resources.displayMetrics.scaledDensity
                        centerTextColor = ta.getColor(R.styleable.SkinnableNumberPicker_centerTextColor, centerTextColor)
                        otherTextColor = ta.getColor(R.styleable.SkinnableNumberPicker_otherTextColor, otherTextColor)
                    } finally {
                        ta.recycle()
                    }
                }
                
                // 個別屬性會覆蓋 style 中的設定（優先級更高）
                if (a.hasValue(R.styleable.SkinnableNumberPicker_centerTextSize)) {
                    val centerTextPx = a.getDimension(R.styleable.SkinnableNumberPicker_centerTextSize,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, centerTextSp, resources.displayMetrics))
                    centerTextSp = centerTextPx / resources.displayMetrics.scaledDensity
                }
                if (a.hasValue(R.styleable.SkinnableNumberPicker_otherTextSize)) {
                    val otherTextPx = a.getDimension(R.styleable.SkinnableNumberPicker_otherTextSize,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, otherTextSp, resources.displayMetrics))
                    otherTextSp = otherTextPx / resources.displayMetrics.scaledDensity
                }
                if (a.hasValue(R.styleable.SkinnableNumberPicker_centerTextColor)) {
                    centerTextColor = a.getColor(R.styleable.SkinnableNumberPicker_centerTextColor, centerTextColor)
                }
                if (a.hasValue(R.styleable.SkinnableNumberPicker_otherTextColor)) {
                    otherTextColor = a.getColor(R.styleable.SkinnableNumberPicker_otherTextColor, otherTextColor)
                }
            } finally {
                a.recycle()
            }
        }

        setOnScrollListener { picker, _ ->
            picker.performClick()
        }
        
        // 延遲應用樣式，確保子 View 已創建
        post {
            applyTextAppearance(centerTextColor, otherTextColor, centerTextSp, otherTextSp)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow(findViewTreeLifecycleOwner()?.lifecycleScope) {
            numberPickerHelper.updateSkin()
        }
        // 確保樣式被應用（處理動態添加的情況）
        post {
            applyTextAppearance(centerTextColor, otherTextColor, centerTextSp, otherTextSp)
        }
    }

    private fun applyTextAppearance(centerColor: Int, otherColor: Int, centerSizeSp: Float, otherSizeSp: Float) {
        try {
            val count = this.childCount
            for (i in 0 until count) {
                val child = this.getChildAt(i)
                if (child is EditText) {
                    Log.d("abcd", "applyTextAppearance: centerSp=$centerSizeSp otherSp=$otherSizeSp")
                    // EditText.setTextSize() 預設單位是 SP
                    child.setTextSize(TypedValue.COMPLEX_UNIT_SP, centerSizeSp)
                    child.setTextColor(centerColor)
                }
            }

            // 反射取得滚轮文字 Paint
            val pickerFields = NumberPicker::class.java.declaredFields
            for (field in pickerFields) {
                if (field.name == "mSelectorWheelPaint") {
                    field.isAccessible = true
                    val paint = field.get(this) as Paint
                    // Paint 需要 px，所以轉換 SP -> px
                    paint.textSize = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,
                        otherSizeSp,
                        this.resources.displayMetrics
                    )
                    paint.color = otherColor
                    this.invalidate()
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun hidePickerDivider(picker: NumberPicker) {
        if (Build.VERSION.SDK_INT >= 29) {
            picker.selectionDividerHeight = 0
        } else {
            try {
                val pickerFields = NumberPicker::class.java.declaredFields
                for (field in pickerFields) {
                    if ("mSelectionDivider" == field.name) {
                        field.isAccessible = true
                        field.set(picker, ColorDrawable(Color.TRANSPARENT))
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }

    override fun setTintColorRes(resId: Int) {
        TODO("Not yet implemented")
    }

    override fun setForegroundRes(resId: Int) {
        TODO("Not yet implemented")
    }

    override fun updateSkin(msgType: SkinMsgType) {
        numberPickerHelper.updateSkin(msgType)
    }
}
