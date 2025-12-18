package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.data.GameAllRankingToday
import com.walisport.module.hall.databinding.FragmentGameAllRankingTodayBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListTodayAdapter
import kotlin.reflect.KClass

class GameAllRankingTodayFragment :
    BaseFragment<EmptyViewModel , FragmentGameAllRankingTodayBinding>() {
    companion object {
        fun newInstance() = GameAllRankingTodayFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingTodayBinding> =
        FragmentGameAllRankingTodayBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    val mockList: List<GameAllRankingToday> by lazy {
        val l = arrayListOf<GameAllRankingToday>()
        for (i in 0..11) {
            l.add(
                GameAllRankingToday.GameAllRankingTodayData(
                    rank = i + 1 ,
                    playerName = "核弹少年团" ,
                    symbol = "¥" ,
                    betting = 23001212.22 ,
                    bonus = 25000.0,
                    myself = false
                )
            )
        }
        l.add(l.size - 2 , GameAllRankingToday.GameAllRankingDashData)
        l
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvTodayRank.layoutManager = LinearLayoutManager(requireContext())
            rvTodayRank.itemAnimator = null
            rvTodayRank.adapter = GameAllRankingListTodayAdapter().apply {
                submitList(mockList)
            }
        }
    }

    override fun initListener() {
        mBinding.clBanner.clickNoRepeat {
            navigate(arch.cayenne.lib.res.R.string.nav_module_competition_fragment.deeplink())
        }
    }

    override suspend fun createObserver() {
    }


}