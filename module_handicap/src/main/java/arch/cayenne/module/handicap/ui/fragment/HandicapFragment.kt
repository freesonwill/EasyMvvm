package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.module.handicap.R
import arch.cayenne.module.handicap.databinding.FragmentHandicapBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

/**
 * 盘口教程页面
 */

class HandicapFragment : BaseFragment<HandicapViewModel, FragmentHandicapBinding>() {

    override val vbClass: KClass<FragmentHandicapBinding> = FragmentHandicapBinding::class
    override val vmClass: KClass<HandicapViewModel> = HandicapViewModel::class
    private val args: HandicapFragmentArgs by navArgs()
    private var skipAnyAnim = true

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(
                R.string.handicap_lesson.getString(),
                { findNavController().navigateUp() },
                {
                    navigate(
                        HandicapFragmentDirections.actionHandicapFragmentToSimulateFragment(
                            homeId = args.homeId
                        )
                    )
                },
                R.string.simulate_bet.getString()
            )
            val array = resources.getStringArray(R.array.handicap_tabs)
            val list = listOf(
                PagerBean(array[0]) { HandicapLetBallFragment() },
                PagerBean(array[1]) { HandicapBigSmallFragment() },
                PagerBean(array[2]) { HandicapCornerFragment() },
            )
            viewpager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
                tab.text = list[position].title
            }.attach()
            setViewPagerAnim()
            tabLayout.removeAllTips()
            reflexPadding(tabLayout)
        }
    }

    private fun setViewPagerAnim() {
        mBinding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        skipAnyAnim = false
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {
                        skipAnyAnim = true
                    }
                }
            }
        })
        mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    if (skipAnyAnim) {
                        mBinding.viewpager.setCurrentItem(tab.position, false)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {

            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {

            }
        })
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                val marginStart: Int = 9.dp2px
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.height = 32.dp2px
                    params.width = 96.dp2px
                    params.topMargin = 7.dp2px
                    params.bottomMargin = 7.dp2px
                    params.leftMargin = marginStart
                    params.rightMargin = marginStart
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initListener() {
        mBinding.root.touchBackPressed()
    }

    override suspend fun createObserver() {

    }
}