package arch.cayenne.module.betslip.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.databinding.FragmentLiveBetSlipLayoutBinding
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * 直播间注单
 * */
class BetSlipFragment :
    BaseFragment<BetSlipPageViewModel, FragmentLiveBetSlipLayoutBinding>() {

    override val vbClass: KClass<FragmentLiveBetSlipLayoutBinding> =
        FragmentLiveBetSlipLayoutBinding::class
    override val vmClass: KClass<BetSlipPageViewModel> = BetSlipPageViewModel::class
    private val betSlipFilterViewModel: BetSlipFilterViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        initMenu()
        mViewModel.setBetSlipDetail()
    }

    /**
     * 监听直播间的matchId 和sportId变化，及时刷新注单数据
     * */
    fun refreshBetSlip(matchId: Long, sportId: Int) {
        betSlipFilterViewModel.setIds(matchId, sportId)
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mBinding.viewPager.offscreenPageLimit = 5
    }

    private fun initMenu() {
        with(mBinding) {
            val array = SkinnableResourceManager.getStringArray(requireContext(),arch.cayenne.lib.res.R.array.bet_slip_menus)
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
            viewPager.setupHorizontalScrollDegree()

            tabLayout.clearOnTabSelectedListeners()
            tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
                override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
//                    mBinding.viewPager.doSmartAnim(targetPosition = tab.position)
                     if(isTabClick) {
                         mBinding.viewPager.startFadeAnim {
                             mBinding.viewPager.setCurrentItem(tab.position, false)
                            it.invoke()
                        }
                    } else {
                         mBinding.viewPager.doSmartAnim(tab.position)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                    (tab.customView as? TextView)?.setTypeface(null, Typeface.NORMAL)
                }

                override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                }
            })

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
                    //设置tab左右间距为8dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                      params.leftMargin = marginStart
                    params.height = 32.dp2px
                    when (i) {
                        0, 1, 2 -> {
                            params.width = 74.dp2px
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

    override suspend fun createObserver() {
    }


}