package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.removeAllTips
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentLiveBetSlipLayoutBinding
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipPageViewModel


/**
 * 注单
 * */
class BetSlipFragment :
    BaseFragment<BetSlipPageViewModel, FragmentLiveBetSlipLayoutBinding>() {

    override val vbClass: KClass<FragmentLiveBetSlipLayoutBinding> =
        FragmentLiveBetSlipLayoutBinding::class
    override val vmClass: KClass<BetSlipPageViewModel> = BetSlipPageViewModel::class
    private val betSlipFilterViewModel: BetSlipFilterViewModel by viewModels()

    companion object {
        val matchKey = "match_id"
        val sportKey = "sport_id"
    }

    override fun initView(savedInstanceState: Bundle?) {
        initMenu()
        mViewModel.setBetSlipDetail()
    }

    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(matchKey,-1) ?: -1
        val sportId = arguments?.getInt(sportKey,-1) ?: -1
        betSlipFilterViewModel.setIds(matchId, sportId)
    }

    private fun initMenu() {
        with(mBinding) {
            val array = resources.getStringArray(R.array.bet_slip_menus)
            val list = listOf(
                PagerBean(array[0]) {
                    BetSlipUnsettledFragment()
                },
                PagerBean(array[1]) {
                    BetSlipConfirmFragment()
                },
                PagerBean(array[2]) {
                    BetSlipSettledFragment()
                },
                PagerBean(array[3]) {
                    BetSlipReserveFragment()
                },
                PagerBean(array[4]) {
                    BetSlipInvalidFragment()
                },
            )
            viewPager.adapter = null
            viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = list[position].title
            }.attach()
            tabLayout.removeAllTips()

            reflexPadding(tabLayout)
        }
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                //拿到tabLayout的mTabStrip属性
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                val marginStart: Int = 8f.dp2px
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.leftMargin = marginStart
                    params.height = 32.dp2px
                    when (i) {
                        0, 1, 2 -> {
                            params.width = 74.dp2px
                            params.rightMargin = marginStart
                        }

                        else -> {
                            params.width = 60.dp2px
                        }
                    }
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}