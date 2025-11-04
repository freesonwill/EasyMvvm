package com.walisport.module.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.compare.VideoSourceBeanCompare
import com.walisport.module.live.databinding.FragmentLiveSourceLandscapeBinding
import com.walisport.module.live.ui.adapter.LiveVideoSourceVerticalAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoSourceViewModel
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的选择数据源页
 */
class LiveVideoSourceLandscapeFragment :
    BaseFragment<LiveVideoSourceViewModel, FragmentLiveSourceLandscapeBinding>() {
    override val vbClass: KClass<FragmentLiveSourceLandscapeBinding> =
        FragmentLiveSourceLandscapeBinding::class
    override val vmClass: KClass<LiveVideoSourceViewModel> = LiveVideoSourceViewModel::class

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

        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mBinding.rvSource.apply {
            itemAnimator = null
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = LiveVideoSourceVerticalAdapter(VideoSourceBeanCompare()).apply {
                post {
                    addItemDecoration(LinearSpacingItemDecoration(16.dp2px, 8.dp2px))
                    submitList(mViewModel.liveVideoBean.value?.source)
                }

                setOnClickListener {
                    mViewModel.setPlayingVideoId(it)
                }
            }
        }

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {

            }

            liveVideoBean.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveVideoSourceVerticalAdapter).apply {
                        val list = mViewModel.liveVideoBean.value
                        val size = list?.source?.size ?: 0
                        submitList(list?.source)

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
        const val TAG = "LiveVideoSourceLandscapeFragment"
    }

}