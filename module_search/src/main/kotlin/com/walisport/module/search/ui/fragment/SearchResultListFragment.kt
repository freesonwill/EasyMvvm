package com.walisport.module.search.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.databinding.FragmentSearchResultListBinding
import com.walisport.module.search.ui.viewmodel.SearchResultListViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultListFragment(private val listData: List<SearchResultBaseBean>) :
    BaseFragment<SearchResultListViewModel, FragmentSearchResultListBinding>() {
    override val vbClass: KClass<FragmentSearchResultListBinding>
        get() = FragmentSearchResultListBinding::class
    override val vmClass: KClass<SearchResultListViewModel>
        get() = SearchResultListViewModel::class

    override fun createVM(): SearchResultListViewModel {
        return activityViewModel<SearchResultListViewModel>().value
    }

    override fun initData() {
        super.initData()
        mViewModel.setData(requireContext(), listData)
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}