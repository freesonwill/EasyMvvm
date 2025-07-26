package com.walisport.module.search.ui.fragment

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResultOnce
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

    private var currentKeyword: String = ""

    override fun initData() {
        super.initData()

        // 等待換頁動畫完成
        view?.postDelayed({
            // 處理navigate過來的（第一次搜尋的）
            findNavController().also { nav ->
                nav.backQueue.getOrNull(nav.backQueue.size - 2)?.destination?.id?.let { fromId ->
                    observeResultOnce<String>(
                        key = SEARCH_KEY,
                        fromId = fromId,
                        navController = nav
                    ) { key ->
                        currentKeyword = key
                        doSearch()
                    }
                }
            }
        }, 300)
    }

    override fun initListener() {
        super.initListener()

        // 處理popBack過來的（再次搜尋的）
        parentFragmentManager.setFragmentResultListener(SEARCH_KEY, viewLifecycleOwner) { _, bundle ->
            bundle.getString(SEARCH_KEY)?.let { keyword ->
                currentKeyword = keyword
                doSearch()
            }
        }
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
                            is ResultList -> goToListResult(it.data, currentKeyword)
                            is DirectMatch -> goToDirectMatch(it.data, currentKeyword)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearTempScreenShot()
    }

    private fun doSearch() {
        updateSearchText(currentKeyword)
        addSearchRecord(currentKeyword)
        mViewModel.getSearchResult(currentKeyword)
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

    private fun goToListResult(data: SearchResultBean, keyword: String) {
        val action = SearchResultBaseFragmentDirections
            .actionSearchResultBaseFragmentToSearchResultListFragment(data, keyword)
        navigateTo(action)
    }

    private fun goToDirectMatch(data: SearchResultBean, keyword: String) {
        val action =
            SearchResultBaseFragmentDirections
                .actionSearchResultBaseFragmentToSearchResultDirectMatchFragment(
                    data, keyword, null, SearchTypeEnum.UNKNOWN
                )
        navigateTo(action)
    }

    private fun navigateTo(action: NavDirections) {
        parentFragmentManager.setFragmentResultListener(GO_BACK_TO_MAIN, viewLifecycleOwner) { _, bundle ->
            parentFragmentManager.clearFragmentResultListener(GO_BACK_TO_MAIN)

            bundle.getBoolean(GO_BACK_TO_MAIN).let {
                if (it) {
                    // 攔截返回時機，加入畫面截圖遮罩並延遲 popBackStack，
                    // 避免中間頁閃爍，實現從 SearchResultListFragment / SearchResultDirectMatchFragment
                    // 直接返回 SearchFragment 的流暢轉場效果
                    WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
                    requireActivity().window.statusBarColor = Color.TRANSPARENT
                    ViewCompat.setOnApplyWindowInsetsListener(requireView()) { v, insets ->
                        v.setPadding(0, 0, 0, 0)
                        insets
                    }
                    mBinding.root.apply {
                        ImageView(requireContext()).apply {
                            setImageBitmap(getTempScreenShot())
                        }.let { view -> addView(view) }
                        doOnLayout {
                            parentFragmentManager.setFragmentResult(SEARCH_KEY, bundleOf(SEARCH_KEY to currentKeyword))
                            findNavController().popBackStack(R.id.searchFragment, false)
                            requireView().postDelayed({ updateStatusSearchBar() }, 300)
                        }
                    }
                    ViewCompat.requestApplyInsets(requireView())
                }
            }
        }
        findNavController().navigate(action)
    }

    companion object {
        const val GO_BACK_TO_MAIN = "GO_BACK_TO_MAIN"
    }
}