package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.reflexMargin
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.data.AddSelectionStatus
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.data.BetOnMenuStatus
import com.walisport.module.live.databinding.FragmentLiveBetOnBinding
import com.walisport.module.live.ui.adapter.LivBetListCallback
import com.walisport.module.live.ui.adapter.LiveBetOnAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.abs
import kotlin.reflect.KClass

//投注
class LiveBetOnFragment : BaseFragment<LiveBetOnViewModel, FragmentLiveBetOnBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnBinding> = FragmentLiveBetOnBinding::class
    override val vmClass: KClass<LiveBetOnViewModel> = LiveBetOnViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    private val fabViewModel: FloatingButtonControlViewModel by activityViewModel()
    private var tabList: MutableList<String> = mutableListOf()
    private var tabPosition: List<Int> = mutableListOf(0, 0)
    private lateinit var liveBetOnAdapter: LiveBetOnAdapter
    private var mCurrentItemPosition: Int = 0
    private var selectionComboId: Long? = null
    private var isTabClicked: Boolean = false
    private lateinit var viewPager2: ViewPager2
    private var startX = 0f
    private var startY = 0f
    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        mBinding.clDynamics.setState(States.LOADING, "")
    }


    private fun initAdapter() {
        mBinding.rvBetList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(
                this@LiveBetOnFragment.context, LinearLayoutManager.VERTICAL, false
            )
            liveBetOnAdapter = LiveBetOnAdapter(object : LivBetListCallback {
                override fun itemListCallback(
                    marketI: Long,
                    selectionId: Long,
                    x: Float,
                    y: Float,
                    position: Int,
                    beforePosition: Int
                ) {
                    mCurrentItemPosition = position
                    launch {
                        val status = mainViewModel.matchId.value?.let {
                            mViewModel.setSelection(it, selectionId)
                        }
                        if (status is AddSelectionStatus.Success.Single) {
                            BetSheetFragment.show(requireActivity())
                        } else if (status is AddSelectionStatus.Failure) {
                            status.msg?.let {
                                showToast(it)
                            }
                        } else if (status is AddSelectionStatus.Success.Combo || status is AddSelectionStatus.Success.Update) {
                            fabViewModel.setClickAnimation(x, y)
                        }
                    }
                }
            })
            adapter = liveBetOnAdapter
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        // 获取 ViewPager2
        viewPager2 = requireActivity().findViewById(R.id.vp_page)

        // 设置 ViewPager2 的触摸监听
        viewPager2.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val endX = event.x
                    val endY = event.y
                    val distanceX = abs(endX - startX)
                    val distanceY = abs(endY - startY)
                    // 如果垂直滑动距离大于水平滑动距离，禁用 ViewPager2 滑动
                    if (distanceY > distanceX) {
                        viewPager2.isUserInputEnabled = false
                    } else {
                        viewPager2.isUserInputEnabled = true
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 恢复 ViewPager2 的滑动
                    viewPager2.isUserInputEnabled = true
                }
            }
            false // 让事件继续传递给 RecyclerView
        }

        // 监听 RecyclerView 滑动状态
        mBinding.rvBetList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 当 RecyclerView 滑动时禁用 ViewPager2，停止时启用
                viewPager2.isUserInputEnabled = newState == RecyclerView.SCROLL_STATE_IDLE
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 滑动到底部时禁用 ViewPager2
                if (!recyclerView.canScrollVertically(1)) {
                    viewPager2.isUserInputEnabled = false
                }
            }
        })
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                isTabClicked = true
                mViewModel.getMarketList(
                    (if (tab.position == 0) "" else mViewModel.marketType.value?.get(
                        tab.position - 1
                    )?.code).toString()
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        mBinding.ivMenu.clickNoRepeatSingle{
            mainViewModel.setLiveBetOnMen(BetOnMenuStatus.OPEN)
        }
    }

    override suspend fun createObserver() {
        mainViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            when (state) {
                DataState.NetworkUnavailable->{
                    mBinding.LLCBetOn.visibility = View.GONE
                    mBinding.ivMenu.visibility = View.GONE
                    mBinding.clDynamics.setState(
                        States.NETWORK_ANOMALY,
                        arch.cayenne.lib.common.R.string.error_net.getString()
                    )
                }
            }
        }

        liveBetOnAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {

            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payloads: Any?) {
                if (isTabClicked) {
                    mBinding.rvBetList.post {
                        mBinding.rvBetList.scrollToPosition(0)
                    }
                    isTabClicked = false
                }
            }
        })
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mBinding.clDynamics.setState(States.LOADING, "")
            mBinding.LLCBetOn.visibility = View.GONE
            mBinding.ivMenu.visibility = View.GONE
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
            mainViewModel.matchId.value?.let { matchId -> mViewModel.observerSelectionComboByMatchId(matchId) }
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
                    addNewTab()
                } else {
                    mBinding.tabLayout.getTabAt(tabPosition[0])?.select()
                }
                mBinding.ivMenu.visibility = View.VISIBLE
                mBinding.LLCBetOn.visibility = View.VISIBLE
                mBinding.clDynamics.setVisibilityGone()
            }
        }

        //根据盘口分类code获取盘口列表
        mViewModel.liveMarketListBean.observe(viewLifecycleOwner) {
            launch {
                val baseInfo = mainViewModel.mainMatch.value?.basicInfo
                val selectionsEdit = mainViewModel.getSelectionsEditAll()
                // LogUtils.dTag("盘口推","---------------${selectionEdit}")
                mBinding.clDynamics.setVisibilityGone()
                liveBetOnAdapter.setData(
                    baseInfo?.homeTeam.toString(),
                    baseInfo?.homeTeamIcon.toString(),
                    baseInfo?.awayTeam.toString(),
                    baseInfo?.awayTeamIcon.toString(), true,
                    selectionsEdit
                )
                liveBetOnAdapter.submitList(it)
            }
        }

        //侧边栏筛选
        mViewModel.observeMarketMenu.observe(viewLifecycleOwner) {
            mainViewModel.setLiveBetOnMen(BetOnMenuStatus.CLOSE)
            mBinding.tabLayout.getTabAt(it[0])?.select()
            tabPosition = it
            mBinding.rvBetList.post {
                launch {
                    delay(200)
                    val layoutManager = mBinding.rvBetList.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(tabPosition[1], 0)
                }
            }
        }

        //盘口数据变动
        mViewModel.observeSelection.observe(viewLifecycleOwner) {
            mViewModel.observeSelectionGetMarketList(
                (if (mBinding.tabLayout.selectedTabPosition <= 0) "" else mViewModel.marketType.value?.get(
                    mBinding.tabLayout.selectedTabPosition - 1
                )?.code).toString()
            )
        }
        //串关数据变动
        mViewModel.observerSelectionCombo.observe(viewLifecycleOwner) {
            selectionComboId = it
            liveBetOnAdapter.setSelectionComboId(selectionComboId, false)
            if (liveBetOnAdapter.getBeforePosition() == -1) {
                it?.let {  comboIdByMarketPosition(it)?.let { position ->
                    liveBetOnAdapter.notifyItemChanged(position) }  }
            } else {
                if(liveBetOnAdapter.getBeforePosition()!=mCurrentItemPosition){
                    liveBetOnAdapter.notifyItemChanged(mCurrentItemPosition)
                }
                liveBetOnAdapter.notifyItemChanged(liveBetOnAdapter.getBeforePosition())
            }
        }
    }

    private fun comboIdByMarketPosition(id: Long): Int? {
        var position: Int? = null
        mViewModel.liveMarketListBean.value?.forEachIndexed{index,it->
            it.list.forEach{selection->
                if (id==selection.selectionId){
                    position = index
                }
            }
        }
        return position
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
        mBinding.tabLayout.removeAllTips()
    }
}
