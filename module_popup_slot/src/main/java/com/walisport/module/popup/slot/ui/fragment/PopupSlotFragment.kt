package com.walisport.module.popup.slot.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
        mViewModel.popupSlotDataListLiveData.observe(viewLifecycleOwner) {
            if (it.get(0) != null && it[0].show) {
                mBinding.popupSlot0.visibility = View.VISIBLE
            }

            if (it.get(1) != null && it[1].show) {
                mBinding.popupSlot1.visibility = View.VISIBLE
            }
        }
    }



    companion object{
        const val TAG = "PopupSlotFragment"
    }


}





