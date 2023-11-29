package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.databinding.FragmentDetailTabIndexBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.details.fragment.index.Index1Fragment
import com.xcjh.app.ui.details.fragment.index.Index2Fragment
import com.xcjh.app.ui.details.fragment.index.Index3Fragment
import com.xcjh.base_lib.utils.initFragment

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
        //loadData()
    }

    private fun loadData() {
        //ViewModelProvider.get()
        vm.getOddsInfo(matchId)
    }

    override fun initView(savedInstanceState: Bundle?) {

        if ("1" == matchType) {//1：足球；2：篮球，这个版本篮球暂时不做，因为没有数据
            //mDatabind.layTabIndexFootball.visibility = View.VISIBLE
            mDatabind.tvTabIndexSf.isSelected = true
            mDatabind.viewPager.initFragment(
                this,
                arrayListOf(Index1Fragment(), Index2Fragment(), Index3Fragment())
            )
            mDatabind.viewPager.offscreenPageLimit = 3
            mDatabind.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    changeUI(pos = position + 1)
                }
            })
        } else {
            //mDatabind.layTabIndexFootball.visibility = View.GONE
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
        loadData()
    }

    private fun changeUI(pos: Int) {
        mDatabind.viewPager.currentItem = pos-1
        mDatabind.tvTabIndexSf.isSelected = pos == 1
        mDatabind.tvTabIndexRq.isSelected = pos == 2
        mDatabind.tvTabIndexJq.isSelected = pos == 3
    }

    override fun createObserver() {
        //appViewModel.appPolling.observeForever {
        appViewModel.appPolling.observe(activity as MatchDetailActivity) {
            if (isAdded && !isFirst) {
                loadData()
            }
        }

    }
}