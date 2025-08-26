package com.walisport.module.search.ui.fragment

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchBinding
import com.walisport.module.search.ui.adapter.HotWordAdapter
import com.walisport.module.search.ui.adapter.SearchHistoryAdapter
import com.walisport.module.search.ui.view.FlowAdapter
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlin.reflect.KClass

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
        //获取搜索记录
        mViewModel.getRecordByUID()

        //获取热门搜索
        mViewModel.getSearchHotWord { error ->
            error?.let { showToast(it.msg) }
        }
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
}