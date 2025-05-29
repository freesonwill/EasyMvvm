package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultPlayerBeanBean
import com.walisport.module.search.data.model.SearchResultTeamBeanBean
import com.walisport.module.search.data.model.SearchResultTournamentBeanBean
import com.walisport.module.search.databinding.FragmentSearchResultPageBinding
import com.walisport.module.search.ui.adapter.SearchResultPageGridAdapter
import com.walisport.module.search.ui.adapter.SearchResultPageLinearAdapter
import com.walisport.module.search.ui.viewmodel.SearchResultViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class SearchResultPageFragment :
    BaseFragment<SearchResultViewModel, FragmentSearchResultPageBinding>() {
    override val vbClass: KClass<FragmentSearchResultPageBinding>
        get() = FragmentSearchResultPageBinding::class
    override val vmClass: KClass<SearchResultViewModel>
        get() = SearchResultViewModel::class

    private val onItemClick = { item: SearchResultBaseBean ->
        mViewModel.getSearchResult(requireContext(), item)
    }

    private val gridAdapter by lazy {
        SearchResultPageGridAdapter().apply {
            onItemClick = this@SearchResultPageFragment.onItemClick
        }
    }

    private val linearAdapter by lazy {
        SearchResultPageLinearAdapter(getType()).apply {
            onItemClick = this@SearchResultPageFragment.onItemClick
        }
    }

    companion object {
        fun newInstance(type: String) = SearchResultPageFragment().apply {
            arguments = Bundle().apply { putString("type", type) }
        }

        internal const val TYPE_ALL = "TYPE_ALL"
        internal const val TYPE_TOURNAMENT = "TYPE_TOURNAMENT"
        internal const val TYPE_TEAM = "TYPE_TEAM"
        internal const val TYPE_PLAYER = "TYPE_PLAYER"
    }

    override fun createVM(): SearchResultViewModel {
        return activityViewModel<SearchResultViewModel>().value
    }

    override fun initView(savedInstanceState: Bundle?) {
        updateUI()
    }

    private fun updateUI() {
        with(mBinding) {
            with(mViewModel) {
                if (getType() == TYPE_ALL) {
                    dynamicStateLayout.visibility = View.GONE
                    recyclerView.apply {
                        val spanCount = 3
                        visibility = View.VISIBLE
                        layoutManager = GridLayoutManager(context, 3)
                            .apply {
                                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                                    override fun getSpanSize(position: Int): Int {
                                        return if (gridAdapter.getItemViewType(position) == SearchResultPageGridAdapter.VIEW_TYPE_HEADER) spanCount else 1
                                    }
                                }
                            }
                        adapter = gridAdapter
                        layoutParams = MarginLayoutParams(
                            MarginLayoutParams.MATCH_PARENT,
                            MarginLayoutParams.MATCH_PARENT
                        ).apply {
                            marginStart = 21.dp2px
                            marginEnd = 21.dp2px
                        }
                        addItemDecoration(object : ItemDecoration() {
                            override fun getItemOffsets(
                                outRect: android.graphics.Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                val position = parent.getChildAdapterPosition(view)
                                if (position == RecyclerView.NO_POSITION) return

                                when (gridAdapter.getItemViewType(position)) {
                                    SearchResultPageGridAdapter.VIEW_TYPE_HEADER -> {
                                        outRect.set(0, 12.dp2px, 0, 12.dp2px)
                                    }

                                    else -> {
                                        outRect.right =
                                            if (position % spanCount == 0 || position % spanCount == 1) 12.dp2px else 0
                                        outRect.bottom = 12.dp2px
                                    }
                                }
                            }
                        })
                    }
                    gridAdapter.submitList(groupData.value)
                } else {
                    val source = groupData.value.filterIsInstance<SearchResultListItemType.Item>().map { it.data }
                    val list = when (getType()) {
                        TYPE_TOURNAMENT -> source.filterIsInstance<SearchResultTournamentBeanBean>()
                        TYPE_TEAM -> source.filterIsInstance<SearchResultTeamBeanBean>()
                        TYPE_PLAYER -> source.filterIsInstance<SearchResultPlayerBeanBean>()
                        else -> emptyList()
                    }
                    if (list.isEmpty()) {
                        dynamicStateLayout.visibility = View.VISIBLE
                        dynamicStateLayout.setState(
                            DynamicStateLayout.States.DATA_EMPTY,
                            ContextCompat.getString(requireContext(), R.string.no_search_result)
                        )
                        recyclerView.visibility = View.GONE
                    } else {
                        dynamicStateLayout.visibility = View.GONE
                        recyclerView.apply {
                            visibility = View.VISIBLE
                            layoutManager =
                                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            adapter = linearAdapter
                        }
                        linearAdapter.submitList(list)
                    }
                }
            }
        }
    }

    private fun getType(): String {
        return arguments?.getString("type") ?: TYPE_ALL
    }

    override fun initListener() {
    }

    override fun createObserver() {
        with(mViewModel) {
            lifecycleScope.launch {
                groupData.collect {
                    updateUI()
                }
            }
        }
    }
}