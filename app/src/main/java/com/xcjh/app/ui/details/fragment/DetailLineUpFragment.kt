package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabLiveupBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 * 阵容
 */
class DetailLineUpFragment(var match: MatchDetailBean) :
    BaseVpFragment<DetailVm, FragmentDetailTabLiveupBinding>() {

    override val typeId: Long
        get() = 4

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.layoutFootball.visibleOrGone(match.matchType == "1")
        mDatabind.layoutBasketball.visibleOrGone(match.matchType == "2")
        if ("2" == match.matchType) {//1：足球；2：篮球
            mDatabind.layoutFootball.visibility = View.GONE
            mDatabind.layoutBasketball.visibility = View.VISIBLE
            mDatabind.tvHomeName.text = match.homeName
            mDatabind.tvAwayName.text = match.awayName
            mDatabind.tvAwayName.isSelected = true
            mDatabind.tvHomeName.setOnClickListener {
                mDatabind.tvHomeName.isSelected = true
                mDatabind.tvAwayName.isSelected = false
                mDatabind.viewBasketballHomeLineup.visibility = View.VISIBLE
                mDatabind.viewBasketballAwayLineup.visibility = View.GONE
            }
            mDatabind.tvAwayName.setOnClickListener {
                mDatabind.tvHomeName.isSelected = false
                mDatabind.tvAwayName.isSelected = true
                mDatabind.viewBasketballHomeLineup.visibility = View.GONE
                mDatabind.viewBasketballAwayLineup.visibility = View.VISIBLE
            }
        }
    }

    override fun lazyLoadData() {
        if ("1" == match.matchType) {//1：足球；2：篮球
            mViewModel.getFootballLineUp(match.matchId)
            // mViewModel.getFootballLineUp("3973066")
        } else {
            mViewModel.getBasketballLineUp(match.matchId)
        }
    }

    override fun createObserver() {
        //阵容接口返回监听处理
        mViewModel.foot.observe(this) {
            if (it != null) {
                mDatabind.matchLineup.setHomeTeamInfo(
                    it.homeFormation, it.homeMarketValue + it.homeMarketValueCurrency
                )
                mDatabind.matchLineup.setAwayTeamInfo(
                    it.awayFormation, it.awayMarketValue + it.awayMarketValueCurrency
                )
                mDatabind.matchLineup.setData(it)
                mDatabind.matchTable.setData(it,match)
            }
        }
        mViewModel.basket.observe(this) {
            if (it != null) {
                mDatabind.viewBasketballHomeLineup.setData(it.home)
                mDatabind.viewBasketballAwayLineup.setData(it.away)
            }
        }
    }
}