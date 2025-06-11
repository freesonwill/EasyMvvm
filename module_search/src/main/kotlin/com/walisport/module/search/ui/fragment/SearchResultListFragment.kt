package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchResultListBinding
import com.walisport.module.search.ui.adapter.SearchResultPagerAdapter
import com.walisport.module.search.ui.viewmodel.SearchResultListViewModel
import kotlin.reflect.KClass

class SearchResultListFragment :
    BaseFragment<SearchResultListViewModel, FragmentSearchResultListBinding>() {
    override val vbClass: KClass<FragmentSearchResultListBinding>
        get() = FragmentSearchResultListBinding::class
    override val vmClass: KClass<SearchResultListViewModel>
        get() = SearchResultListViewModel::class

    private val args: SearchResultListFragmentArgs by navArgs()
    private var tabMediator: TabLayoutMediator? = null

    override fun initView(savedInstanceState: Bundle?) {
        disablePadding()
        setViewPager()
    }

    override fun initListener() = Unit

    override fun createObserver() = Unit

    override fun onDestroyView() {
        mBinding.viewPager.adapter = null
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }

    private fun disablePadding() {
        mBinding.root.setOnApplyWindowInsetsListener { _, insets ->
            insets
        }
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

    fun switchTab(position: Int, isSmooth: Boolean = true) {
        mBinding.viewPager.setCurrentItem(position, isSmooth)
    }
}