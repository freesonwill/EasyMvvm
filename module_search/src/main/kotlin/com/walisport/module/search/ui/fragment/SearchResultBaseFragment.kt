package com.walisport.module.search.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
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

        // 處理navigate過來的（第一次搜尋的）
        view?.postDelayed({ doSearch() }, 300)
    }

    override fun initListener() {
        super.initListener()

        parentFragmentManager.setFragmentResultListener(FROM_POP_BACK, viewLifecycleOwner) { _, bundle ->
            // 處理popBack過來的（再次搜尋的）
            bundle.getBoolean(FROM_POP_BACK).let {
                if(it) doSearch()
            }
        }

        parentFragmentManager.setFragmentResultListener(GO_BACK_TO_MAIN, viewLifecycleOwner) { _, bundle ->
            parentFragmentManager.clearFragmentResultListener(GO_BACK_TO_MAIN)

            bundle.getBoolean(GO_BACK_TO_MAIN).let {
                if (it) {
                    // 攔截返回時機，加入畫面截圖遮罩並延遲 popBackStack，
                    // 避免中間頁閃爍，實現從 SearchResultListFragment / SearchResultDirectMatchFragment
                    // 直接返回 SearchFragment 的流暢轉場效果
                    (requireActivity().window.decorView as ViewGroup).apply {
                        ImageView(requireContext()).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setImageBitmap(getTempScreenShot())
                        }.let { overlay ->
                            addView(overlay, childCount)
                            overlay.doOnLayout {
                                parentFragmentManager.apply {
                                    run {
                                        object : FragmentManager.FragmentLifecycleCallbacks() {
                                            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                                                if (f is SearchFragment) {
                                                    fm.unregisterFragmentLifecycleCallbacks(this)
                                                    AnimationUtils.loadAnimation(f.requireContext(), RC.anim.slide_out_right).apply {
                                                        setAnimationListener(object :
                                                            Animation.AnimationListener {
                                                            override fun onAnimationRepeat(p0: Animation?) = Unit
                                                            override fun onAnimationStart(p0: Animation?) = Unit
                                                            override fun onAnimationEnd(p0: Animation?) {
                                                                removeView(overlay)
                                                            }
                                                        })
                                                    }?.let { anim ->
                                                        overlay.startAnimation(anim)
                                                    }
                                                }
                                            }
                                        }
                                    }.let { callback ->
                                        registerFragmentLifecycleCallbacks(callback, true)
                                    }
                                }
                                findNavController().popBackStack(R.id.searchFragment, false)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun createObserver() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        clearTempScreenShot()
    }

    private fun doSearch() {
        addSearchRecord(getCurrentKeyword())
        mViewModel.getSearchResult(getCurrentKeyword())
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
            .actionSearchResultBaseFragmentToSearchResultListFragment(data, getCurrentKeyword())
        navigateTo(action)
    }

    private fun goToDirectMatch(data: SearchResultBean) {
        val action =
            SearchResultBaseFragmentDirections
                .actionSearchResultBaseFragmentToSearchResultDirectMatchFragment(
                    data, getCurrentKeyword(), null, SearchTypeEnum.UNKNOWN
                )
        navigateTo(action)
    }

    private fun navigateTo(action: NavDirections) {
        with(findNavController()) {
            // 避免轉跳前用戶已經返回前一頁造成 Crash
            if (isAdded && currentDestination?.id == R.id.searchResultBaseFragment) {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(0)
                    .setExitAnim(0)
                    .setPopEnterAnim(0)
                    .setPopExitAnim(0)
                    .build()
                navigate(action, navOptions)
            }
        }
    }
}