package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.FrameLayout
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.ui.view.MoneyKeyboardView
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.helper.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentCustomMoneyBinding
import com.walisport.module.topup.ui.viewmodel.CustomMoneyViewModel
import kotlin.reflect.KClass

/**
 * 自定义金额底部弹窗
 */

class CustomMoneyFragment :
    BaseBottomSheetFragment<CustomMoneyViewModel, FragmentCustomMoneyBinding>() {

    override val vbClass: KClass<FragmentCustomMoneyBinding> = FragmentCustomMoneyBinding::class
    override val vmClass: KClass<CustomMoneyViewModel> = CustomMoneyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.viewKeyboard.setOnMoneyClickListener(object : MoneyKeyboardView.OnClickListener {
            override fun onMoneyClick(number: Int) {
                val cash = mBinding.edtMoney.text.toString() + number.toString()
                mBinding.edtMoney.setText(cash)
            }

            override fun onCustomClick(number: Int) {
                mBinding.edtMoney.setText(number.toString())
            }

            override fun onClear() {
                mBinding.edtMoney.setText("")
            }

            override fun onConfirm() {
                val str = mBinding.edtMoney.text.toString()
                if (TextUtils.isEmpty(str)) {
                    showToast(R.string.tip_empty_input.getString())
                    return
                }
                mBinding.root.postDelayed({
                    this@CustomMoneyFragment.dismiss()
                    this@CustomMoneyFragment.dialog?.dismiss()
                }, 300L)
            }
        })
    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
        super.onStart()
        initBottomSheetStyle()
    }

    private fun initBottomSheetStyle() {
        val bottomSheet = dialog?.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        val screenHeight = resources.displayMetrics.heightPixels
        val targetHeight = (screenHeight * 0.50).toInt()
        val topOffset = screenHeight - targetHeight
        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false
            expandedOffset = topOffset
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = false
            isCancelable = true
            skipCollapsed = false
            isHideable = true
        }
    }

    companion object {
        fun newInstance(): CustomMoneyFragment {
            return CustomMoneyFragment()
        }
    }
}