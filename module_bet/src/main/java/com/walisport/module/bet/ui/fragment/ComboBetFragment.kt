package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentComboBetBinding
import com.walisport.module.bet.viewmodel.ComboBetViewModel
import kotlin.reflect.KClass

class ComboBetFragment : BaseFragment<ComboBetViewModel, FragmentComboBetBinding>(), BetSheetListener  {

    override val vbClass: KClass<FragmentComboBetBinding> = FragmentComboBetBinding::class
    override val vmClass: KClass<ComboBetViewModel> = ComboBetViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.tvTest.setOnClickListener {
            dismiss()
        }
    }

    override fun createObserver() {
    }

    override fun dismiss(key: String, value: String) {
        findNavController().getBackStackEntry(R.id.comboBetFragment).savedStateHandle[key] = value
    }
}