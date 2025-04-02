package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import android.text.InputType
import com.walisport.lib_base.ui.BaseBottomSheetFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.bet.databinding.FragmentBetSheetBinding
import com.walisport.module.bet.viewmodel.BetSheetViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BetSheetFragment : BaseBottomSheetFragment<FragmentBetSheetBinding>() {

    override val mBinding: FragmentBetSheetBinding by viewBind()
    private val mViewModel: BetSheetViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.etMoney.setRawInputType(InputType.TYPE_NULL)
        mBinding.etMoney.setTextIsSelectable(true)
    }

    override fun initListener() {

    }
}