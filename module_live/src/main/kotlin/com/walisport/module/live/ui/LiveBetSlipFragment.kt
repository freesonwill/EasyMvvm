package com.walisport.module.live.ui

import android.os.Bundle
import android.widget.LinearLayout
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.data.model.PagerBean
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.removeAllTips
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import kotlin.reflect.KClass

/**
 * 注单
 * */
class LiveBetSlipFragment : BaseFragment<LiveBetSlipViewModel, FragmentLiveBetSlipLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetSlipLayoutBinding> =
        FragmentLiveBetSlipLayoutBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()


    override fun initView(savedInstanceState: Bundle?) {
        initMenu()
    }

    private fun initMenu() {
        with(mBinding) {
            val array = resources.getStringArray(R.array.bet_slip_menus)
            val list = listOf(
                PagerBean(array[0]) { LiveBetSlipUnsettledFragment() },
                PagerBean(array[1]) { LiveBetSlipConfirmFragment() },
                PagerBean(array[2]) { LiveBetSlipSettledFragment() },
                PagerBean(array[3]) { LiveBetSlipReserveFragment() },
                PagerBean(array[4]) { LiveBetSlipInvalidFragment() },
            )
            viewPager.adapter = null
            viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = list[position].title
            }.attach()
            tabLayout.removeAllTips()

            reflexPadding(tabLayout)
        }
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                //拿到tabLayout的mTabStrip属性
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                val marginStart: Int = 8f.dp2px
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.leftMargin = marginStart
                    params.height = 32.dp2px
                    when (i) {
                        0, 1, 2 -> {
                            params.width = 74.dp2px
                            params.rightMargin = marginStart
                        }

                        else -> {
                            params.width = 60.dp2px
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

//    private fun initRecycler() {
//
//        val list: MutableList<List<Common.Order>> = mutableListOf()
//        val array = resources.getStringArray(R.array.bet_slip_menus)
//        array.forEach {
//            val order = Common.Order.newBuilder().setBetId("0").build()
//            val order1 = Common.Order.newBuilder().setBetId("1").build()
//            val tmpList = arrayListOf(order, order1)
//            list.add(tmpList)
//        }
//        val adapter = LiveBetSlipTabAdapter(LiveBetSlipTabCompare())
//        adapter.submitList(list)
//        mBinding.horizontalRecycler.also {
//            it.setHasFixedSize(true)
//            it.layoutManager =
//                HorizontalLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            it.adapter = adapter
//        }
//        val snapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(mBinding.horizontalRecycler)
//
//        mBinding.horizontalRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                when (newState) {
//                    RecyclerView.SCROLL_STATE_IDLE -> {
//                        recyclerView.layoutManager?.let {
//                            val manager = it as LinearLayoutManager
//                            val firstVisibleItem = manager.findFirstVisibleItemPosition()
//                            val count = manager.childCount
//                            val lastVisbleItem = manager.findLastVisibleItemPosition()
//                            mBinding.tabLayout.getTabAt(firstVisibleItem)?.select()
////                            LogUtils.dTag("TAG","first $firstVisibleItem  last $lastVisbleItem count $count")
//                        }
//                    }
//
//                    RecyclerView.SCROLL_STATE_DRAGGING -> {}
//                    RecyclerView.SCROLL_STATE_SETTLING -> {}
//                }
//            }
//        })
//    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

//    inner class HorizontalLayoutManager(
//        context: Context?, @RecyclerView.Orientation orientation: Int,
//        reverseLayout: Boolean
//    ) : LinearLayoutManager(context, orientation, reverseLayout) {
//        override fun canScrollVertically(): Boolean {
//            return false
//        }
//
//        override fun canScrollHorizontally(): Boolean {
//            return true
//        }
//    }

    companion object{
         val TAG = LiveBetSlipFragment::class.java.simpleName
    }

}