package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentHomeBetslipBinding
import arch.cayenne.module.betslip.ui.viewmodel.HomeBetSlipViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

class HomeBetSlipFragment: BaseFragment<HomeBetSlipViewModel, FragmentHomeBetslipBinding>() {
    override val vbClass: KClass<FragmentHomeBetslipBinding> = FragmentHomeBetslipBinding::class
    override val vmClass: KClass<HomeBetSlipViewModel> = HomeBetSlipViewModel::class
    private val viewPagerAnimHelper by lazy {
        ViewPagerAnimHelper()
    }

    override fun initView(savedInstanceState: Bundle?) {
        val array = resources.getStringArray(arch.cayenne.module.betslip.R.array.bet_slip_menus)
        val list = listOf(
            PagerBean(array[0]) { BetSlipUnsettledFragment() },
            PagerBean(array[1]) { BetSlipConfirmFragment() },
            PagerBean(array[2]) { BetSlipSettledFragment() },
            PagerBean(array[3]) { BetSlipReserveFragment() },
            PagerBean(array[4]) { BetSlipInvalidFragment() },
        )
        setPage(list)
    }

    override fun initListener() {
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.tvDateFilter.setOnClickListener {
            showDateFilter()
        }
        mBinding.tvSportFilter.setOnClickListener {
            showSportFilter()
        }
    }

    override fun createObserver() {
        mViewModel.onDateFilter.observe(viewLifecycleOwner) {
            mBinding.tvDateFilter.text = it.title
        }
        mViewModel.onSportFilter.observe(viewLifecycleOwner) {
            mBinding.tvSportFilter.text = it.sportName
        }
    }

    private fun setPage(pager: List<PagerBean>) {
        mBinding.viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, pager)
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

    private fun showDateFilter() {
        mViewModel.onDateFilter.value?.let {
            childFragmentManager.setFragmentResultListener(
                Config.KEY_RESULT,
                viewLifecycleOwner
            ) { _, bundle ->
                childFragmentManager.clearFragmentResultListener(Config.KEY_RESULT)
                if (bundle.containsKey(Config.VALUE_SELECTED_DATE)) {
                    bundle.getString(Config.VALUE_SELECTED_DATE)?.let { result ->
                        val date = BetSlipDateFilterEnum.valueOf(result)
                        if (date == BetSlipDateFilterEnum.CUSTOM) {
                            val time = bundle.getLong(Config.VALUE_SELECTED_MILLISECOND)
                            mViewModel.customTime = time
                        } else {
                            mViewModel.setDateFilter(date)
                        }
                    }
                }
            }
            val time = if (it.date == BetSlipDateFilterEnum.CUSTOM && mViewModel.customTime != null) {
                mViewModel.customTime
            } else {
                null
            }
            DatePickerFragment.newInstance(it.date, time).show(childFragmentManager)
        }
    }

    private fun showSportFilter() {
        mViewModel.onSportFilter.value?.let {

        }
    }
}