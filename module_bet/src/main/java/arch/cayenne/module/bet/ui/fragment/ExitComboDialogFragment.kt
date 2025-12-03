package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.module.bet.databinding.DialogExitComboBinding
import com.blankj.utilcode.util.ScreenUtils
import kotlin.reflect.KClass

/**
 * @date: 2025/12/3 16:47
 * @description:
 */
class ExitComboDialogFragment: BaseDialogFragment<EmptyViewModel, DialogExitComboBinding>() {
    override val vbClass: KClass<DialogExitComboBinding>
        get() = DialogExitComboBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private var onOkClick: (() -> Unit)? = null
    private var onCancelClick: (() -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.apply {
            tvConfirm.setOnClickListener {
                onOkClick?.invoke()
                dismiss()
            }

            tvCancel.setOnClickListener {
                onCancelClick?.invoke()
                dismiss()
            }
        }
    }

    fun setOnOkClickListener(listener: () -> Unit) {
        onOkClick = listener
    }

    fun setOnCancelClickListener(listener: () -> Unit) {
        onCancelClick = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout((ScreenUtils.getScreenWidth() * 0.9f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            setWindowAnimations(R.style.CommonDialogAnimation)
        }
    }
}