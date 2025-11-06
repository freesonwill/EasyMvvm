package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.live.data.model.LiveShareBean
import com.walisport.module.live.databinding.FragmentLiveShareBinding
import com.walisport.module.live.ui.adapter.LiveShareAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoPlayerViewModel
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的分享页
 */
class LiveVideoShareFragment : BaseFragment<LiveVideoPlayerViewModel, FragmentLiveShareBinding>() {
    override val vbClass: KClass<FragmentLiveShareBinding> = FragmentLiveShareBinding::class
    override val vmClass: KClass<LiveVideoPlayerViewModel> = LiveVideoPlayerViewModel::class

    private val liveShareAdapter by lazy {
        LiveShareAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val metrics = resources.displayMetrics
        //density和scaledDensity被篡改，尝试恢复
        if (metrics.density != DensityInfo.density && DensityInfo.density > 0) {
            metrics.density = DensityInfo.density
        }
        if (metrics.scaledDensity != DensityInfo.scaledDensity && DensityInfo.scaledDensity > 0) {
            metrics.scaledDensity = DensityInfo.scaledDensity
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initRv()

    }

    private fun initRv() {
        mBinding.rvShareApps.apply {
            //某些机型上， RecyclerView存在过滚动效果。 禁用scroll, 禁用过滚动效果
            layoutManager = object : GridLayoutManager(requireContext(), 3) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }// 每行3个
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val layoutManager = parent.layoutManager as? GridLayoutManager ?: return
                    val position = parent.getChildAdapterPosition(view)
                    if (position == RecyclerView.NO_POSITION) return

                    val spanCount = layoutManager.spanCount
                    val spanSizeLookup = layoutManager.spanSizeLookup

                    // 獲取當前 Item 所在的「行索引」(group index)
                    // 這是 GridLayoutManager 判斷「行」的正確方式，不受 span size 影響
                    val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)
                    val columnIndex = spanSizeLookup.getSpanIndex(position, spanCount)

                    if (spanGroupIndex == 0) {
                        outRect.top = 0
                    } else {
                        outRect.top = 20.dp2px //
                    }

                    if (columnIndex == 1) {
                        outRect.left = (-12).dp2px
                    } else if (columnIndex == 2) {
                        outRect.left = (-12).dp2px
                    } else {
                        outRect.left = (-6).dp2px
                    }

                }
            })
            adapter = liveShareAdapter
            itemAnimator = null
        }

        var id = 0
        liveShareAdapter.submitList(
            listOf(
                LiveShareBean(
                    id++, R.drawable.icon_share_chat,
                    "聊天室",
                ) {
                    showToast("聊天室")
                },
                LiveShareBean(
                    id++, R.drawable.icon_share_soccer,
                    "巴西-中国直播间"
                ) {
                    showToast("巴西-中国直播间")
                },
                LiveShareBean(
                    id++, R.drawable.icon_share_soccer,
                    "阿森纳-曼联直播间"
                ) {
                    showToast("阿森纳-曼联直播间")
                },

                LiveShareBean(
                    id++, R.drawable.icon_share_what,
                    "WhatsApp"
                ) {
                    showToast("WhatsApp")
                },
                LiveShareBean(
                    id++, com.walisport.module.live.R.drawable.icon_live_share_link,
                    "复制链接"
                ) {
                    showToast("复制链接")
                },
                LiveShareBean(
                    id++, com.walisport.module.live.R.drawable.icon_live_share_more,
                    "更多"
                ) {
                    showToast("更多")
                },


                )
        )


    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    override fun onResume() {
        super.onResume()
        mBinding.root.fitsSystemWindows = false
    }


    companion object {
        const val TAG = "LiveVideoShareFragment"
    }

}