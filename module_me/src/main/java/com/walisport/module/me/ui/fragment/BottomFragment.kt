package com.walisport.module.me.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2sp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.me.R
import com.walisport.module.me.databinding.FragmentBottomBinding
import com.walisport.module.me.databinding.FragmentRecentlyBinding
import com.walisport.module.me.ui.viewmodel.BottomViewModel
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/10/17 16:52
 * @description:
 */
class BottomFragment : BaseFragment<BottomViewModel, FragmentBottomBinding>() {

    override val vbClass: KClass<FragmentBottomBinding> = FragmentBottomBinding::class
    override val vmClass: KClass<BottomViewModel> = BottomViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
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
        mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    if (isTabClick) {
                        CustomTabIndicatorUtils.animateIndicatorToPosition(mBinding.customIndicator,tab.position)
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
                    textView.textSize = 15f.px2sp
                    textView.typeface = Typeface.DEFAULT_BOLD
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
                    textView.textSize = 15f.px2sp
                    textView.typeface = Typeface.DEFAULT
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
        mBinding.vpPage.setupViewPagerScroll(mBinding.tabLayout,mBinding.customIndicator,0.24f)
    }

    override suspend fun createObserver() {
    }

    companion object {
        const val TAG = "BottomFragment"
    }
}