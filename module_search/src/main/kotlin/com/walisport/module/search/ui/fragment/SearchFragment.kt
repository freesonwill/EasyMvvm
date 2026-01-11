package com.walisport.module.search.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.view.ClearableEditText
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

    // 遊戲分類匹配 Fragment（例如：輸入「電子」時顯示）
    private var gameContentFragment: SearchGameContentFragment? = null

    companion object {
        // 匹配關鍵字列表（目前僅「電子」，未來可擴展）
        private val MATCH_KEYWORDS = setOf("電子")
        // 匹配關鍵字對應的 gameTypeId（例如：電子 = 4）
        private val KEYWORD_TO_GAME_TYPE_ID = mapOf(
            "電子" to 4
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setHistory()
        setHotWords()
        mBinding.root.touchBackPressed()
        contentBinding.root.touchBackPressed()
        
        autoShowKeyboard()
        setupGameContentFragment()
    }

    /**
     * 設置遊戲分類匹配 Fragment 容器
     */
    private fun setupGameContentFragment() {
        // Fragment 將在關鍵字匹配時動態添加
    }

    /**
     * 判斷關鍵字是否為遊戲分類匹配關鍵字（例如：「電子」）
     */
    private fun isGameCategoryKeyword(keyword: String?): Boolean {
        return !keyword.isNullOrBlank() && MATCH_KEYWORDS.contains(keyword.trim())
    }

    /**
     * 重寫基類方法，處理遊戲分類匹配關鍵字（例如：「電子」）。
     * 當輸入匹配關鍵字時，顯示 SearchGameContentFragment 而不是推薦列表。
     */
    override fun handleKeywordChanged(keyword: String): Boolean {
        val trimmedKeyword = keyword.trim()
        
        if (trimmedKeyword.isNotEmpty() && isGameCategoryKeyword(trimmedKeyword)) {
            // 顯示遊戲分類匹配頁面
            showGameContentFragment(trimmedKeyword)
            return true  // 已處理，不需要顯示推薦列表
        } else {
            // 隱藏遊戲分類匹配頁面
            hideGameContentFragment()
            return false  // 未處理，繼續使用推薦列表
        }
    }

    /**
     * 顯示遊戲分類匹配 Fragment（當輸入匹配關鍵字時）
     */
    fun showGameContentFragment(keyword: String) {
        val gameTypeId = KEYWORD_TO_GAME_TYPE_ID[keyword.trim()] ?: return
        with(contentBinding) {
            // 隱藏歷史記錄與熱門搜索區塊，改為顯示遊戲內容
            clHistory.visibility = View.GONE
            clHotWord.visibility = View.GONE
            dynamicState.visibility = View.GONE

            // 顯示遊戲內容容器
            flGameContentContainer.visibility = View.VISIBLE

            // 如果 Fragment 尚未添加，則添加它
            if (gameContentFragment == null) {
                gameContentFragment = SearchGameContentFragment.newInstance(gameTypeId, keyword)
                childFragmentManager.beginTransaction()
                    .replace(R.id.fl_game_content_container, gameContentFragment!!)
                    .commitAllowingStateLoss()
            }
        }
    }

    /**
     * 隱藏遊戲分類匹配 Fragment（當關鍵字不匹配或清空時）
     */
    fun hideGameContentFragment() {
        with(contentBinding) {
            flGameContentContainer.visibility = View.GONE
            // 還原歷史記錄區塊的可見性（實際內容是否顯示由各自觀察者決定）
            clHistory.visibility = View.VISIBLE
            gameContentFragment?.let { fragment ->
                childFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss()
                gameContentFragment = null
            }
        }
    }
    
    /**
     * 自動喚起鍵盤並設置搜索框為可輸入狀態
     */
    private fun autoShowKeyboard() {
        val searchEditText = mBinding.titleBar.findViewById<ClearableEditText>(RC.id.ce_search)
            ?: return
        
        searchEditText.isFocusable = true
        searchEditText.isFocusableInTouchMode = true
        
        // 使用 doOnLayout 確保視圖布局完成後再喚起鍵盤
        searchEditText.doOnLayout {
            if (isAdded && view != null) {
                searchEditText.requestFocus()
                
                // 使用 post 確保焦點設置完成後再喚起鍵盤
                searchEditText.post {
                    if (isAdded && view != null && searchEditText.hasFocus()) {
                        val imm = requireContext().getSystemService(InputMethodManager::class.java)
                        imm?.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
                    }
                }
            }
        }
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
                            .filter { it == true && apiStateListener.value == DataState.NetworkUnavailable }
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
        hideGameContentFragment()
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
                            outRect.left = if(position % 2 != 0) 20.dp2px else 0
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