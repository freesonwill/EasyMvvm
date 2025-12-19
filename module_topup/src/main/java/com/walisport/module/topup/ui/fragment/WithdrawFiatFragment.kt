package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentWithdrawFiatBinding
import com.walisport.module.topup.ui.adapter.PayMoneyAdapter
import com.walisport.module.topup.ui.adapter.WithdrawTypeAdapter
import com.walisport.module.topup.ui.viewmodel.WithdrawFiatViewModel
import kotlin.reflect.KClass

/**
 * 提现-法币页面
 */

class WithdrawFiatFragment : BaseFragment<WithdrawFiatViewModel, FragmentWithdrawFiatBinding>() {

    override val vbClass: KClass<FragmentWithdrawFiatBinding> = FragmentWithdrawFiatBinding::class
    override val vmClass: KClass<WithdrawFiatViewModel> = WithdrawFiatViewModel::class

    private val withdrawTypeAdapter: WithdrawTypeAdapter by lazy {
        WithdrawTypeAdapter(object : WithdrawTypeAdapter.PaTypeListener {
            override fun onSelectPayType(id: Int) {
                selectRechargeType(id)
                mViewModel.selectRechargeType(id)
            }
        })
    }

    private val withdrawMoneyAdapter: PayMoneyAdapter by lazy {
        PayMoneyAdapter(object : PayMoneyAdapter.PayMoneyListener {
            override fun onSelectPayMoney(id: Int) {
                mViewModel.selectPayMoney(id)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvWithdrawType.adapter = withdrawTypeAdapter
        mBinding.rvWithdrawMoney.adapter = withdrawMoneyAdapter
    }

    override fun initData() {
        super.initData()
        mViewModel.getPayTypeList()
    }

    override fun initListener() {
        mBinding.layWithdrawNormal.isSelected = true
        mBinding.layWithdrawNormal.clickNoRepeat {
            mBinding.layWithdrawNormal.isSelected = true
            mBinding.layWithdrawYue.isSelected = false
        }
        mBinding.layWithdrawYue.clickNoRepeat {
            mBinding.layWithdrawNormal.isSelected = false
            mBinding.layWithdrawYue.isSelected = true
        }
        mBinding.betDetailLesson.clickNoRepeat {
            navigate(R.id.action_withdrawFragment_to_betDetailFragment)
        }
        mBinding.layWithdrawLesson.clickNoRepeat {
            navigate(
                arch.cayenne.lib.res.R.string.nav_module_web_fragment
                    .deeplink("url" to BizUrl.WITHDRAW_LESSON.url)
            )
        }
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
        mBinding.layAddBank.clickNoRepeat {
            navigate(R.id.action_withdrawFragment_to_addBankCardFragment)
        }
        mBinding.btnSmall.clickNoRepeat { }
        mBinding.btnMiddle.clickNoRepeat { }
        mBinding.btnBig.clickNoRepeat { }
        mBinding.btnAll.clickNoRepeat { }
    }

    override suspend fun createObserver() {
        mViewModel.onRechargeMethodListener.observe(viewLifecycleOwner) {
            withdrawTypeAdapter.submitList(it)
        }
        mViewModel.onPayMoneyListener.observe(viewLifecycleOwner) {
            withdrawMoneyAdapter.submitList(it)
        }
    }

    private fun selectRechargeType(id: Int) {
        mBinding.layEe.isVisible = id == 0
        mBinding.layBank.isVisible = id == 1
    }
}