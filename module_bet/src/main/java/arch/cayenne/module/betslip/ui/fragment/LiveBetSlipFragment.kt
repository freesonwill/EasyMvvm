package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import android.widget.LinearLayout
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentLiveBetSlipLayoutBinding
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipPageViewModel
import arch.cayenne.module.betslip.ui.viewmodel.LiveBetSlipViewModel
import com.google.gson.Gson


/**
 * 注单
 * */
class LiveBetSlipFragment :
    BaseFragment<BetSlipPageViewModel, FragmentLiveBetSlipLayoutBinding>() {

    override val vbClass: KClass<FragmentLiveBetSlipLayoutBinding> =
        FragmentLiveBetSlipLayoutBinding::class
    override val vmClass: KClass<BetSlipPageViewModel> = BetSlipPageViewModel::class
    private var matchId:Long = 0
    private var sportId:Int = 0

    /**
     * pagerAdapter重置arguments不能使用
     * */
    fun setArguments(matchId:Long,sportId:Int){
        this.matchId = matchId
        this.sportId = sportId
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.matchId = matchId
        mViewModel.sportId = sportId
        initMenu()
    }

    private fun initMenu() {
        with(mBinding) {
            val array = resources.getStringArray(R.array.bet_slip_menus)
            val list = listOf(
                PagerBean(array[0]) { LiveBetSlipUnsettledFragment() },
                PagerBean(array[1]) { LiveBetSlipConfirmFragment() },
                PagerBean(array[2]) { LiveBetSlipSettledFragment() },
                PagerBean(array[3]) { LiveBetSlipReserveFragment() },
                PagerBean(array[4]) { LiveBetSlipInvalidFragment() },
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