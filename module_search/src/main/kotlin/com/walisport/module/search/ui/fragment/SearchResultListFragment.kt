package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchResultListBinding
import com.walisport.module.search.ui.adapter.SearchResultPagerAdapter
import com.walisport.module.search.ui.viewmodel.SearchResultListViewModel
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlin.reflect.KClass

class SearchResultListFragment :
    BaseFragment<SearchResultListViewModel, FragmentSearchResultListBinding>() {
    override val vbClass: KClass<FragmentSearchResultListBinding>
        get() = FragmentSearchResultListBinding::class
    override val vmClass: KClass<SearchResultListViewModel>
        get() = SearchResultListViewModel::class

    private val sharedViewModel: SearchViewModel by sharedViewModel<SearchViewModel, SearchFragment>()
    private val args: SearchResultListFragmentArgs by navArgs()
    private var tabMediator: TabLayoutMediator? = null

    override fun initView(savedInstanceState: Bundle?) {
        setViewPager()
    }

    override fun initListener() = Unit

    override fun createObserver() {
        launch(Lifecycle.State.STARTED) {
            sharedViewModel.currentLanguage.collect {
                // 更新Tab標籤文字
                updateTabTitles()
            }
        }
    }

    override fun onDestroyView() {
        mBinding.viewPager.adapter = null
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }


    private fun setViewPager() {
        with(mBinding) {
            if (viewPager.adapter == null) {
                viewPager.adapter =
                    SearchResultPagerAdapter(
                        this@SearchResultListFragment,
                        args.data
                    )
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
                switchTab(0, false)
            }
        }
    }

    /** 更新Tab標籤文字 */
    private fun updateTabTitles() {
        val locale = sharedViewModel.getCurrentLanguage()
        val titles = listOf(
            SkinnableResourceManager.getString(requireContext(), R.string.tab_all, locale),
            SkinnableResourceManager.getString(requireContext(), R.string.tab_tournament, locale),
            SkinnableResourceManager.getString(requireContext(), R.string.tab_team, locale),
            SkinnableResourceManager.getString(requireContext(), R.string.tab_player, locale)
        )

        with(mBinding.tlSearch) {
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
        mBinding.viewPager.setCurrentItem(position, isSmooth)
    }
}