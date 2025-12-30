package com.walisport.module.search.ui.fragment

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchRecommendListBinding
import com.walisport.module.search.ui.adapter.RecommendAdapter
import com.walisport.module.search.ui.viewmodel.SearchRecommendError
import com.walisport.module.search.ui.viewmodel.SearchRecommendListViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC
class SearchRecommendListFragment : BaseFragment<SearchRecommendListViewModel, FragmentSearchRecommendListBinding>() {
    override val vbClass: KClass<FragmentSearchRecommendListBinding>
        get() = FragmentSearchRecommendListBinding::class
    override val vmClass: KClass<SearchRecommendListViewModel>
        get() = SearchRecommendListViewModel::class

    private val recommendAdapter by lazy { RecommendAdapter() }
    private var currentKeyword: String? = null

    var onClickListener: ((String) -> Unit?)?
        get() = recommendAdapter.onClick
        set(value) {
            recommendAdapter.onClick = value
        }
    var onDismissListener: (() -> Unit?)? = null

    override fun initView(savedInstanceState: Bundle?) {
        setRecommend()
        setBackPressHandler()
        mBinding.root.touchBackPressed()
    }

    override fun initListener() =  Unit

    override suspend fun createObserver() {
        with(mViewModel) {
            launch(Lifecycle.State.RESUMED) {
                // 觀察搜索推薦列表
                launch {
                    searchRecommendList.collect { list ->
                        recommendAdapter.submitList(list)
                        // 僅在有關鍵字且無結果時顯示空狀態
                        updateEmptyState(list.isEmpty() && !currentKeyword.isNullOrBlank())
                    }
                }
                
                // 觀察錯誤狀態
                launch {
                    errorState.collect { error ->
                        when (error) {
                            is SearchRecommendError.Timeout -> {
                                showToast(
                                    SkinnableResourceManager.getString(
                                        requireContext(),
                                        R.string.search_timeout
                                    )
                                )
                            }
                            is SearchRecommendError.General -> {
                                showToast(error.error.msg)
                            }
                            is SearchRecommendError.None -> {
                                // 無錯誤，不處理
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateKeyword(keyword: String?) {
        currentKeyword = keyword
        mViewModel.getSearchRecommendList(keyword)
        recommendAdapter.updateMatchKeyword(keyword)
    }
    
    /**
     * 更新空狀態顯示
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        with(mBinding) {
            if (isEmpty) {
                // 關鍵字有輸入但沒有任何推薦詞時，顯示「暫無搜索數據」文案
                rvSearchRecommend.visibility = View.GONE
                clEmptyState.visibility = View.VISIBLE
            } else {
                // 顯示列表，隱藏空狀態
                rvSearchRecommend.visibility = View.VISIBLE
                clEmptyState.visibility = View.GONE
            }
        }
    }

    fun show() {
        mBinding.clSearchRecommend.visibility = View.VISIBLE
    }

    fun dismiss() {
        resetSearchRecommend()
    }

    private fun setRecommend() {
        with(mBinding) {
            // 搜索自动补充词汇
            rvSearchRecommend.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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

                            override fun getItemOffsets(
                                outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                super.getItemOffsets(outRect, view, parent, state)
                                val position = parent.getChildAdapterPosition(view)
                                outRect.top = if (position == 0) -resources.getDimension(R.dimen.search_recommend_item_padding_vertical).toInt() else 0
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
                itemAnimator = null
            }

            clSearchRecommend.setOnClickListener {
                resetSearchRecommend()
            }
        }
    }

    private fun resetSearchRecommend() {
        onDismissListener?.invoke()
        mBinding.clSearchRecommend.visibility = View.GONE
        currentKeyword = null
        mViewModel.clearSearchRecommendList()
    }

    private fun setBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (mBinding.clSearchRecommend.isVisible) {
                resetSearchRecommend()
            }

            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        const val TAG = "SearchRecommendListFragment"
    }
}