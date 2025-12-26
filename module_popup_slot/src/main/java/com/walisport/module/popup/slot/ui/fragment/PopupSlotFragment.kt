package com.walisport.module.popup.slot.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.adapter.BannerUrlImageAdapter
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.popup.slot.R
import com.walisport.module.popup.slot.databinding.FragmentPopupSlotBinding
import com.walisport.module.popup.slot.ui.viewmodel.PopUpSlotViewModel
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/12/5 19:23
 * @description:
 */
class PopupSlotFragment :
    BaseFragment<PopUpSlotViewModel, FragmentPopupSlotBinding>() {
    override val vbClass: KClass<FragmentPopupSlotBinding> =
        FragmentPopupSlotBinding::class
    override val vmClass: KClass<PopUpSlotViewModel> = PopUpSlotViewModel::class


    override fun initView(savedInstanceState: Bundle?) {
        val radius = 6.dp2px.toFloat()
        with(mBinding.popupSlot0.binding) {
            vpBanner.setBannerRound(radius)
            vpBanner.isAutoLoop(false)
            // 设置滑动时长丝滑,不影响曲线,
            vpBanner.setScrollTime(600)  // 0.5 秒
            vpBanner.setPageTransformer(CustomCurveTransformer())
            // 启动轮播
        }

        with(mBinding.popupSlot1.binding) {
            vpBanner.setBannerRound(radius)
            vpBanner.isAutoLoop(false)
            // 设置滑动时长丝滑,不影响曲线,
            vpBanner.setScrollTime(600)  // 0.5 秒
            vpBanner.setPageTransformer(CustomCurveTransformer())
            // 启动轮播
        }
    }

    override fun initListener() {
        mBinding.popupSlot0.setPerformClick {

        }

        mBinding.popupSlot0.binding.ivPopupSlotClose.clickNoRepeat {
            mViewModel.hideView(0)
        }

        mBinding.popupSlot1.setPerformClick { }

        mBinding.popupSlot1.binding.ivPopupSlotClose.clickNoRepeat {
            mViewModel.hideView(1)
        }
    }

    override suspend fun createObserver() {
        //监听弹窗显示与隐藏
        mViewModel.popupShowLiveData.observe(viewLifecycleOwner) {
            val showList = it
            val initialAlpha = 0.5f
            val targetAlpha = 1f
            val alphaDuration = 320L
            val translationDistance = 20f
            val translationDuration = 80L
            val translationBackDistance = -40f
            val translationBackDuration = 160L

            // 假设 PopupSlotRepository 有两个布尔变量 slot0Animated 和 slot1Animated
            if (showList[0]) {
                mBinding.popupSlot0.visibility = View.VISIBLE
                if (!mViewModel.slot0Animated) {
                    mBinding.popupSlot0.apply {
                        alpha = initialAlpha
                        animate().alpha(targetAlpha).setDuration(alphaDuration).start()
                        animate()
                            .translationXBy(translationDistance).setDuration(translationDuration)
                            .withEndAction {
                                animate()
                                    .translationXBy(translationBackDistance)
                                    .setDuration(translationBackDuration).withEndAction {
                                        animate()
                                            .translationXBy(translationDistance)
                                            .setDuration(translationDuration).start()
                                    }.start()
                            }.start()
                    }
                    mViewModel.slot0Animated = true
                }
            } else {
                mBinding.popupSlot0.visibility = View.GONE
            }

            if (showList[1]) {
                mBinding.popupSlot1.visibility = View.VISIBLE
                if (!mViewModel.slot1Animated) {
                    mBinding.popupSlot1.apply {
                        alpha = initialAlpha
                        animate().alpha(targetAlpha).setDuration(alphaDuration).start()
                        animate()
                            .translationXBy(translationDistance).setDuration(translationDuration)
                            .withEndAction {
                                animate()
                                    .translationXBy(translationBackDistance)
                                    .setDuration(translationBackDuration).withEndAction {
                                        animate()
                                            .translationXBy(translationDistance)
                                            .setDuration(translationDuration).start()
                                    }.start()
                            }.start()
                    }
                    mViewModel.slot1Animated = true
                }
            } else {
                mBinding.popupSlot1.visibility = View.GONE
            }
        }

        mViewModel.popupSlotDataListLiveData.observe(viewLifecycleOwner) {
            val dataList = it
            // 可根据需要将此变量提到类属性或通过参数传递
            mBinding.popupSlot0.binding.vpBanner.apply {
                setAdapter(BannerUrlImageAdapter(dataList[0].data.map {
                    Pair(it.bottomImagePath, R.drawable.popup_slot_placeholder)
                }))
                setLoopTime(LOOP_TIME)
                isAutoLoop(true)
                start()
            }
            mBinding.popupSlot1.binding.vpBanner.apply {
                setAdapter(BannerUrlImageAdapter(dataList[1].data.map {
                    Pair(
                        it.bottomImagePath,
                        R.drawable.popup_slot_placeholder
                    )
                }))
                setLoopTime(LOOP_TIME)
                isAutoLoop(true)
                start()
            }
        }
    }


    companion object {
        const val TAG = "PopupSlotFragment"
        const val LOOP_TIME = 3000L

    }


}





