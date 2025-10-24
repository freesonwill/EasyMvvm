package com.walisport.module.topup.ui.widget

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.topup.data.entity.AddressBean
import com.walisport.module.topup.databinding.DialogCopyEditBinding
import kotlin.reflect.KClass

class CopyEditDialog : BaseBottomSheetFragment<EmptyViewModel, DialogCopyEditBinding>() {

    override val vbClass: KClass<DialogCopyEditBinding> get() = DialogCopyEditBinding::class
    override val vmClass: KClass<EmptyViewModel> get() = EmptyViewModel::class
    private var clicklistener: OnItemClickListener? = null

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.tvCopy.clickNoRepeat {
            clicklistener?.onCopy()
            super.dismiss()
        }
        mBinding.tvEdit.clickNoRepeat {
            clicklistener?.onEdit()
            super.dismiss()
        }
        mBinding.tvCancel.clickNoRepeat {
            super.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setWindowAnimations(R.style.CommonDialogAnimation)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clicklistener = listener
    }

    interface OnItemClickListener {
        fun onCopy()
        fun onEdit()
    }

    companion object {
        private const val ARG_IID = "arg_id"

        fun newInstance(
            bean: AddressBean
        ) = CopyEditDialog().apply {
            arguments = Bundle().apply {
                putInt(ARG_IID, bean.id)
            }
        }
    }
}