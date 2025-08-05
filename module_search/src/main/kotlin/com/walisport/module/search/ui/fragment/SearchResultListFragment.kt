package com.walisport.module.search.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
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

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setViewPager()
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
                        findNavController().navigate(action, navOptions)
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