package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.interface_.StatusBarConfig
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.databinding.FragmentLeagueBinding
import com.walisport.module.live.ui.adapter.LeagueAdapter
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import kotlin.reflect.KClass

class LiveLeagueFragment : BaseFragment<LeagueViewModel, FragmentLeagueBinding>() {
    override val vbClass: KClass<FragmentLeagueBinding> = FragmentLeagueBinding::class
    override val vmClass: KClass<LeagueViewModel> = LeagueViewModel::class

    class LeagueItemDecoration(
        private val spacing: Int = 12.dp2px,         // 常规间距大小（像素）
        private val leftRight: Int = 8.dp2px,         // 左右间距
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
        mBinding.recyclerLeague.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = LeagueAdapter().apply {
                addItemDecoration(LeagueItemDecoration())
                val week1 = LeagueMatchBean(0, true, "12月8日 星期四", 0, "", "", "", "0")
                val week2 = LeagueMatchBean(0, true, "12月10日 星期六", 0, "", "", "", "0")
                val temp = LeagueMatchBean(0, false, "", 10001010, "", "", "阿森纳", "曼城")
                val list = listOf(temp, week1, temp, temp, temp, week2, temp, temp)
                submitList(list)
            }
        }
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig(hideStatusBar = true))
    }

    override fun initListener() {
        mBinding.ivLeagueClose.clickNoRepeat{
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.root.fitsSystemWindows = true
        setStatusBar(StatusBarConfig(hideStatusBar = false))
    }


    override fun createObserver() {

    }
}