package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.databinding.FragmentGameAllBinding
import com.walisport.module.hall.ui.adapter.GameAllHeaderAdapter
import com.walisport.module.hall.ui.adapter.GameAllListAdapter
import kotlin.reflect.KClass

class GameAllFragment: BaseFragment<EmptyViewModel, FragmentGameAllBinding>() {
    companion object {
        fun newInstance() = GameAllFragment()
    }

    private val headerAdapter by lazy { GameAllHeaderAdapter() }
    private val listAdapter by lazy { GameAllListAdapter() }


    override val vbClass: KClass<FragmentGameAllBinding> = FragmentGameAllBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter = ConcatAdapter(
                headerAdapter,
                listAdapter
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