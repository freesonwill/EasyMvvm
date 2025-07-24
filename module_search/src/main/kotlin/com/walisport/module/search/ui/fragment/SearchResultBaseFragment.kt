package com.walisport.module.search.ui.fragment

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResultOnce
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultUiState.DirectMatch
import com.walisport.module.search.data.constants.SearchResultUiState.ResultList
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.databinding.FragmentSearchResultBaseBinding
import com.walisport.module.search.ui.viewmodel.SearchResultBaseViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

class SearchResultBaseFragment :
    SearchBaseFragment<SearchResultBaseViewModel, FragmentSearchResultBaseBinding>() {
    override val vmClass: KClass<SearchResultBaseViewModel>
        get() = SearchResultBaseViewModel::class
    override val contentVbClass: KClass<FragmentSearchResultBaseBinding>
        get() = FragmentSearchResultBaseBinding::class

    override fun initData() {
        super.initData()
        doSearch()
    }

    override fun createObserver() {
        super.createObserver()
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
                    key = SEARCH_KEY,
                    fromId = fromId,
                    navController = nav
                ) { key ->
                    mViewModel.getSearchResult(key)
                }
            }
        }
    }

    private fun setEmptyView(state: DataState) {
        with(contentBinding) {
            val layoutState =
                if(state == DataState.NetworkUnavailable) DynamicStateLayout.States.NETWORK_ANOMALY
                else DynamicStateLayout.States.DATA_EMPTY
            val errorStr =
                if(state == DataState.NetworkUnavailable) {
                    RC.string.error_net.toTranslatedStr()
                } else {
                    R.string.no_search_result.toTranslatedStr()
                }
            val onRefresh: (() -> Unit)? =
                if(state == DataState.NetworkUnavailable) { ::doSearch }
                else null

            dynamicState.setState(layoutState, errorStr, onRefresh)
        }
    }

    private fun switchUi(state: DataState) {
        with(contentBinding) {
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
        val action = SearchResultBaseFragmentDirections
            .actionSearchResultBaseFragmentToSearchResultListFragment(data)
        navigateTo(action)
    }

    private fun goToDirectMatch(data: SearchResultBean) {
        val action =
            SearchResultBaseFragmentDirections
                .actionSearchResultBaseFragmentToSearchResultDirectMatchFragment(
                    data, null, null, SearchTypeEnum.UNKNOWN
                )
        navigateTo(action)
    }

    private fun navigateTo(action: NavDirections) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.searchResultBaseFragment, true)
            .build()
        findNavController().navigate(action, navOptions)
    }
}