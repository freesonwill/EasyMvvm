package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.DialogBottomBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import kotlin.reflect.KClass

class CommonBottomDialog : BaseBottomSheetFragment<EmptyViewModel, DialogBottomBinding>() {

    override val vbClass: KClass<DialogBottomBinding> get() = DialogBottomBinding::class
    override val vmClass: KClass<EmptyViewModel> get() = EmptyViewModel::class
    private var clicklistener: OnClickListener? = null
    private var title: String? = null
    private var iid: Long = 0

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            title = it.getString(ARG_TITLE) ?: ""
            iid = it.getLong(ARG_IID)
        }
        with(mBinding) {
            tvDialogTitle.text = title
            tvConfirm.clickNoRepeat {
                clicklistener?.onClick(iid)
                dismiss()
            }
            tvCancel.clickNoRepeat {
                dismiss()
            }
        }
    }

    override fun initListener() {
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setWindowAnimations(R.style.CommonDialogAnimation)
            //setLayout(280f.dp2px, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClick(id: Long)
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_IID = "arg_id"

        fun newInstance(
            title: String,
            id: Long
        ) = CommonBottomDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putLong(ARG_IID, id)
            }
        }
    }
}