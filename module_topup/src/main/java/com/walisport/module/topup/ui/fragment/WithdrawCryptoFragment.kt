package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.ui.fragment.CoinDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
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
        const val TIP = "TIP"
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
        childFragmentManager.setFragmentResultListener(TIP, viewLifecycleOwner) { _, bundle ->
            val iid = bundle.getString("iid") ?: ""
            showTipDialog(iid)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getCoinList()
    }

    override fun initListener() {
        mBinding.layTopUpLesson.clickNoRepeat {
            navigate(
                arch.cayenne.lib.res.R.string.nav_module_web_fragment
                    .deeplink("url" to BizUrl.TOP_LESSON.url)
            )
        }
        mBinding.layWithdrawLesson.clickNoRepeat {
            navigate(
                arch.cayenne.lib.res.R.string.nav_module_web_fragment
                    .deeplink("url" to BizUrl.TOP_LESSON.url)
            )
        }
        mBinding.layCoin.clickNoRepeat {
            val location = IntArray(2)
            mBinding.layCoin.getLocationOnScreen(location)
            val offset = location[1] + 25.dp2px
            CoinDialogFragment.newInstance(offset).apply {
                setDismissListener(object : CoinDialogFragment.DialogDismissListener {
                    override fun onDismiss() {
                        ViewUtils.expandView(mBinding.ivArrow, false)
                    }

                    override fun onShow() {
                        ViewUtils.expandView(mBinding.ivArrow, true)
                    }
                })
            }.show(childFragmentManager)
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
        mBinding.btnRecharge.clickNoRepeat {
            val add = mBinding.edtAddress.text.toString()
            val coin = mBinding.edtCoin.text.toString() + " USDT"
            if (TextUtils.isEmpty(add)) {
                showToast(R.string.tip_empty_address.getString())
                return@clickNoRepeat
            }
            if (TextUtils.isEmpty(coin)) {
                showToast(R.string.tip_empty_input.getString())
                return@clickNoRepeat
            }
            showConfirmDialog(add, coin)
        }
    }

    private fun showConfirmDialog(address: String, coin: String) {
        val tag = "withdraw_confirm_fragment"
        if (childFragmentManager.findFragmentByTag(tag) != null) return
        WithdrawConfirmFragment.newInstance(address, coin).show(childFragmentManager, tag)
    }

    private fun showTipDialog(iid: String) {
        val tag = "withdraw_tip_fragment"
        if (childFragmentManager.findFragmentByTag(tag) != null) return
        WithdrawTipFragment.newInstance(iid).show(childFragmentManager, tag)
    }

    private fun showSelectAddress() {
        val tag = "sel_address_bottom_fragment"
        if (childFragmentManager.findFragmentByTag(tag) != null) return
        SelAddressBottomFragment.newInstance().show(childFragmentManager, tag)
    }

    override suspend fun createObserver() {
    }
}