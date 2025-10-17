package com.walisport.module.me.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2sp
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.me.R
import com.walisport.module.me.databinding.FragmentMeBinding
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass


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


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            tvNickname.text = "中文sdf323"
            val day = 137
            tvJoinTime.text = "已加入${day}天"
        }

        initVIPInfo()
        initFeatures()
        loadFragment()
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
                delay(500)
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
                    textSize = 15f.px2sp
                    typeface =
                        if (position == tabSelectPosition) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

                }
                tab.view.setOnClickListener { /* Handle click */ }
            }.attach()
            tabLayout.clearOnTabSelectedListeners()
            tabLayout.post {
                CustomTabIndicatorUtils.animateIndicatorToPosition(
                    mBinding.customIndicator,
                    1,
                    false
                )
                mBinding.vpPage.setCurrentItem(0, false)
            }
            tabLayout.removeAllTips()
        }
    }

    override fun initListener() {
        with(mBinding) {

            ivDrawer.addScaleOnTouchAnimation()
            ivDrawer.clickNoRepeat { }

            ivCustomer.addScaleOnTouchAnimation()
            ivCustomer.clickNoRepeat { }

            ivSetting.addScaleOnTouchAnimation()
            ivSetting.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }

        }

    }

    override suspend fun createObserver() {
        mViewModel.createObserver()
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llContent)
        super.onStart()
    }


}