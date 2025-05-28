package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchResultListBinding
import com.walisport.module.search.ui.adapter.SearchResultPagerAdapter
import com.walisport.module.search.ui.viewmodel.SearchResultViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultListFragment :
    BaseFragment<SearchResultViewModel, FragmentSearchResultListBinding>() {
    override val vbClass: KClass<FragmentSearchResultListBinding>
        get() = FragmentSearchResultListBinding::class
    override val vmClass: KClass<SearchResultViewModel>
        get() = SearchResultViewModel::class

    private val pagerAdapter by lazy {
        SearchResultPagerAdapter(this)
    }

    override fun createVM(): SearchResultViewModel {
        return activityViewModel<SearchResultViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            viewPager.adapter = pagerAdapter
            (viewPager.getChildAt(0) as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER

            TabLayoutMediator(tlSearch, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.tab_all)
                    1 -> getString(R.string.tab_tournament)
                    2 -> getString(R.string.tab_team)
                    3 -> getString(R.string.tab_player)
                    else -> ""
                }
            }.attach()
            tlSearch.getTabAt(0)?.select()
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}