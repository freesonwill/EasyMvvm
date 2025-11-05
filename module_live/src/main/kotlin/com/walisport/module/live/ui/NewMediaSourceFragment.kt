package com.walisport.module.live.ui

import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimensionPixelSize
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import com.walisport.module.live.compare.MediaSourceBeanCompare
import com.walisport.module.live.data.model.MediaSource
import com.walisport.module.live.data.model.MediaSourceType
import com.walisport.module.live.databinding.FragmentLiveMediaSourceBinding
import com.walisport.module.live.ui.adapter.LiveMediaSourceAdapter
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoSourceViewModel
import kotlin.reflect.KClass


class NewMediaSourceFragment :
    BaseFragment<LiveVideoSourceViewModel, FragmentLiveMediaSourceBinding>() {

    override val vbClass: KClass<FragmentLiveMediaSourceBinding> =
        FragmentLiveMediaSourceBinding::class
    override val vmClass: KClass<LiveVideoSourceViewModel> = LiveVideoSourceViewModel::class

    private val mediaViewModel: LiveMatchMediaViewModel by sharedViewModel<LiveMatchMediaViewModel, LiveMatchMediaFragment>()

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    private var isDismissing = false


    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        with(mBinding) {

            rvSource.apply {
                itemAnimator = null
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = LiveMediaSourceAdapter(MediaSourceBeanCompare()).apply {
                    post {
                        addItemDecoration(HorizontalItemDecoration())

                        val list: MutableList<MediaSource> = mutableListOf()

                        if (!mViewModel.animationLiveUrl.value.isNullOrEmpty()) {
                            list.add(
                                MediaSource(
                                    MediaSourceType.ANIMATION,
                                    mViewModel.animationLiveUrl.value, null, true
                                )
                            )
                        }

                        mViewModel.liveVideoBean.value?.source?.map { bean ->
                            MediaSource(
                                MediaSourceType.VIDEO,
                                null,
                                bean, bean.isPlaying
                            )
                        }
                            ?.let { it1 -> list.addAll(it1) }
                        submitList(list)
                    }

                    setOnClickListener { mediaSourceItem ->
                        onMediaSourceItemClicked(mediaSourceItem)
                    }
                }
            }
        }

        //进入时展示动画
        mBinding.ctSource.addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                val location = IntArray(2)
                mBinding.ctSource.getLocationOnScreen(location)

                if (location[0] == 0) {
                    mBinding.ctSource.removeOnLayoutChangeListener(this)
                    playEnterAnimations()
                }
            }
        })
    }

    override fun initListener() {
        with(mBinding) {
            ivClose.clickNoRepeat {
                dismiss()
            }
        }

    }


    override suspend fun createObserver() {
        with(mViewModel) {

            liveVideoBean.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveMediaSourceAdapter).apply {
                        val list: MutableList<MediaSource> = mutableListOf()

                        if (!mViewModel.animationLiveUrl.value.isNullOrEmpty()) {
                            list.add(
                                MediaSource(
                                    MediaSourceType.ANIMATION,
                                    mViewModel.animationLiveUrl.value, null, true
                                )
                            )
                        }

                        mViewModel.liveVideoBean.value?.source?.map { bean ->
                            MediaSource(
                                MediaSourceType.VIDEO,
                                null,
                                bean, bean.isPlaying
                            )
                        }
                            ?.let { it1 -> list.addAll(it1) }

                        notifyItemRangeChanged(0, list.size)
                    }
                }
            }
        }

    }


    private fun playEnterAnimations() {
        mBinding.ctSource.startSafeAnimateSet(
            {
                playTogether(
                    ValueAnimator.ofInt(
                        com.walisport.module.live.R.dimen.video_source_portrait_margin_top.getDimensionPixelSize(),
                        0
                    ).apply {
                        addUpdateListener {
                            mBinding.ctSource.translationY = (it.animatedValue as Int).toFloat()
                        }
                    },
                )
            },
            duration = AnimationController[AnimType.popupEnter]!!.duration,
            interpolator = LinearInterpolator(),
            start = true
        )

    }

    private fun onMediaSourceItemClicked(item: MediaSource) {
        if (item.mediaSourceType == MediaSourceType.ANIMATION) {
            item.isPlaying = true
            (mBinding.rvSource.adapter as LiveMediaSourceAdapter).currentList.forEach {
                if (it.mediaSourceType == MediaSourceType.VIDEO) {
                    it.isPlaying = false
                }
            }
        } else if (item.mediaSourceType == MediaSourceType.VIDEO) {
            item.isPlaying = true
            mViewModel.setPlayingVideoId(item.videoSourceBean!!.id)
            (mBinding.rvSource.adapter as LiveMediaSourceAdapter).currentList.filter {
                it.mediaSourceType == MediaSourceType.VIDEO
            }.forEach { it.isPlaying = false }

        }
    }


    class HorizontalItemDecoration(
        private val spacing: Int = 12.dp2px,         // 常规间距大小（像素）
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
            outRect.left = if (position == 0) leftRight else 0
            outRect.right = if (position == itemCount - 1) bottomSpacing else spacing
        }
    }

    private fun dismiss() {
        if (isDismissing) return
        isDismissing = true

        mBinding.ctSource.startSafeAnimateSet(
            {
                playTogether(
                    ValueAnimator.ofInt(
                        0,
                        com.walisport.module.live.R.dimen.video_source_portrait_margin_top.getDimensionPixelSize()
                    ).apply {
                        addUpdateListener {
                            mBinding.ctSource.translationY = (it.animatedValue as Int).toFloat()
                        }
                    },
                )
                doOnEnd {
                    remove()
                }
            },
            duration = AnimationController[AnimType.popupEnter]!!.duration,
            interpolator = LinearInterpolator(),
            start = true
        )
    }

    private fun remove() {
        mainViewModel.hideMediaSourceFragment.value = true
    }


    companion object {
        const val TAG = "LiveMediaSourceFragmentNew"
    }

}
