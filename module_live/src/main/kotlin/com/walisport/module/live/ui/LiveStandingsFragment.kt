package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.databinding.FragmentLiveStandingsBinding
import com.walisport.module.live.ui.adapter.StandingsAdapter
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import kotlin.reflect.KClass

/**
 * 积分榜
 */

class LiveStandingsFragment : BaseFragment<LiveStandingsViewModel, FragmentLiveStandingsBinding>() {

    override val vbClass: KClass<FragmentLiveStandingsBinding> = FragmentLiveStandingsBinding::class
    override val vmClass: KClass<LiveStandingsViewModel> = LiveStandingsViewModel::class
    private var standsAdapter = StandingsAdapter()

    class StandingsItemDecoration(
        private val spacing: Int = 12.dp2px,         // 常规间距大小（像素）
        private val leftRight: Int = 8.dp2px,         // 左右边距（像素）
        private val bottomSpacing: Int = 20.dp2px,   // 最后一个 item 与底部的距离（像素）
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val itemCount = parent.adapter?.itemCount ?: 0 // 总 item 数
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
        mViewModel.addCompetitionLiveData()
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.competitionBean.observe(this) {
            if (it != null) {
                standsAdapter.submitList(it.tables)
            }
        }
    }
}