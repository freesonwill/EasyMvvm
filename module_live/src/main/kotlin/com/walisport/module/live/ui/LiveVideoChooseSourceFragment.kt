package com.walisport.module.live.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.compare.VideoSourceCompare
import com.walisport.module.live.databinding.FragmentLiveChooseSourceBinding
import com.walisport.module.live.ui.adapter.LiveVideoSourceAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

class LiveVideoChooseSourceFragment :
    BaseFragment<LiveVideoViewModel, FragmentLiveChooseSourceBinding>(), CancelAdapt {
    override val vbClass: KClass<FragmentLiveChooseSourceBinding> =
        FragmentLiveChooseSourceBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.rvSource.apply {
            itemAnimator = null
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = LiveVideoSourceAdapter(VideoSourceCompare()).apply {
                post {
                    addItemDecoration(LinearSpacingItemDecoration(16.dp2px, 0))
                    submitList(mViewModel.sources.value)
                }
            }
        }

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }

    override fun onResume() {
        super.onResume()
        mBinding.root.fitsSystemWindows = false
    }


    companion object {
        const val TAG = "LiveVideoChooseSourceFragment"
    }

}