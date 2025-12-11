package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.ViewBarEditBinding
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.common.ui.view.CustomTabLayoutMediator
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentWithdrawBinding
import com.walisport.module.topup.ui.viewmodel.WithdrawViewModel
import kotlin.reflect.KClass

/**
 * 提现页面
 */

class WithdrawFragment : BaseFragment<WithdrawViewModel, FragmentWithdrawBinding>() {

    override val vbClass: KClass<FragmentWithdrawBinding> = FragmentWithdrawBinding::class
    override val vmClass: KClass<WithdrawViewModel> = WithdrawViewModel::class
    private var tabMediator: CustomTabLayoutMediator? = null
    private var customIndicator: CustomTabIndicator? = null

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
            val list = listOf(
                PagerBean(R.string.crypto_coin.getString()) { WithdrawCryptoFragment() },
                PagerBean(R.string.fiat_coin.getString()) { WithdrawFiatFragment() },
            )
            viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            tabMediator?.detach()
            tabMediator = CustomTabLayoutMediator(
                tabLayout = mBinding.tabLayout,
                viewPager = mBinding.viewPager
            ) { tab, pos ->
                tab.text = list[pos].title
            }.also { layoutMediator ->
                layoutMediator.attach()
            }
        }
        mBinding.tabLayout.post {
            mBinding.tabLayout.getTabAt(1)?.view?.setPadding(21.dp2px, 0, 21.dp2px, 4.dp2px)
        }
        customIndicator = mBinding.homeIndicator
        mBinding.viewPager.setupViewPagerScroll(
            mBinding.tabLayout,
            customIndicator!!,
            tabIndicatorWidth = 0.45f, select = 1
        )
        mBinding.viewPager.setupHorizontalScrollDegree()
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

}