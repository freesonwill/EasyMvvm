package com.walisport.module.search.ui.fragment

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.delegate.StatusBarDelegate
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResultOnce
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultUiState.DirectMatch
import com.walisport.module.search.data.constants.SearchResultUiState.ResultList
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.databinding.FragmentSearchResultBaseBinding
import com.walisport.module.search.ui.fragment.SearchDatePickerFragment.Companion.DATE_PICKER_RESULT_TIME_IN_MILLIS
import com.walisport.module.search.ui.viewmodel.SearchResultBaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

class SearchResultBaseFragment :
    SearchBaseFragment<SearchResultBaseViewModel, FragmentSearchResultBaseBinding>() {
    override val vmClass: KClass<SearchResultBaseViewModel>
        get() = SearchResultBaseViewModel::class
    override val contentVbClass: KClass<FragmentSearchResultBaseBinding>
        get() = FragmentSearchResultBaseBinding::class

    private var currentKeyWord: String = ""

    override fun initData() {
        super.initData()

        // 等待換頁動畫完成
        view?.postDelayed({ doSearch() }, 300)
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
                            is ResultList -> goToListResult(it.data, currentKeyWord)
                            is DirectMatch -> goToDirectMatch(it.data, currentKeyWord)
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
        findNavController().also { nav ->
            nav.backQueue.getOrNull(nav.backQueue.size - 2)?.destination?.id?.let { fromId ->
                observeResultOnce<String>(
                    key = SEARCH_KEY,
                    fromId = fromId,
                    navController = nav
                ) { key ->
                    currentKeyWord = key
                    updateSearchText(key)
                    addSearchRecord(key)
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

    private fun goToListResult(data: SearchResultBean, keyWord: String) {
        val action = SearchResultBaseFragmentDirections
            .actionSearchResultBaseFragmentToSearchResultListFragment(data, keyWord)
        navigateTo(action)
    }

    private fun goToDirectMatch(data: SearchResultBean, keyWord: String) {
        val action =
            SearchResultBaseFragmentDirections
                .actionSearchResultBaseFragmentToSearchResultDirectMatchFragment(
                    data, keyWord, null, SearchTypeEnum.UNKNOWN
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
                            findNavController().popBackStack(R.id.searchFragment, false)
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