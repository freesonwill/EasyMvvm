package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.adapter.PagerAdapter
import arch.cayenne.lib.base.data.PagerBean
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.module.handicap.databinding.FragmentHandicapBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapViewModel
import kotlin.reflect.KClass
import arch.cayenne.module.handicap.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * 盘口教程页面
 */

class HandicapFragment : BaseFragment<HandicapViewModel, FragmentHandicapBinding>() {

    override val vbClass: KClass<FragmentHandicapBinding> = FragmentHandicapBinding::class
    override val vmClass: KClass<HandicapViewModel> = HandicapViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(
            R.string.handicap_lesson.getString(),
            { findNavController().navigateUp() },
            { navigate(HandicapFragmentDirections.actionHandicapFragmentToSimulateFragment()) },
            R.string.simulate_bet.getString()
        )
        with(mBinding) {
            val array = resources.getStringArray(R.array.handicap_tabs)
            val list = listOf(
                PagerBean(array[0]) { HandicapLetBallFragment() },
                PagerBean(array[1]) { HandicapBigSmallFragment() },
                PagerBean(array[2]) { HandicapCornerFragment() }
            )
            viewpager.adapter = null
            viewpager.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            TabLayoutMediator(tabLayout, viewpager) { tab, position ->
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
                    params.width = 74.dp2px
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

    }

    override fun createObserver() {

    }
}