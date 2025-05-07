package com.walisport.module.live.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.MarketMenuBean
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetOnBinding
import com.walisport.module.live.ui.adapter.LiveBetOnAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//投注
class LiveBetOnFragment : BaseFragment<LiveBetOnViewModel, FragmentLiveBetOnBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnBinding> = FragmentLiveBetOnBinding::class
    override val vmClass: KClass<LiveBetOnViewModel> = LiveBetOnViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    private var tabList: MutableList<String> = mutableListOf()
    var itemDecoration: RecyclerView.ItemDecoration = LinearSpacingItemDecoration(8.dp2px, 0.dp2px)
    var tabPosition = 0
    var liveBetOnAdapter :LiveBetOnAdapter = LiveBetOnAdapter()
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvBetList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(
                this@LiveBetOnFragment.context, LinearLayoutManager.VERTICAL, false
            )
            adapter = liveBetOnAdapter.apply {
                post {
                    addItemDecoration(itemDecoration)
                }
            }
        }
    }

    override fun initData() {
        super.initData()
    }
    fun showData(list: List<MarketMenuBean>?) {
        var baseInfo = mainViewModel.mainMatch.value?.basicInfo
        liveBetOnAdapter.submitList(list)
        liveBetOnAdapter.setHomeAway(
                baseInfo?.homeTeam.toString(),
        baseInfo?.homeTeamIcon.toString(),
        baseInfo?.awayTeam.toString(),
        baseInfo?.awayTeamIcon.toString()
        )
    }

    override fun initListener() {
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mViewModel.getMarketList(
                    (if (tab.position == 0) "" else mViewModel.marketType.value?.get(
                        tab.position - 1
                    )?.code).toString()
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        mBinding.ivMenu.clickNoRepeat {
            navigate(LiveMainFragmentDirections.actionLiveMainFragmentToLiveBetOnMenuFragment())
        }
    }

    override fun createObserver() {
        mainViewModel.mainMatch.observe(viewLifecycleOwner) {
            mViewModel.observeMarketTypeBean()
            // bool bet_stop = 18;         // false: 未停止投注, true: 已停止投注
            if (it.basicInfo != null) {
                if (it.basicInfo.betStop) {
                    mBinding.clDynamics.setState(States.CLOSE, R.string.bet_stop.getString())
                    return@observe
                } else {
                    mBinding.clDynamics.setVisibilityGone()
                }
            }
            mViewModel.getMarketType(mainViewModel.matchId)
        }
        mViewModel.marketType.observe(viewLifecycleOwner) { list ->
            LogUtils.e("marketTypeData${list}")
            if (list!!.isEmpty()) {
                mBinding.clDynamics.setState(States.DATA_EMPTY, R.string.lineup_empty.getString())
                return@observe
            } else {
                mBinding.clDynamics.setVisibilityGone()
            }
            tabList.apply {
                clear()
                add(R.string.live_bet_tab_all.getString())
            }
            list.forEach {
                tabList.add(it.name)
            }
            lifecycleScope.launch {
                addNewTab()
            }
        }
        //根据盘口分类code获取盘口列表
        mViewModel.getMarketList.observe(viewLifecycleOwner) {
            showData(it)
        }

        mViewModel.observeMarketType.observe(viewLifecycleOwner) {
            mViewModel.getMarketTypeAll()
        }
    }

    //获取code在tab的位置

    // 动态添加Tab的方法
    private fun addNewTab() {
        mBinding.tabLayout.removeAllTabs()
        tabList.forEach { text ->
            // 添加新Tab
            val newTab = mBinding.tabLayout.newTab()
            newTab.text = text
            mBinding.tabLayout.addTab(newTab)
        }
        reflexPadding(mBinding.tabLayout)
        mBinding.tabLayout.getTabAt(tabPosition)?.select();
    }

    //设置tab之间的外边距
    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                val margin: Int = 4f.dp2px
                val marginStart: Int = 8f.dp2px
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    when (i) {
                        0 -> {
                            params.leftMargin = marginStart
                            params.rightMargin = margin
                        }

                        mTabStrip.childCount - 1 -> {
                            params.leftMargin = margin
                            params.rightMargin = marginStart
                        }

                        else -> {
                            params.leftMargin = margin
                            params.rightMargin = margin
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

}