package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.constants.SearchResultUiState.DirectMatch
import com.walisport.module.search.data.constants.SearchResultUiState.Empty
import com.walisport.module.search.data.constants.SearchResultUiState.Loading
import com.walisport.module.search.data.constants.SearchResultUiState.ResultList
import com.walisport.module.search.databinding.FragmentSearchResultBaseBinding
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultBaseFragment :
    BaseFragment<SearchViewModel, FragmentSearchResultBaseBinding>() {
    override val vbClass: KClass<FragmentSearchResultBaseBinding>
        get() = FragmentSearchResultBaseBinding::class
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class

    override fun createVM(): SearchViewModel {
        return activityViewModel<SearchViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            dynamicState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                ContextCompat.getString(requireContext(), R.string.no_search_result)
            )
        }
    }

    override fun initData() {
        super.initData()
        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("searchKey")
            ?.let { key ->
                if (key.isNotEmpty()) {
                    mViewModel.getSearchResult(requireContext(), key)

                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.remove<String>("searchKey")
                }
            }
    }

    override fun initListener() = Unit

    override fun createObserver() {
        with(mViewModel) {
            lifecycleScope.launch {
                uiState.collect {
                    when (it) {
                        is ResultList -> goToListResult()
                        is DirectMatch -> goToDirectMatch()
                        is Loading -> switchUi(it)
                        is Empty -> switchUi(it)
                    }
                }
            }
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

    private fun goToListResult() {
        mViewModel.navigateTo(SearchNavigationEvent.ToSearchList)
    }

    private fun goToDirectMatch() {
        mViewModel.navigateTo(SearchNavigationEvent.ToSearchDirectMatch)
    }
}