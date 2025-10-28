package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.walisport.module.topup.data.entity.AddressBean
import com.walisport.module.topup.databinding.FragmentSelectAddressBinding
import com.walisport.module.topup.ui.adapter.AddressAdapter
import com.walisport.module.topup.ui.viewmodel.SelectAddressViewModel
import kotlin.reflect.KClass

/**
 * 选择地址底部弹窗
 */

class SelAddressBottomFragment :
    BaseBottomSheetFragment<SelectAddressViewModel, FragmentSelectAddressBinding>() {

    override val vbClass: KClass<FragmentSelectAddressBinding> = FragmentSelectAddressBinding::class
    override val vmClass: KClass<SelectAddressViewModel> = SelectAddressViewModel::class
    private val addressAdapter by lazy { AddressAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerAddress.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = addressAdapter
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getAddressList()
    }

    override fun initListener() {
        addressAdapter.setOnItemClickListener(object : AddressAdapter.OnItemClickListener {
            override fun onItemClick(bean: AddressBean) {
                val result = Bundle().apply {
                    putString("address", bean.address)
                    putString("type", bean.coinType)
                    putString("network", bean.addressType)
                }
                setFragmentResult(WithdrawCryptoFragment.RESULT, result)
                this@SelAddressBottomFragment.dismiss()
                this@SelAddressBottomFragment.dialog?.dismiss()
            }
        })
        mBinding.ivClose.clickNoRepeat {
            this@SelAddressBottomFragment.dismiss()
            this@SelAddressBottomFragment.dialog?.dismiss()
        }
        mBinding.tvManager.clickNoRepeat {
            this@SelAddressBottomFragment.dismiss()
            this@SelAddressBottomFragment.dialog?.dismiss()
            navigate(WithdrawFragmentDirections.actionWithdrawFragmentToAddressFragment())
        }
    }

    override suspend fun createObserver() {
        mViewModel.addressData.observe(viewLifecycleOwner) {
            if (it != null) {
                addressAdapter.submitList(it)
            }
        }
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
        val targetHeight = (screenHeight * 0.98).toInt()
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
        fun newInstance(): SelAddressBottomFragment {
            return SelAddressBottomFragment()
        }
    }
}