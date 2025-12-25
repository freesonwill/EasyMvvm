package com.walisport.module.me.ui.fragment

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
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.data.constants.FragmentResultEnum
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
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
import kotlinx.coroutines.delay
import com.walisport.module.me.R
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import com.google.android.material.tabs.TabLayout

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



    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            tvNickname.text = "中文sdf323"
            val day = 137
            tvJoinTime.text = "已加入${day}天"
        }

        initVIPInfo()
        initFeatures()

        initBarHeight()
        loadFragment()
    }
    private fun loadFragment() {
        val tabSelectPosition = 0
        with(mBinding) {
            val list = listOf(
                PagerBean(arch.cayenne.lib.common.R.string.drawer_recently_played.getString()) { RecentlyFragment() },
                PagerBean(
                    arch.cayenne.lib.common.R.string.drawer_game_collections.getString()
                ) { GameCollectionsFragment() },
                PagerBean(
                    arch.cayenne.lib.common.R.string.drawer_match_collections.getString()
                ) { MatchCollectionsFragment() },
            )

            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
            launch {
                vpPage.offscreenPageLimit = list.size
            }

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

                tab.customView?.findViewById<SkinnableTextView>(R.id.count)?.apply {
                    visibility = View.VISIBLE
                    text = if (position == 0) {
                        "999+"
                    } else {
                        "1"
                    }
                }
            }.attach()
            tabLayout.clearOnTabSelectedListeners()
            tabLayout.post {
                CustomTabIndicatorUtils.animateIndicatorToPosition(
                    mBinding.customIndicator,
                    0,
                    false
                )
                mBinding.vpPage.setCurrentItem(0, false)
            }
            tabLayout.removeAllTips()
        }
    }

    fun initBarHeight(){
        mBinding.root.post{
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



    override fun initListener() {
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
                        CustomTabIndicatorUtils.animateIndicatorToPosition(
                            mBinding.customIndicator,
                            tab.position
                        )
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
        mBinding.vpPage.setupViewPagerScroll(mBinding.tabLayout, mBinding.customIndicator, 0.24f)
    }

    override suspend fun createObserver() {
        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.ivUnreadDot.visibility = if (flag) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        unreadMessageViewModel.createObserver()

        mViewModel.createObserver()

        with(mViewModel) {
            recentlyCount.observe(viewLifecycleOwner) { count ->
                mBinding.tabLayout.getTabAt(0)?.let {
                    changeTabCount(it, count)
                }
            }
            gameCount.observe(viewLifecycleOwner) { count ->
                mBinding.tabLayout.getTabAt(1)?.let {
                    changeTabCount(it, count)
                }
            }

            matchCount.observe(viewLifecycleOwner) { count ->
                mBinding.tabLayout.getTabAt(2)?.let {
                    changeTabCount(it, count)
                }
            }
        }
        mViewModel.createObserver()

        launch {
            mViewModel.bottomIndexFlow.collect {
                mBinding.vpPage.post {//延迟一帧，viewPager可能正在刷新adapter
                    mBinding.vpPage.setCurrentItem(it,true)
                    //Todo bug1: mBinding.vpPage.setCurrentItem(it,false)不会触发tabLayout的选中变化
                    //Todo bug2: tabLayout.getTabAt(it)?.select()  不会触发indicator的变化
                }
            }
        }




    }
    private fun changeTabCount(tab: TabLayout.Tab, count: Long) {
        tab.customView?.findViewById<SkinnableTextView>(R.id.count)?.apply {
            visibility = View.VISIBLE
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
    private fun getStatusBarHeight(view:View):Int{
        val windowInsetsCompat = ViewCompat.getRootWindowInsets(view)
        val topInset = windowInsetsCompat?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        val ret = if(topInset == 0) ImmersionBar.getStatusBarHeight(view.context) else topInset
        return ret
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llContent)
        super.onStart()
    }

    override suspend fun onArgumentsChanged(oldArgs: Bundle?, newArgs: Bundle?) {
        arguments?.getInt(FragmentResultEnum.KEY_ME_BOTTOM.name)?.let {
            mViewModel.bottomIndexFlow.tryEmit(it)
        }
    }
}