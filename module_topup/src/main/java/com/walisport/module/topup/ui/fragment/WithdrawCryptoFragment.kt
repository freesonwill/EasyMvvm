package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
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

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initData() {
        super.initData()
        mViewModel.getCoinList()
    }

    override fun initListener() {
        mBinding.layTopUpLesson.clickNoRepeat {
            val args = Bundle()
            args.putBoolean("isTopUp", true)
            navigate(R.id.action_withdrawFragment_to_lessonFragment, args)
        }
        mBinding.layWithdrawLesson.clickNoRepeat {
            val args = Bundle()
            args.putBoolean("isTopUp", false)
            navigate(R.id.action_withdrawFragment_to_lessonFragment, args)
        }
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
    }

    override suspend fun createObserver() {

    }
}