package arch.cayenne.module.home.ui.fragment

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.VIPDataExt
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.helper.VIPResourceHelper
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.BiDirectionalDate
import arch.cayenne.module.home.data.BiDirectionalDateType
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.databinding.FragmentSuperCompetitionBinding
import arch.cayenne.module.home.databinding.ItemDateTabBinding
import arch.cayenne.module.home.ui.adapter.SportBannerAdapter
import arch.cayenne.module.home.ui.viewmodel.SuperCompetitionViewModel
import arch.cayenne.module.home.utils.scrollToPositionWithoutAnim
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

class SuperCompetitionFragment :
    BaseFragment<SuperCompetitionViewModel, FragmentSuperCompetitionBinding>(),
    ISubFragmentLifecycle {

    override val vbClass: KClass<FragmentSuperCompetitionBinding> =
        FragmentSuperCompetitionBinding::class
    override val vmClass: KClass<SuperCompetitionViewModel> = SuperCompetitionViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initSportBanner()
        initVIPInfo()
        initMatchListFragment()
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        // 監聽 VIP 等級變化
        mViewModel.vipLevel.observe(viewLifecycleOwner) { level ->
            updateVIPInfo(
                vipLevel = level.toInt(),
                percent = "57.91%",
                levelUpInfo = "升级还需¥59w"
            )
        }
        mViewModel.dateList.observe(viewLifecycleOwner) {
            // 日期 Tab 設定
            updateDateTabs(mBinding.tlDateList, it!!)
            addDateTabListener()
            lifecycleScope.launch {
                mViewModel.selectedDate(it.first().timestamp)
            }
        }

        mViewModel.tournaments.observeEvent(viewLifecycleOwner, this) { comboList ->
            startObservePageMatchListChange()
        }

    }

    override fun initData() {
        super.initData()
        mViewModel.setCurrentSport(arguments?.getInt(ARG_SPORT_ID) ?: SportType.SOCCER.id)
    }

    // init Sport Banner 輪播區塊
    @SuppressLint("ClickableViewAccessibility")
    private fun initSportBanner() {
        val mockBannerList = arrayListOf(
            arch.cayenne.module.home.data.SportBannerData(R.drawable.banner_ad1),
            arch.cayenne.module.home.data.SportBannerData(R.drawable.banner_ad1),
            arch.cayenne.module.home.data.SportBannerData(R.drawable.banner_ad1),
            arch.cayenne.module.home.data.SportBannerData(R.drawable.banner_ad1),
            arch.cayenne.module.home.data.SportBannerData(R.drawable.banner_ad1),
        )
        val bannerAdapter = SportBannerAdapter()

        with(mBinding.includeSportBanner) {
            vpSportBanner.adapter = bannerAdapter
            bannerAdapter.submitList(mockBannerList)
            vpSportBanner.isUserInputEnabled = true
            vpSportBanner.getChildAt(0).setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            vpSportBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    pbSportBanner.resetTriggerJob()
                }
            })
            pbSportBanner.setTriggerListener {
                vpSportBanner.currentItem =
                    (vpSportBanner.currentItem + 1) % bannerAdapter.itemCount
            }
        }
    }

    // init VIP 信息區塊
    private fun initVIPInfo() {
        // VIP 數據設置初始值，避免初始化時沒有數據
        val currentLevel = VIPDataExt.getVIPLevel(75L)
        updateVIPInfo(
            vipLevel = currentLevel.toInt(),
            percent = "57.91%",
            levelUpInfo = "升级还需¥59w"
        )
    }

    // 更新 VIP 信息顯示
    private fun updateVIPInfo(vipLevel: Int, percent: String, levelUpInfo: String) {
        // 使用 VIPResourceHelper 轉換等級
        val level = VIPResourceHelper.getVIPLevelFromInt(vipLevel)

        with(mBinding) {
            // 設置背景 - 使用 VIPResourceHelper
            clVipInfo.background = VIPResourceHelper.getForegroundResource(level).getDrawable()

            // 設置圖標 - 使用 VIPResourceHelper
            ivLevel.setImageResource(VIPResourceHelper.getIconResource(level))
            ivLevelName.setImageResource(VIPResourceHelper.getLevelNameResource(level))

            // 設置文字漸變效果 - 使用 VIPResourceHelper
            val bottom = 20.dp2px.toFloat()
            val linearGradient = LinearGradient(
                0f, 0f,
                0f, bottom,
                intArrayOf(
                    VIPResourceHelper.getShaderStartColor().getColor(requireContext()),
                    VIPResourceHelper.getShaderEndColor(level).getColor(requireContext())
                ),
                null,
                Shader.TileMode.CLAMP
            )
            tvLevel.paint.shader = linearGradient
            tvLevel.text = getString(arch.cayenne.lib.common.R.string.vip_level_format, vipLevel)

            // 設置百分比 - 使用 VIPResourceHelper
            tvPercent.text = percent
            ivPercent.setImageResource(VIPResourceHelper.getPercentResource(level))

            // 設置升級信息
            tvLevelUpInfo.text = levelUpInfo
        }
    }


    private fun addDateTabListener() {
        mBinding.tlDateList.addOnTabSelectedListener2(object :
            TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                playFadeAnimTriggerByDateTab {
                    lifecycleScope.launch {
                        mViewModel.selectedDate(
                            mViewModel.dateList.value?.find { it.dateStr == tab.tag }?.timestamp
                                ?: return@launch
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {}
            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {}
        })
    }

    private fun clearDateTabSelection() {
        val tabLayout = mBinding.tlDateList
        tabLayout.setScrollPosition(0, 0f, true)
        val tabStrip = tabLayout.getChildAt(0) as? LinearLayout ?: return
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i)?.isSelected = false
            tabLayout.getTabAt(i)?.customView?.isSelected = false
        }
        tabLayout.selectTab(null)
    }

    private fun resetDateTabs() {
//        mBinding.layoutContainer.tvTabAll.isSelected = true
        clearDateTabSelection()
    }

    private fun createDateTab(date: String?, weekday: String?, type: BiDirectionalDateType): TabLayout.Tab {
        val tab = mBinding.tlDateList.newTab()
        val tabView = ItemDateTabBinding.inflate(LayoutInflater.from(context), null, false).apply {
            if (type == BiDirectionalDateType.Date) {
                tvDate.visibility = View.VISIBLE
                tvDate.text = date
                tvWeekDay.visibility = View.VISIBLE
                tvWeekDay.text = weekday

                tvOther.visibility = View.GONE
            } else {
                tvDate.visibility = View.GONE
                tvWeekDay.visibility = View.GONE

                tvOther.visibility = View.VISIBLE
                tvOther.text = date
            }


        }
        tab.customView = tabView.root
        tab.tag = date
        return tab
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun showSelectedDateTab(indexOfTabs: Int?) {
        with(mBinding) {
            if (indexOfTabs == null || indexOfTabs == -1) {
                resetDateTabs()
                return
            }

            if (indexOfTabs != -1) {
                tlDateList.post {
                    val tabStrip = tlDateList.getChildAt(0) as LinearLayout
                    for (i in 0 until tabStrip.childCount) {
                        tabStrip.getChildAt(i).apply {
                            isSelected = i == indexOfTabs
                        }
                    }
                }

                //scroll smooth
                val tab: TabLayout.Tab? = tlDateList.getTabAt(indexOfTabs)
                if (tab?.view == null) return

                val tabView: View = tab.view
                tabView.post {
                    val screenWidth: Int = tlDateList.width
                    val tabWidth = tabView.width
                    val tabLeft = tabView.left
                    val tabCenter = tabLeft + tabWidth / 2
                    var targetScrollX = tabCenter - screenWidth / 2

                    // 限制滚动范围，防止越界
                    val maxScrollX: Int = tlDateList.getChildAt(0).width - screenWidth
                    targetScrollX =
                        max(0.0, min(targetScrollX.toDouble(), maxScrollX.toDouble()))
                            .toInt()
                    // 使用平滑滚动
                    tlDateList.smoothScrollTo(targetScrollX, 0)
                }
            }
        }
    }

    private fun updateDateTabs(
        tlDateList: TabLayout,
        dateTabs: List<BiDirectionalDate>
    ) {
        tlDateList.apply {
            removeAllTabs()
            dateTabs.forEach { (date, weekday, _, type) ->
                val tab = createDateTab(date, weekday, type)
                addTab(tab)
            }
            setupDateTabLayoutParams(tlDateList, true)
        }
    }

    private fun setupDateTabLayoutParams(tabLayout: TabLayout, clearSelected: Boolean) {
        tabLayout.post {
            val tabStrip = tabLayout.getChildAt(0) as LinearLayout
            for (i in 0 until tabStrip.childCount) {
                tabStrip.getChildAt(i).apply {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.width = 52.dp2px
                    params.height = 37.dp2px
                    params.marginStart = 3.dp2px
                    params.marginEnd = 4.dp2px
                    layoutParams = params
                    setBackgroundResource(R.drawable.selector_date_tab_bg)
                    if (clearSelected) isSelected = false
                }
            }
        }
    }

    private fun playFadeAnimTriggerByDateTab(switchProcess: () -> Unit) {
        mBinding.fragmentMatchList.startFadeAnim { onComplete ->
            switchProcess.invoke()
            onComplete.invoke()
        }
    }

    private fun initMatchListFragment() {
        childFragmentManager.findFragmentByTag(BiDirectionalMatchListPagerFragment.TAG) as? BiDirectionalMatchListPagerFragment
            ?: BiDirectionalMatchListPagerFragment.newInstance(
                SportType.SOCCER.id,
                PlayType.SUPER_COMPETITION.id,
                arguments?.getIntArray(ARG_LEAGUE_ID)?.toList() ?: listOf(0),
                0,
                false
            ).also {
                childFragmentManager.beginTransaction()
                    .replace(
                        mBinding.fragmentMatchList.id,
                        it,
                        BiDirectionalMatchListPagerFragment.TAG
                    )
                    .commitNow()
            }

    }


    /**
     *
     * */
    private fun startObservePageMatchListChange() {
        val fragment =
            childFragmentManager.findFragmentByTag(BiDirectionalMatchListPagerFragment.TAG)
                ?: return
        if (fragment is BiDirectionalMatchListPagerFragment) {
            fragment.startObserveMatchListChange()
        }
    }

    override fun onFragmentSelected() {
        mBinding.tlDateList.scrollToPositionWithoutAnim(mBinding.tlDateList.selectedTabPosition)
    }

    override fun onFragmentUnSelected() {
    }

    override fun reloadCurrentMatchListPagerFragment() {
        val itemId = 0
        val fragment = childFragmentManager.findFragmentByTag("f$itemId") ?: return
        (fragment as? BiDirectionalMatchListPagerFragment)?.reloadAllData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            mBinding.includeSportBanner.pbSportBanner.stopTriggerJob()
        } else {
            mBinding.includeSportBanner.pbSportBanner.resetTriggerJob()
        }
        super.onHiddenChanged(hidden)
    }

    companion object {
        private const val ARG_SPORT_ID = "sport_id"
        private const val ARG_LEAGUE_ID = "arg_league_id"

        fun newInstance(sportId: Int, leagueIdList: List<Int>): SuperCompetitionFragment {
            return SuperCompetitionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SPORT_ID, sportId)
                    putIntArray(ARG_LEAGUE_ID, leagueIdList.toIntArray())
                }
            }
        }
    }
}


