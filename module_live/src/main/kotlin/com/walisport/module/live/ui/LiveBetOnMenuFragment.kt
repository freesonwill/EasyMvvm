package com.walisport.module.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.clickNoRepeatSingle
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
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
    private val fixedSkin = SkinType.getLogicSkinType(SkinType.SKIN_BLACK_RED.value)

    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        StatusBarConfig.statusBarDarkFont = false
        setStatusBar(StatusBarConfig,mBinding.root)
        SkinnableResourceManager.setFixedSkin(fixedSkin)
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinnableResourceManager.setFixedSkin(null)
    }

    override fun initView(savedInstanceState: Bundle?) {
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
                            tvContent.clickNoRepeatSingle {
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

    override suspend fun createObserver() {

    }


}