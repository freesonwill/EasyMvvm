package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.xcjh.app.R
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabLiveupBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.fragment.liveup.BasketballFragment
import com.xcjh.app.ui.details.fragment.result.FootballFragment
import com.xcjh.base_lib.utils.bindBgViewPager2
import com.xcjh.base_lib.utils.initFragment
import com.xcjh.base_lib.utils.view.visibleOrGone
import com.xcjh.base_lib.utils.view.visibleOrInvisible

/**
 * 阵容
 */
class DetailLineUpFragment(var match: MatchDetailBean) :
    BaseVpFragment<DetailVm, FragmentDetailTabLiveupBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override val typeId: Long
        get() = 4

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.layoutFootball.visibleOrGone(match.matchType == "1")
        mDatabind.layoutBasketball.visibleOrGone(match.matchType == "2")
        mDatabind.viewPager.visibleOrGone(match.matchType == "2")
        if ("2" == match.matchType) {//1：足球；2：篮球
            mDatabind.viewPager.initFragment(this, arrayListOf(BasketballFragment(0), BasketballFragment(1)),isUserInputEnabled=false)
            mDatabind.magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg)
            mDatabind.magicIndicator.bindBgViewPager2(
                mDatabind.viewPager,
                arrayListOf(match.homeName?:"", match.awayName?:""),
                selectSize = 13f,
                unSelectSize = 13f,
                selectColor = com.xcjh.base_lib.R.color.white,
                normalColor = R.color.c_94999f,
                typefaceBold = true,
                scrollEnable = false,
                lineIndicatorColor = R.color.c_323235,
            )
            mDatabind.viewPager.offscreenPageLimit = 2
        }
        loadData()
    }
    override fun lazyLoadData() {
        //loadData()
    }
    private fun loadData() {
        if ("1" == match.matchType) {//1：足球；2：篮球
            vm.getFootballLineUp(match.matchId)
        } else {
            vm.getBasketballLineUp(match.matchId)
        }
    }

    override fun createObserver() {
        //阵容接口返回监听处理
        vm.foot.observe(this) { it ->
            if (it != null) {
                mDatabind.matchLineup.setData(it,match)
                val home = it.home.filter {
                    it.first == 0
                }
                val away = it.away.filter {
                    it.first == 0
                }
                mDatabind.matchTable.visibleOrGone(!(home.size==away.size && home.isEmpty()))
                mDatabind.matchTable.setData(it,0)
            }
        }
    }
}