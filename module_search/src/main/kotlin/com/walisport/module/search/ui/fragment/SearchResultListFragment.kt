package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchResultListBinding
import com.walisport.module.search.ui.adapter.SearchResultPagerAdapter
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultListFragment :
    BaseFragment<SearchViewModel, FragmentSearchResultListBinding>() {
    override val vbClass: KClass<FragmentSearchResultListBinding>
        get() = FragmentSearchResultListBinding::class
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class

    override fun createVM(): SearchViewModel {
        return activityViewModel<SearchViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            if (viewPager.adapter == null) {
                viewPager.adapter = SearchResultPagerAdapter(this@SearchResultListFragment)
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
                switchTab(0, false)
            }
        }
    }

    fun switchTab(position: Int, isSmooth: Boolean = true) {
        mBinding.viewPager.setCurrentItem(position, isSmooth)
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}