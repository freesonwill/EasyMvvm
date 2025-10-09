package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.topup.databinding.DialogDelBankCardBinding
import kotlin.reflect.KClass

/**
 * 删除银行卡弹窗
 */

class DelCardDialogFragment :
    BaseBottomSheetFragment<EmptyViewModel, DialogDelBankCardBinding>() {

    override val vbClass: KClass<DialogDelBankCardBinding> get() = DialogDelBankCardBinding::class
    override val vmClass: KClass<EmptyViewModel> get() = EmptyViewModel::class

    private var clicklistener: OnClickListener? = null

    private var id: Int = 0
    private var name: String = ""
    private var number: String = ""

    val idStr: String = "ID"
    val nameStr: String = "NAME"
    val numStr: String = "NUMBER"

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            id = it.getInt(idStr)
            name = it.getString(nameStr).toString()
            number = it.getString(numStr).toString()
            val str = name + " " + getBankMaskNumber(number)
            mBinding.tvBankInfo.text = str
        }
    }

    override fun initListener() {
        mBinding.tvClose.clickNoRepeat {
            super.dismiss()
        }
        mBinding.tvDelete.clickNoRepeat {
            clicklistener?.onClickDelete(id, name, number)
            super.dismiss()
        }
    }

    private fun getBankMaskNumber(bankNum: String): String {
        val length = bankNum.length
        if (length < 4) {
            return bankNum
        }
        return "**** " + bankNum.substring(length - 4, length)
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClickDelete(id: Int, bank: String, num: String)
    }
}