package com.walisport.module.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.databinding.FragmentLiveBetOnMenuBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxLayoutBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxTextViewBinding
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import kotlin.reflect.KClass

class LiveBetOnMenuFragment :
    BaseFragment<LiveBetOnMenuViewModel, FragmentLiveBetOnMenuBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnMenuBinding> = FragmentLiveBetOnMenuBinding::class
    override val vmClass: KClass<LiveBetOnMenuViewModel> = LiveBetOnMenuViewModel::class
    private val betOnViewModel: LiveBetOnViewModel by sharedViewModel<LiveBetOnViewModel, LiveBetOnFragment>()

    companion object {
        const val TAG = "LiveBetOnMenuFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
        mViewModel.getMarketType()
        mViewModel.marketType.observe(viewLifecycleOwner) { it ->
            if (it == null) return@observe
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
                mViewModel.getMarketMenuByCode(items.code).let { listMenu ->
                    listMenu.withIndex().forEach { (index, bean) ->
                        val textBinding = LiveBetMenuFlexboxTextViewBinding.inflate(
                            LayoutInflater.from(context),
                            mBinding.llc,
                            false
                        )
                        textBinding.apply {
                            tvContent.text = bean.marketName
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