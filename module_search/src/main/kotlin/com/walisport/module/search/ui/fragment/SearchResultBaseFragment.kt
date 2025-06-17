package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResultOnce
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.constants.SearchResultUiState.DirectMatch
import com.walisport.module.search.data.constants.SearchResultUiState.Empty
import com.walisport.module.search.data.constants.SearchResultUiState.Loading
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

    override fun initView(savedInstanceState: Bundle?) {
        setEmptyView()
    }

    override fun initData() {
        super.initData()
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

    override fun initListener() = Unit

    override fun createObserver() {
        with(mViewModel) {
            launch(Lifecycle.State.STARTED) {
                uiState.collect {
                    when (it) {
                        is ResultList -> goToListResult(it.data)
                        is DirectMatch -> goToDirectMatch(it.data)
                        is Loading -> switchUi(it)
                        is Empty -> switchUi(it)
                    }
                }
            }
        }
    }

    private fun setEmptyView() {
        with(mBinding) {
            dynamicState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                ContextCompat.getString(requireContext(), R.string.no_search_result)
            )
        }
    }

    private fun switchUi(state: SearchResultUiState) {
        with(mBinding) {
            loadingView.visibility =
                if (state is Loading) View.VISIBLE
                else View.GONE
            dynamicState.visibility =
                if (state is Empty) View.VISIBLE
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