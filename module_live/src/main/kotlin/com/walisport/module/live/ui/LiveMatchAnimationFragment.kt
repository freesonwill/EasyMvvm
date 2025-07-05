package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveMatchAnimationBinding
import com.walisport.module.live.databinding.FragmentLiveMatchMediaBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchAnimationViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import kotlin.reflect.KClass


/**
 * 比赛动画页
 */
class LiveMatchAnimationFragment :
    BaseFragment<LiveMatchAnimationViewModel, FragmentLiveMatchAnimationBinding>() {
    override val vbClass: KClass<FragmentLiveMatchAnimationBinding> =
        FragmentLiveMatchAnimationBinding::class
    override val vmClass: KClass<LiveMatchAnimationViewModel> = LiveMatchAnimationViewModel::class

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
    }


    override fun initListener() {

    }

    override fun createObserver() {
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setMatchId(it)
            mViewModel.createObserver()
        }

        with(mViewModel) {
            //比赛动画url监听
            animationLiveUrl.observe(viewLifecycleOwner) { url ->
                url?.also {
                    "url:$url".logd(TAG)
                    mBinding.animationView.loadUrl(it)
                }
            }

        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }


    companion object {
        const val TAG = "LiveMatchAnimationFragment"
    }


}