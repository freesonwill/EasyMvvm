package com.walisport.module.live.ui

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLeagueBinding
import com.walisport.module.live.ui.adapter.LeagueAdapter
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import kotlin.reflect.KClass

class LiveLeagueFragment : BaseFragment<LeagueViewModel, FragmentLeagueBinding>() {

    override val vbClass: KClass<FragmentLeagueBinding> = FragmentLeagueBinding::class
    override val vmClass: KClass<LeagueViewModel> = LeagueViewModel::class
    private val standsAdapter = LeagueAdapter()
    private var leagueID: Int = 0
    private var leagueName: String = ""
    private var leagueLogo: String = ""

    class LeagueItemDecoration(
        private val spacing: Int = 12.dp2px,
        private val leftRight: Int = 8.dp2px,
        private val bottomSpacing: Int = 20.dp2px,
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount ?: 0
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
            outRect.left = leftRight
            outRect.right = leftRight
        }
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun initView(savedInstanceState: Bundle?) {
        //获取联赛日程列表
        val matchID = arguments?.getLong("matchID") ?: 0L
        leagueID = arguments?.getInt("leagueID") ?: 0
        leagueName = arguments?.getString("leagueName") ?: ""
        leagueLogo = arguments?.getString("leagueLogo") ?: ""
        mBinding.apply {
            refreshLayout.setLeagueMode()
            refreshLayout.setEnableRefresh(false)
            refreshLayout.setOnLoadMoreListener {
                mViewModel.getMoreMatchLeagueData(leagueID)
            }
            recyclerLeague.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = standsAdapter
                addItemDecoration(LeagueItemDecoration())
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
        mBinding.recyclerLeague.touchBackPressed(requireContext())
    }

    override fun initData() {
        super.initData()
        mViewModel.getMatchLeagueData(leagueID)
    }

    override fun initListener() {
        mBinding.ivLeagueClose.apply { addScaleOnTouchAnimation() }.clickNoRepeat {
            findNavController().navigateUp()
        }
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            if (it == DataState.NoMoreData) {
                mBinding.refreshLayout.setEnableLoadMore(false)
                mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
        mViewModel.leagueData.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            it?.let {
                mBinding.leagueMain.setVisibilityGone()
                if (it.match.isEmpty() && standsAdapter.currentList.isEmpty()) {
                    mBinding.leagueMain.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                } else {
                    //更新设置背景色
                    var startColor = Color.parseColor("#377c46")
                    if (it.color.isNotEmpty()) {
                        startColor = Color.parseColor(it.color)
                    }
                    val endColor = Color.parseColor("#000000")
                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)
                    )
                    gradientDrawable.shape = GradientDrawable.RECTANGLE
                    mBinding.leagueRoot.background = gradientDrawable
                    //更新联赛数据
                    standsAdapter.submitList(it.match)
                    mViewModel.setItemCount(standsAdapter.itemCount)
                }
            } ?: run {
                if (standsAdapter.currentList.isEmpty()) {
                    mBinding.leagueMain.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                }
            }
        }
    }
}