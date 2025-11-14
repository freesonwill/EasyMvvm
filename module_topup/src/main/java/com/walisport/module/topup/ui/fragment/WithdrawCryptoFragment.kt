package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.fragment.CoinDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentWithdrawCryptoBinding
import com.walisport.module.topup.ui.viewmodel.CryptoViewModel
import kotlin.reflect.KClass

/**
 * 提现-加密货币页面
 */

class WithdrawCryptoFragment : BaseFragment<CryptoViewModel, FragmentWithdrawCryptoBinding>() {

    override val vbClass: KClass<FragmentWithdrawCryptoBinding> =
        FragmentWithdrawCryptoBinding::class
    override val vmClass: KClass<CryptoViewModel> = CryptoViewModel::class

    companion object {
        const val RESULT = "RESULT"
    }

    override fun initView(savedInstanceState: Bundle?) {
        childFragmentManager.setFragmentResultListener(RESULT, viewLifecycleOwner) { _, bundle ->
            val address = bundle.getString("address")
            val type = bundle.getString("type")
            val network = bundle.getString("network")
            mBinding.edtAddress.setText(address)
            mBinding.tvCoin.text = type
            mBinding.tvNetworkType.text = network
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getCoinList()
    }

    override fun initListener() {
        mBinding.layTopUpLesson.clickNoRepeat {
            navigate(WithdrawFragmentDirections.actionWithdrawFragmentToFundDetailsFragment().apply {
                arguments.putString("type", "recharge")
            })
        }
        mBinding.layCoin.clickNoRepeat {
            val location = IntArray(2)
            mBinding.layCoin.getLocationOnScreen(location)
            val offset = location[1] + 25.dp2px
            CoinDialogFragment.newInstance(offset).apply {
                setDismissListener(object : CoinDialogFragment.DialogDismissListener {
                    override fun onDismiss() {
                        ViewUtils.expandView(mBinding.ivArrow,false)
                    }

                    override fun onShow() {
                        ViewUtils.expandView(mBinding.ivArrow,true)
                    }
                })
            }.show(childFragmentManager)
        }
        mBinding.layWithdrawLesson.clickNoRepeat {
            navigate(WithdrawFragmentDirections.actionWithdrawFragmentToFundDetailsFragment().apply {
                arguments.putString("type", "withdraw")
            })
        }
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
        mBinding.ivAddress.clickNoRepeat {
            showSelectAddress()
        }
        mBinding.btnSmall.clickNoRepeat { }
        mBinding.btnMiddle.clickNoRepeat { }
        mBinding.btnBig.clickNoRepeat { }
        mBinding.btnAll.clickNoRepeat { }
    }

    private fun showSelectAddress() {
        val tag = "sel_address_bottom_fragment"
        if (childFragmentManager.findFragmentByTag(tag) != null) return
        SelAddressBottomFragment.newInstance().show(childFragmentManager, tag)
    }

    override suspend fun createObserver() {
    }
}