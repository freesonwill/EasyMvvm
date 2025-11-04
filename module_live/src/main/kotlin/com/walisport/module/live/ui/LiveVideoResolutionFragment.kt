package com.walisport.module.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.data.model.VideoResolutionBean
import com.walisport.module.live.databinding.FragmentLiveVideoResolutionBinding
import com.walisport.module.live.ui.adapter.LiveVideoResolutionVerticalAdapter
import com.walisport.module.live.ui.adapter.VideoResolutionCompare
import com.walisport.module.live.ui.viewmodel.LiveVideoPlayerViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoSourceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的选择清晰度页
 */
class LiveVideoResolutionFragment :
    BaseFragment<LiveVideoSourceViewModel, FragmentLiveVideoResolutionBinding>() {
    override val vbClass: KClass<FragmentLiveVideoResolutionBinding> =
        FragmentLiveVideoResolutionBinding::class
    override val vmClass: KClass<LiveVideoSourceViewModel> = LiveVideoSourceViewModel::class

    private val videoPlayerViewModel: LiveVideoPlayerViewModel by sharedViewModel<LiveVideoPlayerViewModel, LiveVideoLandscapeFragment>()

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
            adapter = LiveVideoResolutionVerticalAdapter(VideoResolutionCompare()).apply {
                post {
                    addItemDecoration(LinearSpacingItemDecoration(16.dp2px, 8.dp2px))

                    lifecycleScope.launch(Dispatchers.IO) {
                        val beanList = videoPlayerViewModel.getVideoResolutionList()

                        lifecycleScope.launch { submitList(beanList) }
                    }
                }

                setOnClickListener {
                    videoPlayerViewModel.changeResolution(it)
                }
            }
        }

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {
        with(videoPlayerViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveVideoResolutionVerticalAdapter).apply {

                        val list =
                            it.source.firstOrNull { ele -> ele.isPlaying }?.liveStreams?.map {
                                VideoResolutionBean(
                                    it.streamType,
                                    it.selected
                                )
                            }
                        val size = list?.size ?: 0
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
        const val TAG = "LiveVideoResolutionFragment"
    }

}