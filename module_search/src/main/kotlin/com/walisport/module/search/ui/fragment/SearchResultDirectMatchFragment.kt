package com.walisport.module.search.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.model.SearchDailyMatchBean
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.databinding.FragmentSearchResultDirectMatchBinding
import com.walisport.module.search.ui.viewmodel.SearchResultDirectMatchViewModel
import kotlin.reflect.KClass

class SearchResultDirectMatchFragment :
    BaseFragment<SearchResultDirectMatchViewModel, FragmentSearchResultDirectMatchBinding>() {
    override val vbClass: KClass<FragmentSearchResultDirectMatchBinding>
        get() = FragmentSearchResultDirectMatchBinding::class
    override val vmClass: KClass<SearchResultDirectMatchViewModel>
        get() = SearchResultDirectMatchViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}