package com.walisport.module.live.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveBetOnMenuBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxLayoutBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxTextViewBinding
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import kotlin.math.abs
import kotlin.reflect.KClass
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.BaseSideSheetDialogFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlinx.coroutines.delay

class LiveBetOnMenuFragment :
    BaseFragment<LiveBetOnMenuViewModel, FragmentLiveBetOnMenuBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnMenuBinding> = FragmentLiveBetOnMenuBinding::class
    override val vmClass: KClass<LiveBetOnMenuViewModel> = LiveBetOnMenuViewModel::class
    private val betOnViewModel: LiveBetOnViewModel by sharedViewModel<LiveBetOnViewModel, LiveBetOnFragment>()
    companion object {
        const val TAG = "LiveBetOnMenuFragment"
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.root)
        mViewModel.getMarketType()
        mViewModel.marketType.observe(viewLifecycleOwner) { it ->
            if (it == null) return@observe
            mBinding.llc.removeAllViews()
            var currentIndex = 0
            it.withIndex().forEach { (indexItems, items) ->
                val binding = LiveBetMenuFlexboxLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    mBinding.llc,
                    false
                )
                binding.apply {
                    if (currentIndex == it.size - 1) {
                        VLin.visibility = View.GONE
                    }
                    tvName.text = items.name
                }
                mViewModel.getMarketMenuByCode(items.code){listMenu->
                    listMenu.withIndex().forEach { (index, bean) ->
                        val textBinding = LiveBetMenuFlexboxTextViewBinding.inflate(
                            LayoutInflater.from(context),
                            mBinding.llc,
                            false
                        )
//                        if (bean.isSelect) {
//                            selectCode = items.code
//                            selectId = bean.marketId
//                        }
                        textBinding.apply {
                            tvContent.text = bean.marketName
//                            betOnViewModel.observeMarketMenu.value?.let {
//                                if (indexItems == (it[0] - 1) && index == it[1]) {
//                                    tvContent.isSelected = true
//                                }
//                            }
                            tvContent.clickNoRepeat {
                                betOnViewModel.setMarketMenuPosition((indexItems + 1), index)
                            }
                        }
                        binding.flexboxLayout.addView(textBinding.root)
                    }
                }

                mBinding.llc.addView(binding.root)
                currentIndex++
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {

    }


}