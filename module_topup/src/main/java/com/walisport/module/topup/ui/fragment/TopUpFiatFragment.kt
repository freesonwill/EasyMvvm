package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentFiatBinding
import com.walisport.module.topup.ui.adapter.PayMethodAdapter
import com.walisport.module.topup.ui.adapter.PayMoneyAdapter
import com.walisport.module.topup.ui.viewmodel.FiatViewModel
import kotlin.reflect.KClass

/**
 * 充值-法币页面
 */

class TopUpFiatFragment : BaseFragment<FiatViewModel, FragmentFiatBinding>() {

    override val vbClass: KClass<FragmentFiatBinding> = FragmentFiatBinding::class
    override val vmClass: KClass<FiatViewModel> = FiatViewModel::class

    private val payTypeAdapter: PayMethodAdapter by lazy {
        PayMethodAdapter(object : PayMethodAdapter.PaTypeListener {
            override fun onSelectPayType(id: Int) {
                selectRechargeType(id)
                mViewModel.selectPayType(id)
            }
        })
    }

    private val payMoneyAdapter: PayMoneyAdapter by lazy {
        PayMoneyAdapter(object : PayMoneyAdapter.PayMoneyListener {
            override fun onSelectPayMoney(id: Int) {
                mViewModel.selectPayMoney(id)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvPayType.adapter = payTypeAdapter
        mBinding.rvRecharge.adapter = payMoneyAdapter
    }

    override fun initData() {
        super.initData()
        mViewModel.getPayTypeList()
    }

    override fun initListener() {
        mBinding.layLesson.clickNoRepeat {
            navigate(TopUpFragmentDirections.actionTopUpFragmentToFundDetailsFragment().apply {
                arguments.putString("type", "recharge")
            })
        }
        mBinding.layCustomer.clickNoRepeat {
            showToast(R.string.cus_service.getString())
        }
        mBinding.layAddCard.clickNoRepeat {
            navigate(R.id.action_topUpFragment_to_addBankCardFragment)
        }
        mBinding.layOrderDetail.clickNoRepeat {
            navigate(R.id.action_topUpFragment_to_orderDetailFragment)
        }
    }

    override suspend fun createObserver() {
        mViewModel.onPayMethodListener.observe(viewLifecycleOwner) {
            payTypeAdapter.submitList(it)
        }
        mViewModel.onPayMoneyListener.observe(viewLifecycleOwner) {
            payMoneyAdapter.submitList(it)
        }
    }

    private fun selectRechargeType(id: Int) {
        when (id) {
            1 -> {
                mBinding.layPayMoney.isVisible = false
                mBinding.layAddBank.isVisible = false
                mBinding.layOrderDetail.isVisible = true
            }

            3 -> {
                mBinding.layPayMoney.isVisible = false
                mBinding.layAddBank.isVisible = true
                mBinding.layOrderDetail.isVisible = false
            }

            else -> {
                mBinding.layPayMoney.isVisible = true
                mBinding.layAddBank.isVisible = false
                mBinding.layOrderDetail.isVisible = false
            }
        }
    }
}