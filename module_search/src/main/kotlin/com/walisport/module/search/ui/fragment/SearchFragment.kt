package com.walisport.module.search.ui.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.databinding.FragmentSearchBinding
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author: caomei
 * @date: 2025/4/24 16:31
 * @description:搜索
 */
class SearchFragment : SearchBaseFragment<SearchViewModel, FragmentSearchBinding>() {
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class
    override val contentLayoutId = R.layout.fragment_search

    override fun createObserver() {
        super.createObserver()
        with(mViewModel) {
            launch(Lifecycle.State.STARTED) {
                launch {
                    navigationEvent.collect { event ->
                        doNavigate(event)
                    }
                }
                launch {
                    resultBackgroundColor.collect { color ->
                        setResultBackground(
                            color != null,
                            color ?: R.color.search_result_default_gradient_start
                        )
                    }
                }
            }
        }
    }

    fun addSearchRecord(word: String) {
        if (word.isNotEmpty()) {
            mViewModel.addOneRecord(word)
            notifyUpdateRecordList(word)
        }
    }

    override fun setBackPressHandler() {
        super.setBackPressHandler()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navController = findNavController()
            val backStackId = navController.previousBackStackEntry?.destination?.id

            when (backStackId) {
                R.id.searchResultBaseFragment -> {
                    navController.popBackStack(R.id.searchResultBaseFragment, true)
                }
                else -> {
                    if (!navController.popBackStack()) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }

    private fun setResultBackground(isShow: Boolean, color: Int? = null) {
        mBinding.clRoot.apply {
            val duration = 100
            if (isShow) {
                GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        color ?: ContextCompat.getColor(context, R.color.search_result_default_gradient_start),
                        Color.BLACK
                    )
                ).let { newDrawable ->
                    background = TransitionDrawable(
                        arrayOf(background, newDrawable)
                    ).apply {
                        startTransition(duration)
                    }
                }
            } else {
                SkinnableResourceManager
                    .getColor(context, R.color.search_main_bg)
                    .toDrawable()
                    .let { newDrawable ->
                        background = TransitionDrawable(
                            arrayOf(background, newDrawable)
                        ).apply {
                            startTransition(duration)
                        }
                    }
            }
        }
    }

    private fun doNavigate(event: SearchNavigationEvent) {
        val navController = findNavController()
        val currentId = navController.currentDestination?.id ?: return

        when (event) {
            is SearchNavigationEvent.ToSearchResultBase -> {
                mapOf(
                    R.id.searchMainFragment to R.id.action_searchMainFragment_to_searchResultBaseFragment,
                    R.id.searchResultListFragment to R.id.action_searchResultListFragment_to_searchResultBaseFragment,
                    R.id.searchResultDirectMatchFragment to R.id.action_searchResultDirectMatchFragment_to_searchResultBaseFragment
                )[currentId]?.let { actionId ->
                    sendResult(
                        key = SEARCH_KEY,
                        value = event.searchKey,
                        destinationId = currentId,
                        navController = navController
                    )
                    navController.navigate(actionId)
                }
            }
            is SearchNavigationEvent.ToSearchDirectMatch -> {
                if (currentId == R.id.searchResultBaseFragment) {
                    val action =
                        SearchResultBaseFragmentDirections
                            .actionSearchResultBaseFragmentToSearchResultDirectMatchFragment(
                                event.data, event.keyword, event.id, event.type ?: SearchTypeEnum.UNKNOWN
                            )
                    navController.navigate(action)
                } else if (currentId == R.id.searchResultListFragment) {
                    val action =
                        SearchResultListFragmentDirections
                            .actionSearchResultListFragmentToSearchResultDirectMatchFragment(
                                event.data, event.keyword, event.id, event.type ?: SearchTypeEnum.UNKNOWN
                            )
                    navController.navigate(action)
                }
            }
            is SearchNavigationEvent.ToSearchList -> {
                if (currentId == R.id.searchResultBaseFragment) {
                    val action = SearchResultBaseFragmentDirections
                        .actionSearchResultBaseFragmentToSearchResultListFragment(event.data)
                    navController.navigate(action)
                }
            }

            is SearchNavigationEvent.ToLiveFragment -> {
                navigate(event.deepLink.toUri())
            }
        }
    }

    private fun notifyUpdateRecordList(key: String) {
        childFragmentManager.fragments
            .filterIsInstance<SearchMainFragment>()
            .forEach { fragment ->
                (fragment as? SearchMainFragment)?.notifyUpdateRecordList(key)
            }
    }
}