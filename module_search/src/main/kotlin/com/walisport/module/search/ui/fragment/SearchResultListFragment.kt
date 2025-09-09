package com.walisport.module.search.ui.fragment

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.animateIndicatorToPosition
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchResultListBinding
import com.walisport.module.search.ui.adapter.SearchResultPagerAdapter
import com.walisport.module.search.ui.viewmodel.SearchResultListViewModel
import java.util.Locale
import kotlin.reflect.KClass

class SearchResultListFragment :
    SearchBaseFragment<SearchResultListViewModel, FragmentSearchResultListBinding>() {
    override val vmClass: KClass<SearchResultListViewModel>
        get() = SearchResultListViewModel::class
    override val contentVbClass: KClass<FragmentSearchResultListBinding>
        get() = FragmentSearchResultListBinding::class

    private val args: SearchResultListFragmentArgs by navArgs()
    private var tabMediator: TabLayoutMediator? = null
    private var skipAnyAnim = true
    private var enableAnim = false
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setViewPager()

        with(contentBinding) {
            viewPager.setupHorizontalScrollDegree()
            // 自定義滑動行為
            viewPager.setupViewPagerScroll(tlSearch, customIndicator, 0.20f,skipAnyAnim = { skipAnyAnim = it }, enableAnimation = {
                if(it == null){
                    return@setupViewPagerScroll enableAnim
                }
                enableAnim = it
                return@setupViewPagerScroll enableAnim            })
            tlSearch.apply {
                clearOnTabSelectedListeners()
                addOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        if (skipAnyAnim) {
                            enableAnim = false
                            // 动画更新指示器位置
                            customIndicator.animateIndicatorToPosition(tab.position,210)
//                            viewPager.setCurrentItem(tab.position, false)
                            viewPager.doSmartAnim(tab.position)

                        }
                        tab.let { updateTabTypeface(it, true) }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        tab?.let { updateTabTypeface(it, false) }
                    }
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        tab?.let { updateTabTypeface(it, true) }
                    }
                })
            }
        }
    }

    override fun initData() {
        super.initData()
        args.keyword?.let {
            updateSearchText(it)
        }
    }

    override fun onLanguageChanged(locale: Locale) {
        super.onLanguageChanged(locale)
        // 更新Tab標籤文字
        updateTabTitles()
    }

    override fun onBackPressed(): Boolean {
        setTempScreenShot()
        parentFragmentManager.setFragmentResult(GO_BACK_TO_MAIN, bundleOf(GO_BACK_TO_MAIN to true))
        return super.onBackPressed()
    }

    override fun onDestroyView() {
        contentBinding.viewPager.adapter = null
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }

    private fun updateTabTypeface(tab: TabLayout.Tab, isBold: Boolean) {
        tab.view.children.find { it is TextView }?.let {
            (it as TextView).apply {
                post {
                    setTypeface(null, if (isBold) Typeface.BOLD else Typeface.NORMAL)
                }
            }
        }
    }

    private fun setViewPager() {
        with(contentBinding) {
            if (viewPager.adapter == null) {
                viewPager.adapter =
                    SearchResultPagerAdapter(
                        this@SearchResultListFragment,
                        args.data
                    ) { id, keyword, type ->
                        val action =
                            SearchResultListFragmentDirections
                                .actionSearchResultListFragmentToSearchResultDirectMatchFragment(
                                    null, keyword, id, type
                                )
                        navigate(action)
                    }
                (viewPager.getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER

                tabMediator = TabLayoutMediator(tlSearch, viewPager) { tab, position ->
                    tab.text = when (position) {
                        0 -> getString(R.string.tab_all)
                        1 -> getString(R.string.tab_tournament)
                        2 -> getString(R.string.tab_team)
                        3 -> getString(R.string.tab_player)
                        else -> ""
                    }
                }.apply {
                    attach()
                }

                // disable tooltip for tabs
                (tlSearch.getChildAt(0) as ViewGroup)
                    .children.forEach { tabView ->
                        tabView.apply {
                            setOnLongClickListener { true }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                tooltipText = null
                            }
                        }
                    }

                switchTab(0, false)
                tlSearch.getTabAt(0)?.let { updateTabTypeface(it, true) }
            }
        }
    }

    /** 更新Tab標籤文字 */
    private fun updateTabTitles() {
        val titles = listOf(
            R.string.tab_all.toTranslatedStr(),
            R.string.tab_tournament.toTranslatedStr(),
            R.string.tab_team.toTranslatedStr(),
            R.string.tab_player.toTranslatedStr()
        )

        with(contentBinding.tlSearch) {
            for (i in 0 until tabCount) {
                getTabAt(i)?.text = titles.getOrNull(i) ?: ""
            }
        }
    }

    /**
     * 切換Tab
     * @param position 要切換到的Tab位置
     * @param isSmooth 是否使用平滑過渡，默認為true
     */
    fun switchTab(position: Int, isSmooth: Boolean = true) {
        contentBinding.viewPager.setCurrentItem(position, isSmooth)
    }
}