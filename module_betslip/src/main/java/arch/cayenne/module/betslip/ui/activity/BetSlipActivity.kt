package arch.cayenne.module.betslip.ui.activity

import android.os.Bundle
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ActivityBetslipBinding
import arch.cayenne.module.betslip.ui.fragment.BetSlipConfirmFragment
import arch.cayenne.module.betslip.ui.fragment.BetSlipInvalidFragment
import arch.cayenne.module.betslip.ui.fragment.BetSlipReserveFragment
import arch.cayenne.module.betslip.ui.fragment.BetSlipSettledFragment
import arch.cayenne.module.betslip.ui.fragment.BetSlipUnsettledFragment
import arch.cayenne.module.betslip.ui.fragment.DatePickerFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class BetSlipActivity: BaseActivity<EmptyViewModel, ActivityBetslipBinding>() {
    override val vbClass: KClass<ActivityBetslipBinding> = ActivityBetslipBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private val viewPagerAnimHelper by lazy {
        ViewPagerAnimHelper()
    }

    override fun initView(savedInstanceState: Bundle?) {
        // TODO 待詳情解耦後補上
//        val array = resources.getStringArray(R.array.bet_slip_menus)
//        val list = listOf(
//            PagerBean(array[0]) { BetSlipUnsettledFragment() },
//            PagerBean(array[1]) { BetSlipConfirmFragment() },
//            PagerBean(array[2]) { BetSlipSettledFragment() },
//            PagerBean(array[3]) { BetSlipReserveFragment() },
//            PagerBean(array[4]) { BetSlipInvalidFragment() },
//        )
        setPage(emptyList())
    }

    override fun initListener() {
        mBinding.ivBack.setOnClickListener {
            finish()
        }
        mBinding.tvTitle.setOnClickListener {
            // TODO 測試用
            DatePickerFragment.newInstance().show(supportFragmentManager)
        }
    }

    override fun createObserver() {

    }

    private fun setPage(pager: List<PagerBean>) {
        mBinding.viewPager.adapter = PagerAdapter(supportFragmentManager, lifecycle, pager)
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = pager[position].title
        }.attach()

        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPagerAnimHelper.doDirectViewPagerAnim(
                    targetPosition = tab?.position ?: 0,
                    viewPager = mBinding.viewPager,
                    fakeViewPager = mBinding.ivFaker
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        mBinding.tabLayout.removeAllTips()
    }
}