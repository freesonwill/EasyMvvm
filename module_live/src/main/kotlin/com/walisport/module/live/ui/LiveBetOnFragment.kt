package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetOnBinding
import com.walisport.module.live.ui.adapter.LivBetListCallback
import com.walisport.module.live.ui.adapter.LiveBetOnAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.utils.TabMarginExt.reflexMargin
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

//投注
class LiveBetOnFragment : BaseFragment<LiveBetOnViewModel, FragmentLiveBetOnBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnBinding> = FragmentLiveBetOnBinding::class
    override val vmClass: KClass<LiveBetOnViewModel> = LiveBetOnViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()
    private var tabList: MutableList<String> = mutableListOf()
    private var tabPosition: List<Int> = mutableListOf(0, 0)
    lateinit var liveBetOnAdapter: LiveBetOnAdapter
    private var isNotify = false
    private var selectionComboId: Long? = null
    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        mBinding.clDynamics.setState(States.LOADING, "")
    }

    override fun initData() {
        super.initData()
    }

    fun initAdapter() {
        mBinding.rvBetList.apply {
            layoutManager = LinearLayoutManager(
                this@LiveBetOnFragment.context, LinearLayoutManager.VERTICAL, false
            )
            liveBetOnAdapter = LiveBetOnAdapter(object : LivBetListCallback {
                override fun itemListCallback(
                    marketI: Long,
                    selectionId: Long,
                    x: Float,
                    y: Float
                ) {
                    launch {
                        val status = mainViewModel.matchId.value?.let {
                            mViewModel.setSelection(
                                it,
                                selectionId
                            )
                        }
                        if (status == AddSelectionStatus.SINGLE) {
                            BetSheetFragment.newInstance().show(parentFragmentManager)
                        } else if (status == AddSelectionStatus.DISABLE_COMBO) {
                            showToast(getString(R.string.disabled_to_combo))
                        } else if (status == AddSelectionStatus.COMBO || status == AddSelectionStatus.UPDATE) {
                            fabViewModel.setClickAnimation(x, y)
                        }
                    }
                }
            })
            adapter = liveBetOnAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showData(list: List<MarketMenuBean>?, marketIds: List<Long>) {
        var baseInfo = mainViewModel.mainMatch.value?.basicInfo
        //根据盘口获取投注注区
        mViewModel.getLiveSelectionBean(marketIds)
        launch {
            mViewModel.getLiveSelectionBean.collect {
                    mBinding.clDynamics.setVisibilityGone()
                    mBinding.rvBetList.setItemViewCacheSize(list?.size ?: 0)
                    liveBetOnAdapter.setHomeAway(
                        baseInfo?.homeTeam.toString(),
                        baseInfo?.homeTeamIcon.toString(),
                        baseInfo?.awayTeam.toString(),
                        baseInfo?.awayTeamIcon.toString(), it, isNotify
                    )
                    liveBetOnAdapter.submitList(list)
                    liveBetOnAdapter.setSelectionComboId(selectionComboId)
                    liveBetOnAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun initListener() {
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                initAdapter()
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

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mBinding.clDynamics.setState(States.LOADING, "")
            mBinding.LLCBetOn.visibility = View.GONE
            mBinding.ivMenu.visibility = View.GONE
            mViewModel.observerSelectionComboByMatchId(it)
        }

        //推送盘口关闭和开启发生变化
        mainViewModel.observeMainMatch.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.basicInfo.betStop) {
                    mBinding.LLCBetOn.visibility = View.GONE
                    mBinding.ivMenu.visibility = View.GONE
                    mBinding.clDynamics.setState(States.CLOSE, R.string.bet_stop.getString())
                }
            }
        }
        mainViewModel.mainMatch.observe(viewLifecycleOwner) {
            liveBetOnAdapter.submitList(emptyList())
            tabList.clear()
            tabPosition = mutableListOf(0, 0)
            selectionComboId = null
            mBinding.tabLayout.removeAllTabs()
            // bool bet_stop = 18;         // false: 未停止投注, true: 已停止投注
            if (it != null) {
                if (it.basicInfo.betStop) {
                    mBinding.ivMenu.visibility = View.GONE
                    mBinding.LLCBetOn.visibility = View.GONE
                    mBinding.clDynamics.setState(States.CLOSE, R.string.bet_stop.getString())
                } else {
                    mViewModel.getMarketType(it.matchId)
                }
            }
        }
        mViewModel.marketType.observe(viewLifecycleOwner) { list ->
            if (list!!.isEmpty()) {
                mBinding.ivMenu.visibility = View.GONE
                mBinding.LLCBetOn.visibility = View.GONE
                mBinding.clDynamics.setState(States.DATA_EMPTY, R.string.lineup_empty.getString())
                return@observe
            } else {
                if (tabList.isEmpty()) {
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
                } else {
                    mBinding.tabLayout.getTabAt(tabPosition[0])?.select()
                }
                mBinding.ivMenu.visibility = View.VISIBLE
                mBinding.LLCBetOn.visibility = View.VISIBLE
                mBinding.clDynamics.setVisibilityGone()
            }
        }
            //根据盘口分类code获取盘口列表
            mViewModel.getMarketList.observe(viewLifecycleOwner) {
                var marketIds: MutableList<Long> = mutableListOf()
                it?.forEach {
                    marketIds.add(it.marketId)
                }
               // LogUtils.e("showData${marketIds}")
                showData(it, marketIds)
            }

            //侧边栏筛选
            mViewModel.observeMarketMenu.observe(viewLifecycleOwner) {
                mBinding.tabLayout.getTabAt(it[0])?.select()
                tabPosition = it
                mBinding.rvBetList.post {
                    launch {
                        delay(200)
                        val layoutManager =  mBinding.rvBetList.layoutManager as LinearLayoutManager
                        layoutManager.scrollToPositionWithOffset( tabPosition[1],0)
                    }
                }
            }

            //盘口数据变动
            launch {
                mViewModel.observeSelection.collect {
                    isNotify = true
                    mViewModel.observeSelectionGetMarketList(
                        (if (mBinding.tabLayout.selectedTabPosition <= 0) "" else mViewModel.marketType.value?.get(
                            mBinding.tabLayout.selectedTabPosition - 1
                        )?.code).toString()
                    )
                }
            }

            //串关数据变动
            mViewModel.observerSelectionCombo.observe(viewLifecycleOwner){
                selectionComboId = it
                liveBetOnAdapter.setSelectionComboId(selectionComboId)
                liveBetOnAdapter.notifyDataSetChanged()
            }
    }

    // 动态添加Tab的方法
    private fun addNewTab() {
        mBinding.tabLayout.removeAllTabs()
        tabList.forEach { text ->
            // 添加新Tab
            val newTab = mBinding.tabLayout.newTab()
            newTab.text = text
            mBinding.tabLayout.addTab(newTab)
        }
        mBinding.tabLayout.getTabAt(0)?.select()
        mBinding.tabLayout.reflexMargin(8.dp2px, 8.dp2px, 4.dp2px)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
