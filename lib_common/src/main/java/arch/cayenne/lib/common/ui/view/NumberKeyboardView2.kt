package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import arch.cayenne.lib.common.databinding.LayoutNumberKeyboard2Binding
import arch.cayenne.lib.common.ui.view.NumberKeyboardView.OnCalculatorClickListener
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setOnClickOrLongPressListener

/**
 * @date: 2025/11/12 15:20
 * @description:
 */
class NumberKeyboardView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutNumberKeyboard2Binding.inflate(LayoutInflater.from(context), this)
    /***************** listener ************/
    private var onClear: (() -> Unit)? = null
    private var onBackClick: (() -> Unit)? = null
    private var onBackLongPressRepeat: (() -> Unit)? = null
    private var onHideKeyboard: (() -> Unit)? = null
    private var onShowKeyboard: (() -> Unit)? = null


    init {
        binding.btnCollapse.clickNoRepeat {
            hideKeyboard()
        }
        binding.btnBack.setOnClickOrLongPressListener (onClick = {
            this.onBackClick?.invoke()
        }, onLongPressRepeat = {
            this.onBackLongPressRepeat?.invoke()
        })
        binding.btnClear.clickNoRepeat {
            this.onClear?.invoke()
        }
    }

    fun hideKeyboard() {
        ViewUtils.collapseView(binding.root, binding.ivFakerView)
        onHideKeyboard?.invoke()
    }

    fun showKeyBoard(){
        if (!binding.root.isVisible) {
            ViewUtils.expandView(binding.root, binding.ivFakerView)
            onShowKeyboard?.invoke()
        }
    }

    fun setRvQuickAmountAdapter(adapter: Adapter<*>){
        binding.rvQuickAmount.adapter = adapter
    }

    fun setOnHideKeyboardListener(lis:(() -> Unit)?){
        this.onHideKeyboard = lis
    }

    fun setOnShowKeyboardListener(lis:(() -> Unit)?){
        this.onShowKeyboard = lis
    }

    fun setOnCalculatorClickListener(lis:OnCalculatorClickListener){
        binding.numberKeyboard.setOnCalculatorClickListener(lis)
    }

    fun setOnBackListener(onClick: (() -> Unit)? = null,
                          onLongPressRepeat: (() -> Unit)? = null
    ) {
        this.onBackClick = onClick
        this.onBackLongPressRepeat = onLongPressRepeat
    }

    fun setOnClearListener(lis:(() -> Unit)?){
        this.onClear = lis
    }
}