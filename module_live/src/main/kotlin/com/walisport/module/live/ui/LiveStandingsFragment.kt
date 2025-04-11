package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.data.model.StandingsBean
import com.walisport.module.live.data.model.StandingsTeam
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

    private val itemDecoration: ItemDecoration = object : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(8.dp2px, 8.dp2px, 12.dp2px, 0)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerStandings.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StandingsAdapter(context).apply {
                addItemDecoration(itemDecoration)
                val tm1 = StandingsTeam(1, "厄瓜多尔", 1, 1)
                val tm2 = StandingsTeam(2, "荷兰", 3, 1)
                val tm3 = StandingsTeam(3, "巴西", 2, 1)
                val tm4 = StandingsTeam(4, "阿根廷", 4, 1)
                val team = listOf(tm1, tm2, tm3, tm4)
                val temp = StandingsBean(0, team)
                val list = listOf(temp, temp, temp, temp, temp, temp, temp)
                submitList(list)
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}