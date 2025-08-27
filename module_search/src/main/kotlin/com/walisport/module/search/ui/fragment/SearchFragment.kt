package com.walisport.module.search.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchBinding
import com.walisport.module.search.ui.adapter.HotWordAdapter
import com.walisport.module.search.ui.adapter.SearchHistoryAdapter
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

/**
 * @author: caomei
 * @date: 2025/4/24 16:31
 * @description:搜索
 */
class SearchFragment : SearchBaseFragment<SearchViewModel, FragmentSearchBinding>() {
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class
    override val contentVbClass: KClass<FragmentSearchBinding>
        get() = FragmentSearchBinding::class

    private var historyAdapter: SearchHistoryAdapter? = null
    private var isEditor: Boolean = false

    private val hotWordAdapter by lazy {
        HotWordAdapter { hotWord ->
            updateSearchText(hotWord)
            toSearchResult(hotWord)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setHistory()
        setHotWords()
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        super.initData()
        getData()
    }

    override suspend fun createObserver() {
        super.createObserver()
        with(contentBinding) {
            with(mViewModel) {
                searchRecord.observe(viewLifecycleOwner) {
                    historyAdapter?.setNewData(it.reversed().toMutableList())
                    textNoHistory.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    rlHistoryDelete.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }
                searchHotWord.observe(viewLifecycleOwner) {
                    hotWordAdapter.submitList(it)
                    clHotWord.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }

                launch(Lifecycle.State.STARTED) {
                    launch {
                        apiStateListener.observe(viewLifecycleOwner) { state ->
                            switchUI(state)
                        }
                    }

                    launch {
                        observeLoginChange()
                            .filter { it && apiStateListener.value == DataState.NetworkUnavailable }
                            .collect { getData() }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getRecordByUID()
    }

    override fun onDestroyView() {
        historyAdapter?.setOnDataChangedListener(null)
        historyAdapter = null
        contentBinding.rvHotWord.adapter = null
        super.onDestroyView()
    }

    private fun getData() {
        //获取搜索记录
        mViewModel.getRecordByUID()

        //获取热门搜索
        mViewModel.getSearchHotWord { error ->
            error?.let { showToast(it.msg) }
        }
    }

    private fun setHistory() {
        with(contentBinding) {
            with(mViewModel) {
                //设置搜索历史
                historyAdapter = SearchHistoryAdapter(
                    closeAction = { position, text ->
                        historyAdapter?.deleteData(position)
                        mViewModel.deleteOneRecord(text)
                        mViewModel.getRecordByUID()
                    },
                    onSearch = { content ->
                        content.let {
                            updateSearchText(content)
                            toSearchResult(content)
                        }
                    }
                )
                hfList.apply {
                    setAdapter(historyAdapter)
                    setMaxFoldLines(2)
                }

                //删除图标，点击进入删除模式
                ivClickShowDelete.clickNoRepeat {
                    setHistoryButton()
                }

                //完成按钮
                txtCompletedAll.clickNoRepeat {
                    setHistoryButton()
                }

                //全部删除按钮
                txtDeleteAll.clickNoRepeat {
                    CommonDialog.newInstance(
                        title = "",
                        message = R.string.search_dialog_delete_all_history.toTranslatedStr(),
                        okText = R.string.search_dialog_confirm.toTranslatedStr(),
                        cancelText = R.string.search_dialog_cancel.toTranslatedStr()
                    ).apply {
                        setOnOkClickListener {
                            historyAdapter?.deleteAllData()
                            deleteAllData()
                            getRecordByUID()
                            setHistoryButton()
                        }
                    }.show(parentFragmentManager)
                }
            }
        }
    }

    private fun setHotWords() {
        with(contentBinding) {
            //热门搜索
            rvHotWord.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = hotWordAdapter
                itemAnimator = null
                if(itemDecorationCount == 0) {
                    addItemDecoration(object: RecyclerView.ItemDecoration(){
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            val position = parent.getChildAdapterPosition(view)
                            if(position == RecyclerView.NO_POSITION) return
                            outRect.left = if(position % 2 != 0) 17.dp2px else 0
                        }
                    })
                }
            }
        }
    }

    private fun setHistoryButton() {
        with(contentBinding) {
            isEditor = !isEditor
            historyAdapter?.setDeleteMode(isEditor)
            hfList.setDeleteMode(isEditor)
            if (isEditor) {
                ivClickShowDelete.visibility = View.GONE
                llShowCompleted.visibility = View.VISIBLE
            } else {
                ivClickShowDelete.visibility = View.VISIBLE
                llShowCompleted.visibility = View.GONE
            }
        }
    }

    private fun switchUI(state: DataState) {
        with(contentBinding) {
            loadingView.visibility = if (state is DataState.Loading) View.VISIBLE else View.GONE
            clHotWord.visibility = if (state is DataState.LoadSuccess) View.VISIBLE else View.GONE

            when (state) {
                is DataState.NetworkUnavailable,
                is DataState.DataEmpty,
                is DataState.None -> {
                    setEmptyView(state)
                    dynamicState.visibility = View.VISIBLE
                }
                else -> {
                    dynamicState.visibility = View.GONE
                }
            }
        }
    }

    private fun setEmptyView(state: DataState) {
        val layoutState =
            if(state == DataState.NetworkUnavailable) DynamicStateLayout.States.NETWORK_ANOMALY()
            else DynamicStateLayout.States.DATA_EMPTY
        val errorStr =
            if(state == DataState.NetworkUnavailable) {
                RC.string.error_net.toTranslatedStr()
            } else {
                R.string.no_search_result.toTranslatedStr()
            }
        contentBinding.dynamicState.setState(layoutState, errorStr)
    }
}