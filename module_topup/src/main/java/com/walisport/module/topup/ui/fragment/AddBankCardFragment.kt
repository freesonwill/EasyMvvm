package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentAddBankCardBinding
import com.walisport.module.topup.ui.viewmodel.BankCardViewModel
import kotlin.reflect.KClass

/**
 * 添加银行卡
 */

class AddBankCardFragment : BaseFragment<BankCardViewModel, FragmentAddBankCardBinding>() {

    override val vbClass: KClass<FragmentAddBankCardBinding> = FragmentAddBankCardBinding::class
    override val vmClass: KClass<BankCardViewModel> = BankCardViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.add_bank, {
            findNavController().navigateUp()
        })
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.layAddBank.clickNoRepeat {

        }
    }

    override suspend fun createObserver() {

    }
}