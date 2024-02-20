package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.databinding.FragmentDetailTabIndexBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.details.fragment.index.Index1Fragment
import com.xcjh.app.ui.details.fragment.index.Index2Fragment
import com.xcjh.base_lib.utils.bindBgViewPager2
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


    override fun initView(savedInstanceState: Bundle?) {

        if ("1" == matchType) {//1：足球；2：篮球，这个版本篮球暂时不做，因为没有数据
            mDatabind.viewPager.initFragment(this, arrayListOf(Index1Fragment(), Index2Fragment(1), Index2Fragment(2)),isUserInputEnabled=true)
            mDatabind.magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg)
            mDatabind.magicIndicator.bindBgViewPager2(
                mDatabind.viewPager,
                arrayListOf(getString(R.string.win_loss),getString(R.string.handicap),getString(R.string.goal_num)),
                selectSize = 13f,
                unSelectSize = 13f,
                selectColor = com.xcjh.base_lib.R.color.white,
                normalColor = R.color.c_94999f,
                typefaceBold = true,
                scrollEnable = false,
                lineIndicatorColor = R.color.c_323235,
            )
            mDatabind.viewPager.offscreenPageLimit = 3
        } else {
            //mDatabind.layTabIndexFootball.visibility = View.GONE
        }
        loadData()
    }
    override fun lazyLoadData() {
        //ViewModelProvider.get()
       // loadData()
    }

    private fun loadData() {
        //ViewModelProvider.get()
        vm.getOddsInfo(matchId)
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