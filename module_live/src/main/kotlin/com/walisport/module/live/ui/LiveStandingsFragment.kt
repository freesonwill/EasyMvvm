package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveStandingsBinding
import com.walisport.module.live.ui.adapter.StandingsAdapter
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import kotlin.reflect.KClass

/**
 * 积分榜
 */

class LiveStandingsFragment : BaseFragment<LiveStandingsViewModel, FragmentLiveStandingsBinding>() {

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    override val vbClass: KClass<FragmentLiveStandingsBinding> = FragmentLiveStandingsBinding::class
    override val vmClass: KClass<LiveStandingsViewModel> = LiveStandingsViewModel::class
    private var standsAdapter = StandingsAdapter()

    class StandingsItemDecoration(
        private val spacing: Int = 12.dp2px,         // 常规间距大小（像素）
        private val leftRight: Int = 8.dp2px,        // 左右边距（像素）
        private val bottomSpacing: Int = 20.dp2px,   // 最后一个 item 与底部的距离（像素）
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val itemCount = parent.adapter?.itemCount ?: 0      // 总 item 数
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
            outRect.left = leftRight
            outRect.right = leftRight
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerStandings.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = standsAdapter
            addItemDecoration(StandingsItemDecoration())
        }
        mBinding.mainLayout.setState(DynamicStateLayout.States.LOADING, "")
    }

    override fun initListener() {
    }

    override fun createObserver() {
        launch(Lifecycle.State.RESUMED) {
            mViewModel.competitionTables.observe(viewLifecycleOwner) {
                if (it.isEmpty() && standsAdapter.itemCount == 0) {
                    mBinding.mainLayout.setState(
                        DynamicStateLayout.States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                } else {
                    mBinding.mainLayout.setVisibilityGone()
                    standsAdapter.submitList(it)
                }
            }
            mainViewModel.mainMatch.observe(viewLifecycleOwner) {
                it?.let {
                    val leagueID = it.basicInfo.tournamentId
                    mViewModel.getCompetitionData(leagueID)
                }
            }
        }
    }
}