package com.walisport.module.popup.slot.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import com.walisport.module.popup.slot.data.PopupSlotRepository
import com.walisport.module.popup.slot.databinding.FragmentPopupSlotBinding
import com.walisport.module.popup.slot.ui.viewmodel.PopUpSlotViewModel
import com.walisport.module.popup.slot.ui.viewmodel.PopupControlViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
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

    private val popupControlViewModel: PopupControlViewModel by viewModel()


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.popupSlot.setPositionCallbacks { x , y ->
            mViewModel.anchorX = x; mViewModel.anchorY = y
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        popupControlViewModel.isShowButtonListener.observe(this) {
            if (it) {
                show()
            } else {
                hide()
            }
        }
    }

    fun adjustPosition() {
        // 檢查當前 Fragment 是否已經是目標父 Fragment 的子 Fragment
        setFloatingViewPosition(requireActivity().resources.displayMetrics.heightPixels)

    }

    private fun setFloatingViewPosition(screenHeight: Int) {

        val floatingView = mBinding.popupSlot
        val layoutParams = floatingView.layoutParams as FrameLayout.LayoutParams

        if (mViewModel.anchorX != PopupSlotRepository.UNINITIALIZED_ANCHOR) {
            layoutParams.leftMargin = mViewModel.anchorX.toInt()
        } else {
            // 設定懸浮按鈕的橫向位置，將其放在螢幕寬度的3/4處
            layoutParams.leftMargin =
                (resources.displayMetrics.widthPixels * 3 / 4)
            mViewModel.anchorX = layoutParams.leftMargin.toFloat()
        }

        if (mViewModel.anchorY != PopupSlotRepository.UNINITIALIZED_ANCHOR) {
            layoutParams.topMargin = mViewModel.anchorY.toInt()
        } else {
            // 設定懸浮按鈕的縱向位置，將其放在螢幕高度的2/3處
            layoutParams.topMargin = (screenHeight * 2 / 3)
            mViewModel.anchorY = layoutParams.topMargin.toFloat()
        }

        floatingView.layoutParams = layoutParams
    }

    private fun show() {
        mBinding.root.visibility = View.VISIBLE
    }

    private fun hide() {
        mBinding.root.visibility = View.GONE
    }

    companion object {
        const val TAG = "PopupSlotFragment"
    }


}
