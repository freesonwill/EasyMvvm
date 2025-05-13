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
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.walisport.module.live.databinding.FragmentLeagueBinding
import com.walisport.module.live.ui.adapter.LeagueAdapter
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import kotlin.reflect.KClass

class LiveLeagueFragment : BaseFragment<LeagueViewModel, FragmentLeagueBinding>() {

    override val vbClass: KClass<FragmentLeagueBinding> = FragmentLeagueBinding::class
    override val vmClass: KClass<LeagueViewModel> = LeagueViewModel::class
    private val standsAdapter = LeagueAdapter()
    private var statusBarColor: Int = 0
    private var leagueID: Int = 0

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

    override fun initView(savedInstanceState: Bundle?) {
        //浸入式背景
        statusBarColor = StatusBarConfig.statusBarColor
        StatusBarConfig.statusBarColor = arch.cayenne.lib.common.R.color.tran_0
        setStatusBar(StatusBarConfig)
        //获取联赛日程列表
        val matchID = arguments?.getLong("matchID") ?: 0L
        leagueID = arguments?.getInt("leagueID") ?: 0
        mBinding.apply {
            refreshLayout.setOnRefreshListener {
                mViewModel.getMatchLeagueData(leagueID)
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel.getMoreMatchLeagueData(leagueID)
            }
            recyclerLeague.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = standsAdapter
                addItemDecoration(LeagueItemDecoration())
            }
        }
        standsAdapter.setMatchID(matchID)
        standsAdapter.setOnItemClickListener { pos ->
            val matchId = standsAdapter.currentList[pos].matchId
            val sportId = standsAdapter.currentList[pos].sportId
            navigate(
                LiveLeagueFragmentDirections.actionLeagueFragmentToLiveMainFragment(
                    matchId,
                    sportId
                )
            )
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMatchLeagueData(leagueID)
    }

    override fun initListener() {
        mBinding.ivLeagueClose.clickNoRepeat {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        StatusBarConfig.statusBarColor = statusBarColor
        setStatusBar(StatusBarConfig)
        super.onDestroyView()
    }

    override fun createObserver() {
        mViewModel.leagueData.observe(this) {
            mBinding.refreshLayout.finishRefresh()
            if (it != null) {
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
                //更新设置联赛LOGO
                Glide.with(this).load(it.logo).into(mBinding.ivLeagueLogo)
                //更新设置联赛名称
                mBinding.tvLeagueName.text = it.tournamentName
                //更新联赛数据
                standsAdapter.submitList(it.match)
            }
        }
    }
}