package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabLiveupBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.base_lib.utils.view.visibleOrGone
import com.xcjh.base_lib.utils.view.visibleOrInvisible

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
            mDatabind.tvHomeName.text = match.homeName?:""
            mDatabind.tvAwayName.text = match.awayName?:""
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
        loadData()
    }

    override fun lazyLoadData() {
        //loadData()
    }
    private fun loadData() {
        if ("1" == match.matchType) {//1：足球；2：篮球
            mViewModel.getFootballLineUp(match.matchId)
            // mViewModel.getFootballLineUp("3973066")
        } else {
            mViewModel.getBasketballLineUp(match.matchId)
        }
    }

    override fun createObserver() {
        //阵容接口返回监听处理
        mViewModel.foot.observe(this) { it ->
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
        mViewModel.basket.observe(this) {
            if (it != null) {
                mDatabind.viewBasketballHomeLineup.setData(it.home)
                mDatabind.viewBasketballAwayLineup.setData(it.away)
            }
        }
    }
}