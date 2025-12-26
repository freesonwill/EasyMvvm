package com.walisport.module.popup.slot.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
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

            if (showList[0]) {
                mBinding.popupSlot0.apply {
                    visibility = View.VISIBLE
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
                }.visibility = View.VISIBLE
            } else {
                mBinding.popupSlot0.visibility = View.GONE
            }

            if (showList[1]) {
                mBinding.popupSlot1.apply {
                    visibility = View.VISIBLE
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
                }.visibility = View.VISIBLE
            } else {
                mBinding.popupSlot1.visibility = View.GONE
            }
        }
    }


    companion object {
        const val TAG = "PopupSlotFragment"
    }


}





