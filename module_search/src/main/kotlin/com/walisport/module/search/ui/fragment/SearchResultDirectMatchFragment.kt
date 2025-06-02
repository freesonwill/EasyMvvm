package com.walisport.module.search.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.search.databinding.FragmentSearchResultDirectMatchBinding
import com.walisport.module.search.ui.viewmodel.SearchResultViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultDirectMatchFragment :
    BaseFragment<SearchResultViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class
    override val vmClass: KClass<SearchResultViewModel>
        get() = SearchResultViewModel::class

    override fun createVM(): SearchResultViewModel {
        return activityViewModel<SearchResultViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
        with(mBinding) {
            with(mViewModel) {
                lifecycleScope.launch {
                    directData.collect { data ->
                        //TODO
                    }
                }

                lifecycleScope.launch {
                    combineResult.collect { combineResult ->
                        //TODO
                    }
                }
            }
        }
    }
}