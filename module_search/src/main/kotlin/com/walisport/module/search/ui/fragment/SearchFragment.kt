package com.walisport.module.search.ui.fragment

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.ClearableEditText
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchBinding
import com.walisport.module.search.ui.adapter.HotWordAdapter
import com.walisport.module.search.ui.adapter.RecommendAdapter
import com.walisport.module.search.ui.adapter.SearchHistoryAdapter
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as Rc

/**
 * @author: caomei
 * @date: 2025/4/24 16:31
 * @description:搜索
 */
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
    override val vbClass: KClass<FragmentSearchBinding>
        get() = FragmentSearchBinding::class
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class

    private var historyAdapter: SearchHistoryAdapter? = null
    private var isEditor: Boolean = false

    private val hotWordAdapter by lazy {
        HotWordAdapter { hotWord ->
            mViewModel.getSearchResult(hotWord)
            updateSearchText(hotWord)
            addSearchHistory(hotWord)
        }
    }

    private val recommendAdapter by lazy {
        RecommendAdapter()
    }
    private var canSearch: Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            with(mViewModel) {
                //设置标题
                titleBar.loadSearchTitleBar(
                    hint = getString(R.string.please_input_content),
                    afterTextChanged = { text, binding ->
                        if (!canSearch) return@loadSearchTitleBar

                        val count = text?.length ?: 0
                        val color = if (count > 0) Rc.color.search_btn
                        else Rc.color.search_btn_normal
                        binding.tvSearchText.setTextColor(color.getColor())

                        // 搜索自动补充词汇
                        if (recommendAdapter.onClick == null) {
                            recommendAdapter.setOnClickListener { recommendWord ->
                                updateSearchText(recommendWord) {
                                    mViewModel.getSearchResult(recommendWord)
                                    clearSearchRecommend()
                                }
                            }
                        }
                        with(text?.toString()) {
                            recommendAdapter.updateMatchKeyword(this)
                            getSearchRecommend(this)
                        }
                    },
                    onSearch = { content, _ ->
                        if (TextUtils.isEmpty(content)) {
                            showToast(getString(R.string.please_input_content))
                            return@loadSearchTitleBar
                        }
                        addSearchHistory(content)
                        getSearchResult(content)
                        clearSearchRecommend()
                        getRecordByUID()
                    }
                )

                //设置搜索历史
                historyAdapter = SearchHistoryAdapter(
                    closeAction = { position, text ->
                        historyAdapter?.deleteData(position)
                        mViewModel.deleteOneRecord(text)
                        mViewModel.getRecordByUID()
                    },
                    onSearch = { content ->
                        content?.let {
                            mViewModel.getSearchResult(content)
                            clearSearchRecommend()
                            updateSearchText(content)
                        }
                    }
                )
                hfList.setAdapter(historyAdapter)

                //删除图标，点击进入删除模式
                ivClickShowDelete.clickNoRepeat {
                    setButton()
                }

                //完成按钮
                txtCompletedAll.clickNoRepeat {
                    setButton()
                }

                //全部删除按钮
                txtDeleteAll.clickNoRepeat {
                    historyAdapter?.deleteAllData()
                    deleteAllData()
                    getRecordByUID()
                    setButton()
                }

                // 搜索自动补充词汇
                rvSearchRecommend.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    adapter = recommendAdapter.apply {
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
                                    val itemCount = parent.adapter?.itemCount ?: 0

                                    for (i in 0 until parent.childCount) {
                                        val child = parent.getChildAt(i)
                                        val position = parent.getChildAdapterPosition(child)

                                        // 如果不是最後一個 item，就畫底部分隔線
                                        if (position < itemCount - 1) {
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
    }

    override fun initData() {
        super.initData()
        //获取搜索记录
        mViewModel.getRecordByUID()

        //获取热门搜索
        mViewModel.getSearchHotWord()
    }

    private fun setButton() {
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

    private fun addSearchHistory(word: String) {
        historyAdapter?.addData(word)
        mViewModel.addOneRecord(word)
    }

    private fun clearSearchRecommend() {
        mBinding.clSearchRecommend.visibility = View.GONE
        mViewModel.clearSearchRecommend()
    }

    private fun updateSearchText(word: String, afterChange: (() -> Unit)? = null) {
        canSearch = false
        mBinding.titleBar.findViewById<ClearableEditText>(arch.cayenne.lib.common.R.id.ce_search)
            .apply {
                setText(word)
                setSelection(word.length)
            }
        afterChange?.invoke()
        canSearch = true
    }

    override fun initListener() {

    }

    override fun createObserver() {
        with(mBinding) {
            with(mViewModel) {
                searchRecord.observe(viewLifecycleOwner) {
                    historyAdapter?.setNewData(it.toMutableList())
                    clHistory.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }
                searchResult.observe(viewLifecycleOwner) { result ->
                    //TODO
                    println(result)
                }
                searchRecommend.observe(viewLifecycleOwner) {
                    recommendAdapter.submitList(it)
                    clSearchRecommend.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }
                searchHotWord.observe(viewLifecycleOwner) {
                    hotWordAdapter.submitList(it)
                    clHotWord.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }
}