package com.walisport.module.me.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.data.constants.FragmentResultEnum
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.biz.CommonBiz
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.gyf.immersionbar.ImmersionBar
import com.walisport.module.me.databinding.FragmentMeBinding
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlin.reflect.KClass

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.me.R
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.walisport.module.hall.ui.view.MeScrollableTabIndicatorHelper

/**
 * 我的界面
 *
 * VIP原有12个等级
 *
 * 铜 白银 黄金 铂金 钻石 绿钻 红钻 黑钻 星钻 陨钻 星辰 宇宙
 *
 * 后台设定xx-xx位白银，xx-xx位黄金
 */

class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>() {

    override val vbClass: KClass<FragmentMeBinding> = FragmentMeBinding::class
    override val vmClass: KClass<MeViewModel> = MeViewModel::class

    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()
    var tabSelectPosition = 0 //保留选中状态,数据更新时不会切换tab
    private var tabIndicatorHelper: MeScrollableTabIndicatorHelper? = null
    private var topTabIndicatorHelper: MeScrollableTabIndicatorHelper? = null
    override fun initView(savedInstanceState: Bundle?) {
        initVIPInfo()
        initFeatures()
        initBarHeight()
        loadFragment()
        tabIndicatorHelper = MeScrollableTabIndicatorHelper(mBinding.tabLayout, mBinding.customIndicator)
        topTabIndicatorHelper = MeScrollableTabIndicatorHelper(mBinding.topTabLayout, mBinding.topCustomIndicator)
    }

    private fun loadFragment() {
        val list = listOf(
            PagerBean(arch.cayenne.lib.common.R.string.drawer_recently_played.getString()) { RecentlyTabFragment() },
            PagerBean(
                arch.cayenne.lib.common.R.string.drawer_game_collections.getString()
            ) { GameCollectionsTabFragment() },
            PagerBean(
                arch.cayenne.lib.common.R.string.drawer_match_collections.getString()
            ) { MatchCollectionsFragment() },
        )
        with(mBinding) {

            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            launch {
                vpPage.offscreenPageLimit = 3
            }
            vpPage.isUserInputEnabled = false
            TabLayoutMediator(tabLayout, vpPage, false) { tab, position ->
                tab.text = list[position].title
                tab.setCustomView(R.layout.layout_custom_tab)
                tab.customView?.findViewById<SkinnableTextView>(R.id.tabText)?.apply {
                    text = list[position].title
                    setTextColor(
                        SkinnableResourceManager.getColor(
                            context,
                            if (position == tabSelectPosition) R.color.tab_selected_text_color else R.color.video_tab_text_color
                        )
                    )
                }
                tab.view.setOnClickListener { /* Handle click */ }
            }.attach()
            tabLayout.clearOnTabSelectedListeners()
            tabLayout.post {
                mBinding.vpPage.setCurrentItem(0, false)
            }
            tabLayout.removeAllTips()
        }
        initTopTab(list)
    }


    fun initTopTab(tabs:List<PagerBean>){
        tabs.forEachIndexed { index, pagerBean ->
            mBinding.topTabLayout.addTab(
                mBinding.topTabLayout.newTab().apply {
                    text = pagerBean.title
                    setCustomView(R.layout.layout_custom_tab)
                    customView?.findViewById<SkinnableTextView>(R.id.tabText)?.apply {
                        text = pagerBean.title
                        setTextColor(
                            SkinnableResourceManager.getColor(
                                context,
                                if (index == 0) R.color.tab_selected_text_color else R.color.video_tab_text_color
                            )
                        )
                    }
                    view.setOnClickListener { /* Handle click */ }
                    customView?.findViewById<SkinnableTextView>(R.id.count)?.apply {
                        visibility = View.VISIBLE
                    }
                }
            )
        }
        mBinding.topTabLayout.removeAllTips()
    }


    fun initBarHeight() {
        mBinding.root.post {
            mBinding.ctTopCc.setPadding(
                mBinding.ctTopCc.paddingLeft,
                getStatusBarHeight(mBinding.root),
                mBinding.ctTopCc.paddingRight,
                mBinding.ctTopCc.paddingBottom
            )
            mBinding.ctTopBar.setPadding(
                mBinding.ctTopBar.paddingLeft,
                getStatusBarHeight(mBinding.root),
                mBinding.ctTopBar.paddingRight,
                mBinding.ctTopBar.paddingBottom
            )
        }
    }

    private fun initVIPInfo() {
        childFragmentManager.findFragmentByTag(MeVIPInfoFragment.TAG) as? MeVIPInfoFragment
            ?: MeVIPInfoFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVipInfo.id, it, MeVIPInfoFragment.TAG).commitNow()
            }
    }

    private fun initFeatures() {
        childFragmentManager.findFragmentByTag(MeFeaturesFragment.TAG) as? MeFeaturesFragment
            ?: MeFeaturesFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentFeatures.id, it, MeFeaturesFragment.TAG).commitNow()
            }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

        mViewModel.onViewpagerHeight.observe(viewLifecycleOwner){
            LogUtils.e("MeFragment-------->onViewpagerHeight------it->${it}}")
            mBinding.vpPage.layoutParams.height = it
            mBinding.vpPage.requestLayout()
        }


        with(mBinding) {
            ivDrawer.addScaleOnTouchAnimation()
            ivDrawer.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResult(
                    REQUEST_KEY_DRAWER,
                    bundleOf(KEY_ACTION to ACTION_OPEN)
                )
            }

            ivCustomer.addScaleOnTouchAnimation()
            ivCustomer.clickNoRepeat {
                CommonBiz.jump2CustomerService(this@MeFragment)
            }

            ivSetting.addScaleOnTouchAnimation()
            ivSetting.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }
            ctUserInfo.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_personal_info_fragment.deeplink())
            }
        }

        mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    if (isTabClick) {
                        val selectedTopTabIndex = mBinding.topTabLayout.selectedTabPosition
                        tabSelectPosition = tab.position
                         LogUtils.e("MeFragment-------->onTabSelected------tabSelectPosition->${tabSelectPosition}}")
                        if (selectedTopTabIndex != tab.position) {
                            mBinding.topTabLayout.getTabAt(tab.position)?.let { topTab ->
                                topTab.select()
                            }
                        }
                        tabIndicatorHelper?.smartAnimateToCurrent()
                        topTabIndicatorHelper?.smartAnimateToCurrent()
                        val vp = mBinding.vpPage
                        vp.startFadeAnim {
                            vp.setCurrentItem(tab.position, false)
                            it.invoke()
                        }
                    }
                }
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.tab_selected_text_color
                        )
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.video_tab_text_color
                        )
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })


        mBinding.topTabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    val selectedTopTabIndex = mBinding.tabLayout.selectedTabPosition
                    tabSelectPosition = tab.position
                    if (selectedTopTabIndex != tab.position) {
                        mBinding.tabLayout.getTabAt(tab.position)?.let { topTab ->
                            topTab.select()
                        }
                    }
                    val vp = mBinding.vpPage
                    vp.startFadeAnim {
                        vp.setCurrentItem(tab.position, false)
                        it.invoke()
                    }
                    tabIndicatorHelper?.smartAnimateToCurrent()
                    topTabIndicatorHelper?.smartAnimateToCurrent()
                    tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                        textView.setTextColor(
                            SkinnableResourceManager.getColor(
                                textView.context,
                                R.color.tab_selected_text_color
                            )
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.video_tab_text_color
                        )
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
        mBinding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // scrollY 就是当前的垂直滑动距离
            if (scrollY > (mBinding.clTop.height- mBinding.ctTopBar.height)) {
                if (mBinding.topSkinTab.visibility== View.INVISIBLE){
                    topTabIndicatorHelper?.smartAnimateToCurrent(0)
                }
                mBinding.topSkinTab.visibility = View.VISIBLE
            } else if (scrollY < mBinding.clTop.height- mBinding.ctTopBar.height) {
                mBinding.topSkinTab.visibility = View.INVISIBLE
            }
            val contentHeight = mBinding.ctUserInfo.height
            val percent = (scrollY.toFloat() / contentHeight).coerceIn(0f, 1f)
            mBinding.ctTopBar.alpha = percent
        }
    }

    override suspend fun createObserver() {
        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.ivUnreadDot.visibility =
                    if (flag) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        unreadMessageViewModel.createObserver()

        with(mViewModel) {
            count.observe(viewLifecycleOwner) { count ->
                count.forEachIndexed { index, item ->
                    mBinding.tabLayout.getTabAt(index)?.let {
                        changeTabCount(it, item.const.toLong())

                    }
                    mBinding.topTabLayout.getTabAt(index)?.let {
                        changeTopTabCount(it, item.const.toLong())

                    }
                }
                mBinding.tabLayout.getTabAt(tabSelectPosition)?.select()
                mBinding.topTabLayout.getTabAt(tabSelectPosition)?.select()
                mBinding.tabLayout.post{
                    tabIndicatorHelper?.smartAnimateToCurrent(0)
                }
                mBinding.topTabLayout.post{
                    topTabIndicatorHelper?.smartAnimateToCurrent(0)
                }
            }

            onVipListener.observe(viewLifecycleOwner) {
                if (it != null) {
                    mBinding.tvNickname.text = it.nickname
                    Glide.with(this@MeFragment).load(it.avatar.url).apply(
                        RequestOptions.bitmapTransform(CircleCrop())
                    ).into(mBinding.ivAvatar)
                    val day = calculateBetweenDay(it.registerTime)
                    mBinding.tvJoinTime.text = day
                }
            }
        }
        mViewModel.createObserver()

    }

    private fun changeTabCount(tab: TabLayout.Tab, count: Long) {
        tab.customView?.findViewById<SkinnableTextView>(R.id.count)?.apply {
            if (count > 0) {
                visibility = View.VISIBLE
                text = if (count > 999) {
                    "999+"
                } else {
                    "$count"
                }
            } else {
                visibility = View.GONE
            }
            post{
                tabIndicatorHelper?.setup()
            }
        }
    }
    private fun changeTopTabCount(tab: TabLayout.Tab, count: Long) {
        tab.customView?.findViewById<SkinnableTextView>(R.id.count)?.apply {
            if (count > 0) {
                visibility = View.VISIBLE
                text = if (count > 999) {
                    "999+"
                } else {
                    "$count"
                }
            } else {
                visibility = View.GONE
            }
        }

    }
    private fun calculateBetweenDay(reg: Long): String {
        if (reg == 0L) {
            return getString(R.string.reg_day, 0)
        }
        val diff = System.currentTimeMillis() - reg
        val day = diff / (24 * 60 * 60 * 1000)
        return getString(R.string.reg_day, day)
    }

    private fun getStatusBarHeight(view: View): Int {
        val windowInsetsCompat = ViewCompat.getRootWindowInsets(view)
        val topInset = windowInsetsCompat?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        val ret = if (topInset == 0) ImmersionBar.getStatusBarHeight(view.context) else topInset
        return ret
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llContent)
        super.onStart()
    }

}