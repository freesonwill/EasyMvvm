package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentSelectBankBinding
import com.walisport.module.topup.ui.adapter.BankAdapter
import com.walisport.module.topup.ui.viewmodel.SelectBankViewModel
import kotlin.reflect.KClass

/**
 * 选择所属银行页面
 */

class SelectBankFragment : BaseFragment<SelectBankViewModel, FragmentSelectBankBinding>() {

    override val vbClass: KClass<FragmentSelectBankBinding> = FragmentSelectBankBinding::class
    override val vmClass: KClass<SelectBankViewModel> = SelectBankViewModel::class
    private val bankAdapter by lazy { BankAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.select_bank, {
            findNavController().navigateUp()
        })
        mBinding.rvBankList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bankAdapter
        }
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        super.initData()
        mViewModel.getBankList()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {
        mViewModel.bankData.observe(viewLifecycleOwner) {
            if (it != null) {
                bankAdapter.submitList(it)
            }
        }
    }
}