package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabLiveupBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.fragment.liveup.BasketballFragment
import com.xcjh.app.ui.details.fragment.result.FootballFragment
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
            mDatabind.tvHomeName.text = match.homeName?:""
            mDatabind.tvAwayName.text = match.awayName?:""
            mDatabind.tvAwayName.isSelected = true
            mDatabind.tvAwayName.setOnClickListener {
                changeUI(0)
            }
            mDatabind.tvHomeName.setOnClickListener {
                changeUI(1)
            }
            mDatabind.viewPager.initFragment(
                this, arrayListOf(BasketballFragment(0), BasketballFragment(1)),true
            )
            mDatabind.viewPager.offscreenPageLimit = 2
            mDatabind.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    changeUI(position)
                }
            })
        }
        loadData()
    }
    private fun changeUI(pos: Int) {
        mDatabind.viewPager.currentItem = pos
        mDatabind.tvAwayName.isSelected = pos == 0
        mDatabind.tvHomeName.isSelected = pos == 1
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
                mDatabind.lltTb.visibleOrGone(!(home.size==away.size && home.isEmpty()))
                mDatabind.matchTable.setData(it,match,0)
            }
        }
    }
}