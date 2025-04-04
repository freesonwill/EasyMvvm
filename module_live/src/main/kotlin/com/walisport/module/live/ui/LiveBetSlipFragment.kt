package com.walisport.module.live.ui

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.lib.base.adapter.PagerAdapter
import com.walisport.lib.base.ben.PagerBean
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.removeAllTips
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 注单
 * */
class LiveBetSlipFragment : BaseFragment<LiveBetSlipViewModel, FragmentLiveBetSlipLayoutBinding>() {
    override val mBinding: FragmentLiveBetSlipLayoutBinding by viewBind()
    override val mViewModel: LiveBetSlipViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        initMenu()
    }

    private fun initMenu() {
        with(mBinding) {
            val array = resources.getStringArray(R.array.bet_slip_menus)
            val list = listOf(
                PagerBean(array[0]) { LiveBetSlipUnsettledFragment() },
                PagerBean(array[1]) { LiveBetSlipConfirmFragment() },
                PagerBean(array[2]) { LiveBetSlipSettledFragment() },
                PagerBean(array[3]) { LiveBetSlipReserveFragment() },
                PagerBean(array[4]) { LiveBetSlipInvalidFragment() },
            )
           val tab = tabLayout.newTab().setCustomView(R.layout.item_live_bet_slip_menu_layout)
            tabLayout.addTab(tab)
            viewpager.adapter = null
            viewpager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                val tabView = tab.view
                tab.text = list[position].title
                tabView.setOnClickListener {
                }
            }.attach()
            tabLayout.removeAllTips()
        }
    }


    override fun initListener() {
    }

    override fun createObserver() {
    }
}