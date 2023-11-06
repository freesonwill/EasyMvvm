package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.databinding.FragmentDetailTabIndexBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 * 指数
 */

class DetailIndexFragment(var matchId: String = "", var matchType: String = "1") :
    BaseVpFragment<DetailVm, FragmentDetailTabIndexBinding>() {

    override val typeId: Long
        get() = 5

    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }

    override fun lazyLoadData() {
        //ViewModelProvider.get()
        mViewModel.getOddsInfo(matchId)
    }

    override fun initView(savedInstanceState: Bundle?) {

        if ("1" == matchType) {//1：足球；2：篮球，这个版本篮球暂时不做，因为没有数据
            mDatabind.layTabIndexFootball.visibility = View.VISIBLE
            mDatabind.tvTabIndexSf.isSelected = true
        } else {
            mDatabind.layTabIndexFootball.visibility = View.GONE
        }
        mDatabind.tvTabIndexSf.setOnClickListener {
            changeUI(pos = 1)
        }
        mDatabind.tvTabIndexRq.setOnClickListener {
            changeUI(pos = 2)
        }
        mDatabind.tvTabIndexJq.setOnClickListener {
            changeUI(pos = 3)
        }

    }

    private fun changeUI(pos: Int) {
        mDatabind.tvTabIndexSf.isSelected = pos == 1
        mDatabind.tvTabIndexRq.isSelected = pos == 2
        mDatabind.tvTabIndexJq.isSelected = pos == 3
        mDatabind.viewSFTable.visibleOrGone(pos == 1)
        mDatabind.viewRQTable.visibleOrGone(pos == 2)
        mDatabind.viewJQTable.visibleOrGone(pos == 3)
    }

    override fun createObserver() {
        //指数接口返回监听处理，
        mViewModel.odds.observe(this) {
            if (it != null) {
                mDatabind.viewSFTable.setData(it.euInfo)//胜平负
                mDatabind.viewRQTable.setData(it.asiaInfo)//让球
                mDatabind.viewJQTable.setData(it.bsInfo)//进球数
            }
        }
        //appViewModel.appPolling.observeForever {
        appViewModel.appPolling.observe(activity as MatchDetailActivity) {
            if (isAdded && !isFirst) {
                lazyLoadData()
            }
        }

    }
}