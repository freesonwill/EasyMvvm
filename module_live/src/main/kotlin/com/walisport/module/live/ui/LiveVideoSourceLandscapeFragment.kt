package com.walisport.module.live.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.compare.VideoSourceCompare
import com.walisport.module.live.databinding.FragmentLiveSourceLandscapeBinding
import com.walisport.module.live.ui.adapter.LiveVideoSourceVerticalAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的选择数据源页
 */
class LiveVideoSourceLandscapeFragment :
    BaseFragment<LiveVideoViewModel, FragmentLiveSourceLandscapeBinding>(), CancelAdapt {
    override val vbClass: KClass<FragmentLiveSourceLandscapeBinding> =
        FragmentLiveSourceLandscapeBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.rvSource.apply {
            itemAnimator = null
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = LiveVideoSourceVerticalAdapter(VideoSourceCompare()).apply {
                post {
                    addItemDecoration(LinearSpacingItemDecoration(16.dp2px, 8.dp2px))
                    submitList(mViewModel.sources.value)
                }

                setOnClickListener {
                    mViewModel.setPlayingVideoId(it)
                }
            }
        }

    }

    override fun initListener() {

    }

    override fun createObserver() {

        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {

            }

            sources.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveVideoSourceVerticalAdapter).apply {
                        val list = mViewModel.sources.value
                        val size = list?.size?:0
                        submitList(list)

                        notifyItemRangeChanged(0, size)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.root.fitsSystemWindows = false
    }


    companion object {
        const val TAG = "LiveVideoChooseSourceFragment"
    }

}