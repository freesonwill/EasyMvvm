package com.walisport.module.search.ui.fragment

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.databinding.FragmentSearchMainBinding
import com.walisport.module.search.ui.adapter.HotWordAdapter
import com.walisport.module.search.ui.adapter.SearchHistoryAdapter
import com.walisport.module.search.ui.viewmodel.SearchMainViewModel
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlin.reflect.KClass

class SearchMainFragment: BaseFragment<SearchMainViewModel, FragmentSearchMainBinding>() {
    override val vbClass: KClass<FragmentSearchMainBinding>
        get() = FragmentSearchMainBinding::class
    override val vmClass: KClass<SearchMainViewModel>
        get() = SearchMainViewModel::class

    private val sharedViewModel: SearchViewModel by sharedViewModel<SearchViewModel, SearchFragment>()
    private var historyAdapter: SearchHistoryAdapter? = null
    private var isEditor: Boolean = false

    private val hotWordAdapter by lazy {
        HotWordAdapter { hotWord ->
            navigateTo(SearchNavigationEvent.ToSearchResultBase(hotWord))
            updateSearchKey(hotWord)
            (requireParentFragment().parentFragment as? SearchFragment)?.addSearchRecord(hotWord)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setHistory()
        setHotWords()
    }

    override fun initData() {
        super.initData()
        //获取搜索记录
        mViewModel.getRecordByUID()

        //获取热门搜索
        mViewModel.getSearchHotWord()
    }

    override fun initListener() = Unit

    override fun createObserver() {
        with(mBinding) {
            with(mViewModel) {
                searchRecord.observe(viewLifecycleOwner) {
                    historyAdapter?.setNewData(it.reversed().toMutableList())
                    clHistory.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }
                searchHotWord.observe(viewLifecycleOwner) {
                    hotWordAdapter.submitList(it)
                    clHotWord.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        historyAdapter?.setOnDataChangedListener(null)
        historyAdapter = null
        mBinding.rvHotWord.adapter = null
        super.onDestroyView()
    }

    private fun setHistory() {
        with(mBinding) {
            with(mViewModel) {
                //设置搜索历史
                historyAdapter = SearchHistoryAdapter(
                    closeAction = { position, text ->
                        historyAdapter?.deleteData(position)
                        mViewModel.deleteOneRecord(text)
                        mViewModel.getRecordByUID()
                    },
                    onSearch = { content ->
                        content?.let {
                            updateSearchKey(content)
                            clearSearchRecommend()
                            navigateTo(SearchNavigationEvent.ToSearchResultBase(content))
                        }
                    }
                )
                hfList.setAdapter(historyAdapter)

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
                    historyAdapter?.deleteAllData()
                    deleteAllData()
                    getRecordByUID()
                    setHistoryButton()
                }
            }
        }
    }

    private fun setHotWords() {
        with(mBinding) {
            //热门搜索
            rvHotWord.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = hotWordAdapter.apply {
                    if (itemDecorationCount == 0) {
                        addItemDecoration(object : ItemDecoration() {
                            private val dividerHeight = 0.5f.dp2px
                            private val paint = Paint().apply {
                                color = SkinnableResourceManager.getColor(
                                    context,
                                    R.color.search_divider
                                )
                                strokeWidth = dividerHeight.toFloat()
                            }

                            override fun onDraw(
                                canvas: Canvas,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                val spanCount = 2
                                val itemCount = parent.adapter?.itemCount ?: 0
                                val totalRowCount = (itemCount + spanCount - 1) / spanCount

                                for (i in 0 until parent.childCount) {
                                    val child = parent.getChildAt(i)
                                    val position = parent.getChildAdapterPosition(child)
                                    val currentRow = position / spanCount
                                    val isLastRow = currentRow == totalRowCount - 1

                                    if (!isLastRow) {
                                        val left = child.left.toFloat()
                                        val right = child.right.toFloat()
                                        val y = child.bottom.toFloat()
                                        canvas.drawLine(left, y, right, y, paint)
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun setHistoryButton() {
        with(mBinding) {
            isEditor = !isEditor
            historyAdapter?.isDelete = isEditor
            hfList.setEditor(isEditor)
            if (isEditor) {
                ivClickShowDelete.visibility = View.GONE
                llShowCompleted.visibility = View.VISIBLE
            } else {
                ivClickShowDelete.visibility = View.VISIBLE
                llShowCompleted.visibility = View.GONE
            }
        }
    }

    private fun navigateTo(event: SearchNavigationEvent) {
        sharedViewModel.setNavigationEvent(event)
    }

    private fun updateSearchKey(key: String) {
        sharedViewModel.setSearchKeyWord(key)
    }

    private fun clearSearchRecommend() {
        sharedViewModel.clearSearchRecommendList()
    }

    fun notifyUpdateRecordList(word: String) {
        historyAdapter?.addData(word)
    }
}