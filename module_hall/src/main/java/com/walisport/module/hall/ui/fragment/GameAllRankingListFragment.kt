package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.databinding.FragmentGameAllRankingListBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListAdapter
import kotlin.reflect.KClass

class GameAllRankingListFragment : BaseFragment<EmptyViewModel, FragmentGameAllRankingListBinding>() {
    companion object {
        fun newInstance() = GameAllRankingListFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingListBinding> = FragmentGameAllRankingListBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    val mockList by lazy {
        val l = arrayListOf<GameAllRankingListData>()
        for (i in 0..9) {
            l.add(
                GameAllRankingListData(
                    gameIcon = R.drawable.ic_little_tiger,
                    gameName = getString(R.string.tiger),
                    multiple = i * 20f,
                    countryIcon = arch.cayenne.lib.common.R.drawable.ic_usdt,
                    symbol = "¥",
                    result = 23456f
                )
            )
        }
        l
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvCurrentRank.layoutManager = LinearLayoutManager(requireContext())
            rvCurrentRank.adapter = GameAllRankingListAdapter().apply {
                submitList(mockList)
            }

        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }


}