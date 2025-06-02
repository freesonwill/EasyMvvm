package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.constants.SearchResultUiState.Loading
import com.walisport.module.search.data.constants.SearchResultUiState.Empty
import com.walisport.module.search.data.constants.SearchResultUiState.ResultList
import com.walisport.module.search.data.constants.SearchResultUiState.DirectMatch
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.databinding.FragmentSearchResultBinding
import com.walisport.module.search.ui.viewmodel.SearchResultViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultFragment: BaseFragment<SearchResultViewModel, FragmentSearchResultBinding>() {
    override val vbClass: KClass<FragmentSearchResultBinding>
        get() = FragmentSearchResultBinding::class
    override val vmClass: KClass<SearchResultViewModel>
        get() = SearchResultViewModel::class

    override fun createVM(): SearchResultViewModel {
        return activityViewModel<SearchResultViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            dynamicState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                ContextCompat.getString(requireContext(), R.string.no_search_result)
            )
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        with(mViewModel) {
            lifecycleScope.launch {
                uiState.collect {
                    switchUi(it)
                }
            }

            lifecycleScope.launch {
                gradientBgColor.collect {
                    (parentFragment as? SearchFragment)?.updateResultBackground(
                        it != null,
                        it ?: R.color.search_result_default_gradient_start
                    )
                }
            }
        }
    }

    private fun switchUi(state: SearchResultUiState) {
        when (state) {
            is ResultList -> SearchResultListFragment()
            is DirectMatch -> SearchResultDirectMatchFragment()
            else -> null
        }?.let {
            childFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                it
            ).commit()
        }

        with(mBinding) {
            loadingView.visibility =
                if (state is Loading) View.VISIBLE
                else View.GONE
            dynamicState.visibility =
                if (state is Empty) View.VISIBLE
                else View.GONE
            fragmentContainer.visibility =
                if (state is ResultList || state is DirectMatch) View.VISIBLE
                else View.GONE
        }
    }

    fun setResult(result: SearchResultBean) {
        mViewModel.setResult(requireContext(), result)
    }
}