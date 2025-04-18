package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.LocationFixedDialogFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.compare.VideoSourceCompare
import com.walisport.module.live.databinding.FragmentLiveSourcePortraitBinding
import com.walisport.module.live.ui.adapter.LiveVideoSourceHorizontalAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlin.reflect.KClass

/**
 * 竖屏播放时的视频源页面
 */
class LiveVideoSourcePortraitFragment :
    LocationFixedDialogFragment<LiveVideoViewModel, FragmentLiveSourcePortraitBinding>() {

    override val vbClass: KClass<FragmentLiveSourcePortraitBinding> =
        FragmentLiveSourcePortraitBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class


    override fun initView(savedInstanceState: Bundle?) {

        with(mBinding) {

            rvSource.apply {
                itemAnimator = null
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = LiveVideoSourceHorizontalAdapter(VideoSourceCompare()).apply {
                    post {
                        addItemDecoration( HorizontalItemDecoration())
                        submitList(mViewModel.sources.value)
                    }

                    setOnClickListener {
                        mViewModel.setPlayingVideoId(it)
                    }
                }
            }
        }

    }

    override fun initListener() {
        with(mBinding) {
            ivClose.clickNoRepeat {
                dismiss()
            }
        }
    }

    override fun createObserver() {

        with(mViewModel) {

            sources.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveVideoSourceHorizontalAdapter).apply {
                        val list = mViewModel.sources.value
                        val size = list?.size ?: 0
                        submitList(list)

                        notifyItemRangeChanged(0, size)
                    }
                }
            }
        }
    }

    class HorizontalItemDecoration(
        private val spacing: Int = 12.dp2px ,         // 常规间距大小（像素）
        private val leftRight: Int = 16.dp2px,         // 左右边距（像素）
        private val bottomSpacing: Int = 6.dp2px,   // 最后一个 item 的右边距离（像素）
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val itemCount = parent.adapter?.itemCount ?: 0 // 总 item 数

            // 包含边缘的情况
            outRect.top = 0
            outRect.bottom = 0
            outRect.left = if (position==0) leftRight else 0
            outRect.right = if (position == itemCount - 1) bottomSpacing else spacing
        }
    }
}