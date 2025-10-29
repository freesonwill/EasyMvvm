package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.ViewBarEditBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.WithdrawTabType
import com.walisport.module.topup.databinding.FragmentWithdrawBinding
import com.walisport.module.topup.ui.viewmodel.WithdrawViewModel
import kotlin.reflect.KClass

/**
 * 提现页面
 */

class WithdrawFragment : BaseFragment<WithdrawViewModel, FragmentWithdrawBinding>() {

    override val vbClass: KClass<FragmentWithdrawBinding> = FragmentWithdrawBinding::class
    override val vmClass: KClass<WithdrawViewModel> = WithdrawViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val bind = ViewBarEditBinding.inflate(LayoutInflater.from(context), root, false)
            titleBar.loadDynamicsTitleBar(bind.root, null)
            bind.apply {
                tvTitleName.text = R.string.withdrawal.getString()
                tvTitleRight.text = R.string.withdrawal_record.getString()
                ivBack.clickNoRepeat {
                    findNavController().navigateUp()
                }
                tvTitleRight.clickNoRepeat {
                    navigate(
                        WithdrawFragmentDirections.actionWithdrawFragmentToFundDetailsFragment()
                            .apply {
                                arguments.putString("type", "tx_record")
                            })
                }
            }
        }
        val page = WithdrawTabType.entries.toTypedArray()
        mBinding.viewPager.adapter =
            PagerAdapter(childFragmentManager, lifecycle, page.map { it.page })
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager, false) { tab, position ->
            tab.text = page[position].page.title
        }.attach()
        mBinding.tabLayout.post {
            mBinding.tabLayout.getTabAt(1)?.view?.setPadding(21.dp2px, 0, 21.dp2px, 4.dp2px)
        }
        mBinding.viewPager.setupHorizontalScrollDegree()
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

}