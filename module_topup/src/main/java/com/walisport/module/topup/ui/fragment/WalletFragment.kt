package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentWalletBinding
import com.walisport.module.topup.ui.viewmodel.WalletViewModel
import kotlin.reflect.KClass

/**
 * 钱包主页
 */

class WalletFragment : BaseFragment<WalletViewModel, FragmentWalletBinding>() {

    override val vbClass: KClass<FragmentWalletBinding> = FragmentWalletBinding::class
    override val vmClass: KClass<WalletViewModel> = WalletViewModel::class

    private var defaultImmColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.fragment_title.getString(), {
                findNavController().navigateUp()
            })
        }
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llConttnet)
        super.onStart()
    }

    override fun initListener() {
        mBinding.root.touchBackPressed()
        mBinding.btnWalletRecharge.setOnClickListener {
            navigate(R.id.action_walletFragment_to_topUpFragment)
        }
        mBinding.btnWalletWithdraw.setOnClickListener {
            navigate(R.id.action_walletFragment_to_withdrawFragment)
        }
        mBinding.layWithdrawRecord.setOnClickListener {
            navigate(R.id.action_walletFragment_to_withdrawRecordFragment)
        }
        mBinding.layTopUpRecord.setOnClickListener {
            navigate(R.id.action_walletFragment_to_topUpRecordFragment)
        }
    }

    override suspend fun createObserver() {
    }

    override fun initData() {
    }
}