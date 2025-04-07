package com.walisport.module.live.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.lib.base.adapter.PagerAdapter
import com.walisport.lib.base.data.PagerBean
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.common.utils.ext.removeAllTips
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

/**
 * 注单
 * */
class LiveBetSlipFragment : BaseFragment<LiveBetSlipViewModel, FragmentLiveBetSlipLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetSlipLayoutBinding> =
        FragmentLiveBetSlipLayoutBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class

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

            viewpager.adapter = null
            viewpager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)

            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                tab.text = list[position].title
            }.attach()
            tabLayout.removeAllTips()
            reflexPadding(tabLayout)
        }
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                //拿到tabLayout的mTabStrip属性
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                val marginStart: Int = 8f.dp2px
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.leftMargin = marginStart
                    params.height = 32.dp2px
                    when (i) {
                        0, 1, 2 -> {
                            params.width = 74.dp2px
                            params.rightMargin = marginStart
                        }
                        else -> {
                           params.width = 60.dp2px
                        }
                    }
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}