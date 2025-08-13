package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentTopupBinding
import com.walisport.module.topup.ui.viewmodel.TopUpViewModel
import kotlin.reflect.KClass

/**
 * 充值页面
 */

class TopUpFragment : BaseFragment<TopUpViewModel, FragmentTopupBinding>() {

    override val vbClass: KClass<FragmentTopupBinding> = FragmentTopupBinding::class
    override val vmClass: KClass<TopUpViewModel> = TopUpViewModel::class

    companion object {
        const val ALI_PAY = "AliPay"
        const val CHAT_PAY = "WeChatPay"
        const val BANK_PAY = "BANK"
        const val RMB_PAY = "RMB"
        const val YUN_PAY = "YUN"
        const val EE_PAY = "EE"
    }


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.recharge.getString(), {
                findNavController().navigateUp()
            })
            ivRefreshBalance.clickNoRepeat {

            }
            btnPayWechat.clickNoRepeat {
                selectPayMethod(CHAT_PAY)
            }
            btnPayAli.clickNoRepeat {
                selectPayMethod(ALI_PAY)
            }
            btnPayEe.clickNoRepeat {
                selectPayMethod(EE_PAY)
            }
            btnPayBank.clickNoRepeat {
                selectPayMethod(BANK_PAY)
            }
            btnPayRmb.clickNoRepeat {
                selectPayMethod(RMB_PAY)
            }
            btnPayYun.clickNoRepeat {
                selectPayMethod(YUN_PAY)
            }
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    private fun selectPayMethod(type: String) {
        if ("" == type)
            return
        mBinding.btnPayWechat.setPayMethodSelected(false)
        mBinding.btnPayAli.setPayMethodSelected(false)
        mBinding.btnPayEe.setPayMethodSelected(false)
        mBinding.btnPayBank.setPayMethodSelected(false)
        mBinding.btnPayRmb.setPayMethodSelected(false)
        mBinding.btnPayYun.setPayMethodSelected(false)
        when (type) {
            ALI_PAY -> mBinding.btnPayAli.setPayMethodSelected(true)
            CHAT_PAY -> mBinding.btnPayWechat.setPayMethodSelected(true)
            BANK_PAY -> mBinding.btnPayBank.setPayMethodSelected(true)
            RMB_PAY -> mBinding.btnPayRmb.setPayMethodSelected(true)
            YUN_PAY -> mBinding.btnPayYun.setPayMethodSelected(true)
            EE_PAY -> mBinding.btnPayEe.setPayMethodSelected(true)
        }
    }

}