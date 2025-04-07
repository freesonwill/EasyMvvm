package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.tabs.TabLayout
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.utils.LogUtils
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.databinding.FragmentLiveBetOnBinding
import com.walisport.module.live.ui.adapter.LiveBetOnAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import kotlin.reflect.KClass

//投注
class LiveBetOnFragment : BaseFragment<LiveBetOnViewModel,FragmentLiveBetOnBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnBinding> = FragmentLiveBetOnBinding::class
    override val vmClass: KClass<LiveBetOnViewModel> = LiveBetOnViewModel::class
    class LinearSpacingItemDecoration(
        private val spacing: Int,         // 常规间距大小（像素）
        private val bottomSpacing: Int,   // 最后一个 item 与底部的距离（像素）
        private val includeEdge: Boolean = false // 是否包含顶部和左右边距
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val itemCount = parent.adapter?.itemCount ?: 0 // 总 item 数
            if (includeEdge) {
                // 包含边缘的情况
                outRect.top = if (position == 0) spacing else spacing / 2
                outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
                outRect.left = spacing
                outRect.right = spacing
            } else {
                // 不包含边缘，只设置 item 之间的间距和底部间距
                if (position > 0) {
                    outRect.top = spacing // 第一个 item 上面没有间距
                }
                outRect.bottom = if (position == itemCount - 1) bottomSpacing else 0
                outRect.left = 0
                outRect.right = 0
            }
        }
    }
    //测试数据
    //赛选条件
    private var tabList : List<String> = listOf("全部","让球大小","波胆","角球&罚牌","罚球","角球&罚牌")
    //赛选内容
    private var list : List<String> = listOf("让球大小","波胆","角球&罚牌","罚球","角球&罚牌")

    override fun initView(savedInstanceState: Bundle?) {
        addNewTab()
        mBinding.rvBetList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(this@LiveBetOnFragment.context, LinearLayoutManager.VERTICAL, false)
             adapter = LiveBetOnAdapter(object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
            }).apply {
                post {
                    addItemDecoration(LinearSpacingItemDecoration(8.dp2px,50.dp2px))
                    submitList(list)
                }
            }
        }
    }

    override fun initListener() {
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }




    override fun createObserver() {
    }

    // 动态添加Tab的方法
    private fun addNewTab() {
        tabList.forEach{ text ->
            // 添加新Tab
            val newTab = mBinding.tabLayout.newTab()
            newTab.text = text
            mBinding.tabLayout.addTab(newTab)
        }
        reflexPadding(mBinding.tabLayout)
    }
    //设置tab之间的外边距
    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                val margin: Int = 4f.dp2px
                val marginStart: Int = 8f.dp2px
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    when (i) {
                        0 -> {
                            params.leftMargin = marginStart
                            params.rightMargin = margin
                        }
                        mTabStrip.childCount - 1 -> {
                            params.leftMargin = margin
                            params.rightMargin = marginStart
                        }
                        else -> {
                            params.leftMargin = margin
                            params.rightMargin = margin
                        }
                    }
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}