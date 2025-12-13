package com.walisport.module.topup.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.databinding.ViewBarEditBinding
import arch.cayenne.lib.common.ui.view.CustomTabIndicator
import arch.cayenne.lib.common.ui.view.CustomTabLayoutMediator
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.touchBackPressed
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
    private var tabMediator: CustomTabLayoutMediator? = null
    private var customIndicator: CustomTabIndicator? = null

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
                    navigate(
                        arch.cayenne.lib.res.R.string.nav_module_web_fragment
                            .deeplink("url" to BizUrl.FUND_DETAIL.url)
                    )
                }
            }
            val list = listOf(
                PagerBean(R.string.crypto_coin.getString()) { TopUpCryptoFragment() },
                PagerBean(R.string.fiat_coin.getString()) { TopUpFiatFragment() },
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
            tabIndicatorWidth = 0.45f,select = 1
        )
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