package com.walisport.module.live.ui

import android.R
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.data.StatusBarConfig
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.data.model.MatchBean
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
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
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
        val match1 = MatchBean(1,true, "12月8日 星期四", 0, "", "", "", "")
        val match2 = MatchBean(2,true, "12月10日 星期六", 0, "", "", "", "")
        val match3 = MatchBean(3,false, "", 1000000, "", "", "阿森纳", "阿森纳")
        mBinding.recyclerLeague.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = LeagueAdapter().apply {
                addItemDecoration(LeagueItemDecoration())
                val list = listOf(match1, match3, match2, match3, match2, match3)
                submitList(list)
            }
        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.hideStatusBar = true
        setStatusBar(StatusBarConfig)
        //测试背景切换，接入数据后需屏蔽
        val week1 = LeagueMatchBean(0, match1, "#008040", "#0E0F1A")
        mViewModel.setData(week1)
    }

    override fun initListener() {
        mBinding.ivLeagueClose.clickNoRepeat {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        mBinding.root.fitsSystemWindows = true
        StatusBarConfig.hideStatusBar = false
        setStatusBar(StatusBarConfig)
        super.onDestroyView()
    }

    override fun createObserver() {
        //当数据发生变化时页面需要更新数据和背景
        mViewModel.leagueData.observe(this) {
            if (it != null && "" != it.startColor && "" != it.endColor) {
                //创建线性背景
                val startColor = Color.parseColor(it.startColor)
                val endColor = Color.parseColor(it.endColor)
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)
                )
                gradientDrawable.shape = GradientDrawable.RECTANGLE
                mBinding.root.background = gradientDrawable
            }
        }
    }
}