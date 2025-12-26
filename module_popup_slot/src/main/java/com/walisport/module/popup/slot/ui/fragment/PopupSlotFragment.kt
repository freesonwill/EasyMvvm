package com.walisport.module.popup.slot.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.popup.slot.data.PopupSlotBean.Companion.UNINITIALIZED_ANCHOR
import com.walisport.module.popup.slot.databinding.FragmentPopupSlotBinding
import com.walisport.module.popup.slot.ui.viewmodel.PopUpSlotViewModel
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/12/5 19:23
 * @description:
 */
class PopupSlotFragment :
    BaseFragment<PopUpSlotViewModel , FragmentPopupSlotBinding>() {
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
        mViewModel.popupSlotDataListLiveData.observe(viewLifecycleOwner) {
            "popupSlotDataListLiveData.observed. $it".logi("PopupSlotFragment")
            if (it.get(0) != null && it[0].show) {
                "it[0].show == true , set visible".logi("PopupSlotFragment")
                mBinding.popupSlot0.visibility = View.VISIBLE
            }

            if (it.get(1) != null && it[1].show) {
                "it[1].show == true , set visible".logi("PopupSlotFragment")
                mBinding.popupSlot1.visibility = View.VISIBLE
            }
        }
    }

    fun adjustPosition() {
        // 檢查當前 Fragment 是否已經是目標父 Fragment 的子 Fragment
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)

    }

    private fun setFloatingViewPosition(screenHeight: Int) {

        mViewModel.popupSlotDataListLiveData.value?.forEachIndexed { index , popupSlotBean ->
            if (!popupSlotBean.show) {
                return@forEachIndexed
            }
            val floatingView = if (index == 0) mBinding.popupSlot0 else mBinding.popupSlot1
            val layoutParams = floatingView.layoutParams as FrameLayout.LayoutParams

            if (popupSlotBean.anchorX != UNINITIALIZED_ANCHOR) {
                layoutParams.leftMargin = popupSlotBean.anchorX.toInt()
            } else {
                layoutParams.leftMargin =
                    resources.displayMetrics.widthPixels
                popupSlotBean.anchorX = layoutParams.leftMargin.toFloat()
            }

            if (popupSlotBean.anchorY != UNINITIALIZED_ANCHOR) {
                layoutParams.topMargin = popupSlotBean.anchorY.toInt()
            } else {
                // 設定懸浮按鈕的縱向位置，將其放在螢幕高度的2/3處
                layoutParams.topMargin = (screenHeight * 2 / 3)
                popupSlotBean.anchorY = layoutParams.topMargin.toFloat()
            }

            floatingView.layoutParams = layoutParams
        }


    }


    companion object {
        const val TAG = "PopupSlotFragment"
    }


}
