package com.walisport.module.live.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLeagueBinding
import com.walisport.module.live.databinding.ItemWeekDayBinding
import com.walisport.module.live.ui.adapter.LeagueAdapter
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import com.walisport.module.live.ui.widget.LeagueItemDecoration
import kotlinx.coroutines.flow.filter
import kotlin.reflect.KClass

class LiveLeagueFragment : BaseFragment<LeagueViewModel, FragmentLeagueBinding>() {

    override val vbClass: KClass<FragmentLeagueBinding> = FragmentLeagueBinding::class
    override val vmClass: KClass<LeagueViewModel> = LeagueViewModel::class
    private val standsAdapter by lazy { LeagueAdapter() }
    private var leagueItemDecoration: LeagueItemDecoration? = null
    private var leagueID: Int = 0
    private var leagueName: String = ""
    private var leagueLogo: String = ""
    private var leagueColor = "#377c46"

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoPaddingNavigationBarColor = arch.cayenne.lib.common.R.color.black,autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun onDestroyView() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoPaddingNavigationBarColor = null,autoIsNavigation = false)
        super.onDestroyView()
    }

    override fun initView(savedInstanceState: Bundle?) {
        //获取联赛日程列表
        val matchID = arguments?.getLong("matchID") ?: 0L
        leagueID = arguments?.getInt("leagueID") ?: 0
        leagueName = arguments?.getString("leagueName") ?: ""
        leagueLogo = arguments?.getString("leagueLogo") ?: ""
        mBinding.apply {
            refreshLayout.setLeagueMode()
            //禁用下拉刷新，支持上拉加载更多
            refreshLayout.setEnableRefresh(false)
            refreshLayout.setOnLoadMoreListener {
                mViewModel.getMoreMatchLeagueList(leagueID)
            }
            recyclerLeague.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = standsAdapter

            }
            tvLeagueName.text = leagueName
        }
        Glide.with(this).load(leagueLogo).into(mBinding.ivLeagueLogo)
        standsAdapter.setMatchID(matchID)
        standsAdapter.setOnItemClickListener { pos ->
            val matchId = standsAdapter.currentList[pos].matchId
            val sportId = standsAdapter.currentList[pos].sportId
            val result = Bundle().apply {
                putLong("matchId", matchId)
                putInt("sportId", sportId)
            }
            sendResult(LiveMainFragment.CHANGE_MATCH, result)
            navigateUp()
        }
        initStickyHeader()
        mBinding.recyclerLeague.touchBackPressed()
        mBinding.root.touchBackPressed()
    }

    private fun initStickyHeader() {
        leagueItemDecoration = LeagueItemDecoration(
            isHeader = { position ->
                standsAdapter.currentList.getOrNull(position)!!.isWeekHead
            },
            createHeaderView = { context, parent ->
                ItemWeekDayBinding.inflate(LayoutInflater.from(context), parent, false).root
            },
            bindHeaderView = { v, position ->
                val item = standsAdapter.currentList.getOrNull(position)
                val binding = ItemWeekDayBinding.bind(v)
                item?.let {
                    val itemColor = item.itemColor
                    if (itemColor.isEmpty()) {
                        val color = Color.parseColor(leagueColor)
                        binding.layoutWeekDay.setBackgroundColor(color)
                    } else {
                        val color = Color.parseColor(itemColor)
                        binding.layoutWeekDay.setBackgroundColor(color)
                    }
                    binding.tvLeagueWeek.text = item.weekDay
                }
            }
        )
        leagueItemDecoration?.let {
            mBinding.recyclerLeague.addItemDecoration(it)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMatchLeagueList(leagueID)
    }

    override fun initListener() {
        mBinding.ivLeagueClose.apply { addScaleOnTouchAnimation() }.clickNoRepeat {
            findNavController().navigateUp()
        }
    }

    override suspend fun createObserver() {
        //网络断开重连后重新获取接口
        launch(Lifecycle.State.RESUMED) {
            mViewModel.observeLoginChange()
                .filter { it && mViewModel.apiStateListener.value == DataState.NetworkUnavailable }
                .collect {
                    if (it) {
                        setGradientBackground(leagueColor)
                        mViewModel.getMatchLeagueList(leagueID)
                    }
                }
        }
        mViewModel.activeHeaderIndex.observe(viewLifecycleOwner) { _ ->
            mBinding.recyclerLeague.invalidateItemDecorations()
        }
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            if (it == DataState.NoMoreData) {
                mBinding.refreshLayout.setNoMoreData(true)
                mBinding.refreshLayout.setEnableLoadMore(false)
                mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
            } else if (it == DataState.NetworkUnavailable) {
                mBinding.refreshLayout.setNoMoreData(true)
                mBinding.refreshLayout.setEnableLoadMore(false)
                mBinding.recyclerLeague.visibility = View.GONE //网络异常时需隐藏列表
                mBinding.leagueRoot.background = arch.cayenne.lib.common.R.color.black.getDrawable()
                mBinding.leagueBody.background = null
                mBinding.leagueMain.setState(
                    States.NETWORK_ANOMALY(),
                    arch.cayenne.lib.common.R.string.error_net.getString()
                )
            }
        }
        mViewModel.leagueData.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishLoadMore()
            it?.let {
                mBinding.leagueMain.setVisibilityGone()
                mBinding.recyclerLeague.visibility = View.VISIBLE
                if (it.match.isEmpty()) {
                    if (standsAdapter.itemCount == 0) {
                        mBinding.leagueMain.setState(
                            States.DATA_EMPTY,
                            R.string.lineup_empty.getString()
                        )
                    }
                } else {
                    setGradientBackground(it.color)
                }
                standsAdapter.submitList(it.match)
            } ?: run {
                if (standsAdapter.itemCount == 0) {
                    mBinding.leagueMain.setState(
                        States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                }
            }
        }
    }

    private fun setGradientBackground(color: String) {
        if (color.isEmpty()) {
            return
        }
        //颜色没有变化不需要设置
        if (leagueColor!=color){
            leagueColor = color
            val startColor = Color.parseColor(color)
            val endColor = Color.parseColor("#000000")
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)
            )
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            mBinding.leagueRoot.setBackgroundColor(startColor)
            mBinding.leagueBody.background = gradientDrawable
        }
    }
}