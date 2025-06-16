package com.walisport.module.setting.ui.dialog

import android.os.Bundle
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.databinding.DialogMatchNoticeBinding
import kotlin.reflect.KClass

class MatchNoticeDialog : BaseBottomSheetFragment<EmptyViewModel, DialogMatchNoticeBinding>() {

    override val vbClass: KClass<DialogMatchNoticeBinding>
        get() = DialogMatchNoticeBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private var clicklistener: OnClickListener? = null

    val betValue: String = "betValue"
    val favValue: String = "favValue"
    val allValue: String = "allValue"

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            val betToggle = it.getBoolean(betValue)
            val favToggle = it.getBoolean(favValue)
            val allToggle = it.getBoolean(allValue)
            mBinding.toggleBet.isChecked = betToggle
            mBinding.toggleFav.isChecked = favToggle
            mBinding.toggleAll.isChecked = allToggle
        }
    }

    override fun initListener() {
        mBinding.toggleBet.setOnCheckedChangeListener { _, isChecked ->
            clicklistener?.onClickBet(isChecked)
        }
        mBinding.toggleFav.setOnCheckedChangeListener { _, isChecked ->
            clicklistener?.onClickFav(isChecked)
        }
        mBinding.toggleAll.setOnCheckedChangeListener { _, isChecked ->
            clicklistener?.onClickAll(isChecked)
        }
        mBinding.tvClose.clickNoRepeat {
            clicklistener?.onClickClose()
        }
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onClickBet(isChecked: Boolean)
        fun onClickFav(isChecked: Boolean)
        fun onClickAll(isChecked: Boolean)
        fun onClickClose()
    }
}