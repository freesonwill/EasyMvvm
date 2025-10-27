package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.walisport.module.topup.databinding.FragmentAddAddressBinding
import com.walisport.module.topup.ui.viewmodel.AddAddressViewModel
import kotlin.reflect.KClass

/**
 * 添加到地址本底部弹窗
 */

class AddAddressBottomFragment :
    BaseBottomSheetFragment<AddAddressViewModel, FragmentAddAddressBinding>() {

    override val vbClass: KClass<FragmentAddAddressBinding> =
        FragmentAddAddressBinding::class
    override val vmClass: KClass<AddAddressViewModel> = AddAddressViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.ivClose.clickNoRepeat {
            this@AddAddressBottomFragment.dismiss()
        }
        mBinding.btnSave.clickNoRepeat {
            this@AddAddressBottomFragment.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        initBottomSheetStyle()
    }

    private fun initBottomSheetStyle() {
        val bottomSheet = dialog?.findViewById<FrameLayout>(
            R.id.design_bottom_sheet
        ) ?: return
        val screenHeight = resources.displayMetrics.heightPixels
        val targetHeight = (screenHeight * 0.98).toInt()
        val topOffset = screenHeight - targetHeight
        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false
            expandedOffset = topOffset
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = false
            skipCollapsed = false
            isHideable = false
        }
    }

    companion object {
        fun newInstance(): AddAddressBottomFragment {
            return AddAddressBottomFragment()
        }
    }
}