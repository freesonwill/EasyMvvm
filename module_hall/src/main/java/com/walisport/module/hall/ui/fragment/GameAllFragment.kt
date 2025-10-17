package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.databinding.FragmentGameAllBinding
import com.walisport.module.hall.ui.adapter.GameAllHeaderAdapter
import kotlin.reflect.KClass

class GameAllFragment: BaseFragment<EmptyViewModel, FragmentGameAllBinding>() {
    companion object {
        fun newInstance() = GameAllFragment()
    }

    private val headerAdapter by lazy { GameAllHeaderAdapter() }

    override val vbClass: KClass<FragmentGameAllBinding> = FragmentGameAllBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter = ConcatAdapter(
                headerAdapter
            )
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = concatAdapter
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}