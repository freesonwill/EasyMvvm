package com.walisport.module.live.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.PositionedDialogFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.compare.VideoSourceCompare
import com.walisport.module.live.databinding.FragmentLiveSourcePortraitBinding
import com.walisport.module.live.ui.adapter.LiveVideoSourceHorizontalAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlin.reflect.KClass

class LiveVideoSourcePortraitFragment :
    PositionedDialogFragment<LiveVideoViewModel, FragmentLiveSourcePortraitBinding>() {

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
//                        addItemDecoration(LinearSpacingItemDecoration(16.dp2px, 8.dp2px))
                        submitList(mViewModel.sources.value)
                    }

                    setOnClickListener {
                        mViewModel.setPlayingVideoUrl(it)
                    }
                }
            }
        }

    }

    override fun initListener() {
        with(mBinding){
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
}