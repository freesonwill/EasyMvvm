package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.bet.databinding.FragmentEnterBetBinding
import kotlin.reflect.KClass

class EnterBetFragment : BaseFragment<EmptyViewModel, FragmentEnterBetBinding>() {

    override val vbClass: KClass<FragmentEnterBetBinding> = FragmentEnterBetBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
//        val matchId = requireArguments().getInt(BetSheetFragment.MATCH_ID, -1)
//        if (matchId == -1) {
            findNavController().navigate(EnterBetFragmentDirections.actionEnterBetFragmentToComboBetFragment())
//        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}