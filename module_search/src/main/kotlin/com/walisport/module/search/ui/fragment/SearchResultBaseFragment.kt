package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResultOnce
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.common.R as RC
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.constants.SearchResultUiState.DirectMatch
import com.walisport.module.search.data.constants.SearchResultUiState.ResultList
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.databinding.FragmentSearchResultBaseBinding
import com.walisport.module.search.ui.viewmodel.SearchResultBaseViewModel
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class SearchResultBaseFragment :
    BaseFragment<SearchResultBaseViewModel, FragmentSearchResultBaseBinding>() {
    override val vbClass: KClass<FragmentSearchResultBaseBinding>
        get() = FragmentSearchResultBaseBinding::class
    override val vmClass: KClass<SearchResultBaseViewModel>
        get() = SearchResultBaseViewModel::class

    private val sharedViewModel: SearchViewModel by sharedViewModel<SearchViewModel, SearchFragment>()

    override fun initView(savedInstanceState: Bundle?) = Unit

    override fun initData() {
        super.initData()
        doSearch()
    }

    override fun initListener() = Unit

    override fun createObserver() {
        with(mViewModel) {
            launch(Lifecycle.State.STARTED) {
                launch {
                    apiStateListener.observe(viewLifecycleOwner) { state ->
                        when (state) {
                            is DataState.Loading,
                            is DataState.DataEmpty,
                            is DataState.NetworkUnavailable,
                            is DataState.None -> switchUi(state)
                        }
                    }
                }

                launch {
                    uiState.collect {
                        when (it) {
                            is ResultList -> goToListResult(it.data)
                            is DirectMatch -> goToDirectMatch(it.data)
                        }
                    }
                }
            }
        }
    }

    private fun doSearch() {
        findNavController().also { nav ->
            nav.backQueue.getOrNull(nav.backQueue.size - 2)?.destination?.id?.let { fromId ->
                observeResultOnce<String>(
                    key = SearchFragment.SEARCH_KEY,
                    fromId = fromId,
                    navController = nav
                ) { key ->
                    mViewModel.getSearchResult(key)
                }
            }
        }
    }

    private fun setEmptyView(state: DataState) {
        with(mBinding) {
            val layoutState =
                if(state == DataState.NetworkUnavailable) DynamicStateLayout.States.NETWORK_ANOMALY
                else DynamicStateLayout.States.DATA_EMPTY
            val errorStr =
                if(state == DataState.NetworkUnavailable) {
                    SkinnableResourceManager.getString(
                        requireContext(),
                        RC.string.error_net,
                        sharedViewModel.getCurrentLanguage()
                    )
                } else {
                    SkinnableResourceManager.getString(
                        requireContext(),
                        R.string.no_search_result,
                        sharedViewModel.getCurrentLanguage()
                    )
                }
            val onRefresh: (() -> Unit)? =
                if(state == DataState.NetworkUnavailable) { ::doSearch }
                else null

            dynamicState.setState(layoutState, errorStr, onRefresh)
        }
    }

    private fun switchUi(state: DataState) {
        with(mBinding) {
            val isLoading = state == DataState.Loading

            if(!isLoading) {
                setEmptyView(state)
            }

            loadingView.visibility =
                if (isLoading) View.VISIBLE
                else View.GONE
            dynamicState.visibility =
                if (!isLoading) View.VISIBLE
                else View.GONE
        }
    }

    private fun goToListResult(data: SearchResultBean) {
        navigateTo(SearchNavigationEvent.ToSearchList(data))
    }

    private fun goToDirectMatch(data: SearchResultBean) {
        navigateTo(SearchNavigationEvent.ToSearchDirectMatch(data))
    }

    private fun navigateTo(event: SearchNavigationEvent) {
        sharedViewModel.setNavigationEvent(event)
    }
}