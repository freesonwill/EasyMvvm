package arch.cayenne.lib.skin.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.NumberPicker
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.helper.SkinnableNumberPickerHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SkinnableNumberPicker : NumberPicker {

    private val numberPickerHelper = SkinnableNumberPickerHelper(this)
    private val skinManager: SkinnableManager by inject(SkinnableManager::class.java)
    private val flowHelper = SkinnableViewFlowHelper()

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

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        numberPickerHelper.loadFromAttributes(attrs, defStyleAttr)
        hidePickerDivider(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
       flowHelper.startSkinFlow {
           numberPickerHelper.updateSkin()
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
}
