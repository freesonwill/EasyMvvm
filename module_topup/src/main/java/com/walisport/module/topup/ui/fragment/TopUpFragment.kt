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
import com.walisport.module.topup.data.entity.TopUpTabType
import com.walisport.module.topup.databinding.FragmentTopupBinding
import com.walisport.module.topup.ui.viewmodel.TopUpViewModel
import kotlin.reflect.KClass

/**
 * 充值页面
 */

class TopUpFragment : BaseFragment<TopUpViewModel, FragmentTopupBinding>() {

    override val vbClass: KClass<FragmentTopupBinding> = FragmentTopupBinding::class
    override val vmClass: KClass<TopUpViewModel> = TopUpViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val bind = ViewBarEditBinding.inflate(LayoutInflater.from(context), root, false)
            titleBar.loadDynamicsTitleBar(bind.root, null)
            bind.apply {
                tvTitleName.text = R.string.recharge.getString()
                tvTitleRight.text = R.string.recharge_record.getString()
                ivBack.clickNoRepeat {
                    findNavController().navigateUp()
                }
                tvTitleRight.clickNoRepeat {
                    navigate(TopUpFragmentDirections.actionTopUpFragmentToFundDetailsFragment().apply {
                        arguments.putString("type", "cz_record")
                    })
                }
            }
        }
        val page = TopUpTabType.entries.toTypedArray()
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

    override fun initData() {
        super.initData()
        mViewModel.getCurrencyList()
    }
}