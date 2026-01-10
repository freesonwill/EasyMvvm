package com.walisport.module.business.common.ui.fragment

import android.view.View
import androidx.annotation.CallSuper
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.addScrollThresholdListener
import arch.cayenne.lib.common.utils.helper.NestedScrollViewBackToTopHelper
import com.google.android.material.appbar.AppBarLayout
import com.walisport.module.business.common.ui.viewmodel.BaseBannerViewModel
import kotlin.math.abs

/**
 * @date: 2026/1/9 20:54
 * @description: 与Banner联动的Fragment基类
 */
abstract class BaseBannerLinkFragment<VM : BaseViewModel, VB : ViewBinding>:BaseFragment<VM, VB>() {
    companion object {
        val THRESHOLD_TOP = 20.dp2px
        val THRESHOLD_DOWN = 20.dp2px
    }
    protected val bannerViewModel:BaseBannerViewModel get() = provideBannerViewModel()

    @CallSuper
    override fun initListener() {
        val nestedScrollView = this.nestedScrollView
        val appLayout = this.appLayout
        if(appLayout != null){
            appLayout.addOnOffsetChangedListener(object:AppBarLayout.OnOffsetChangedListener {
                private var lastAppBarOffset: Int? = null
                override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                    val last = lastAppBarOffset
                    if (last != null) {
                        when {
                            (verticalOffset < last && abs(verticalOffset) > THRESHOLD_TOP) -> {
                                // 往上滑：更折叠（offset 更负）
                                bannerViewModel.setScroll(true)
                            }
                            verticalOffset > last && abs(verticalOffset) < THRESHOLD_DOWN -> {
                                // 往下滑：更展开（offset 变大，趋近 0）
                                bannerViewModel.setScroll(false)
                            }
                            else -> Unit
                        }
                    }
                    lastAppBarOffset = verticalOffset
                }
            })

        } else if(nestedScrollView == null) {
            rvContent?.addScrollThresholdListener(
                thresholdTop = THRESHOLD_TOP,
                thresholdDown = THRESHOLD_DOWN,
                onDowScrolling= { bannerViewModel.setScroll(true) },
                onTopScrolling ={ bannerViewModel.setScroll(false) }
            )

            rvContent?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 这里处理滚动状态变化
                    // newState: 0=IDLE, 1=DRAGGING, 2=SETTLING
                    bannerViewModel.setScrollState(newState)
                }
            })
        } else {
            NestedScrollViewBackToTopHelper(nestedScrollView, ivBackToTop,
                scrollStateListener = { bannerViewModel.setScrollState(it)},
                thresholdTop = THRESHOLD_TOP,
                thresholdDown = THRESHOLD_DOWN,
                onDownScrolling = { bannerViewModel.setScroll(true) },
                onTopScrolling = { bannerViewModel.setScroll(false) }
            )
        }
    }

    abstract fun provideBannerViewModel():BaseBannerViewModel
    open fun provideBannerAppBarLayout():AppBarLayout? = null
    open fun provideBannerRecyclerView():RecyclerView? = null
    open fun provideBannerNestedScrollView():Pair<NestedScrollView, View>? = null

    private val rvContent get() = provideBannerRecyclerView()
    private val nestedScrollView get() = provideBannerNestedScrollView()?.first
    private val ivBackToTop get() = provideBannerNestedScrollView()?.second!!
    private val appLayout get() = provideBannerAppBarLayout()
}