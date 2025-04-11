package com.walisport.module.live.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.databinding.FragmentLiveBetOnBinding
import com.walisport.module.live.ui.adapter.LiveBetOnAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import kotlin.reflect.KClass

//投注
class LiveBetOnFragment : BaseFragment<LiveBetOnViewModel, FragmentLiveBetOnBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnBinding> = FragmentLiveBetOnBinding::class
    override val vmClass: KClass<LiveBetOnViewModel> = LiveBetOnViewModel::class

    //测试数据
    //赛选条件
    private var tabList: List<String> =
        listOf("全部", "让球大小", "波胆", "角球&罚牌", "罚球", "角球&罚牌")

    //赛选内容
    private var list: List<String> = listOf("让球大小", "波胆", "角球&罚牌", "罚球", "角球&罚牌")

    override fun initView(savedInstanceState: Bundle?) {
        addNewTab()
        mBinding.rvBetList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(
                this@LiveBetOnFragment.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = LiveBetOnAdapter(object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
            }).apply {
                post {
                    addItemDecoration(LinearSpacingItemDecoration(8.dp2px, 10.dp2px))
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
        tabList.forEach { text ->
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