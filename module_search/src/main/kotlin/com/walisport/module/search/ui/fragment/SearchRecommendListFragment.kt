package com.walisport.module.search.ui.fragment

import android.graphics.Canvas
import android.graphics.Paint
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
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchRecommendListBinding
import com.walisport.module.search.ui.adapter.RecommendAdapter
import com.walisport.module.search.ui.viewmodel.SearchBaseViewModel
import com.walisport.module.search.ui.viewmodel.SearchRecommendListViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class SearchRecommendListFragment : BaseFragment<SearchRecommendListViewModel, FragmentSearchRecommendListBinding>() {
    override val vbClass: KClass<FragmentSearchRecommendListBinding>
        get() = FragmentSearchRecommendListBinding::class
    override val vmClass: KClass<SearchRecommendListViewModel>
        get() = SearchRecommendListViewModel::class

    private val sharedViewModel: SearchBaseViewModel by sharedViewModel<SearchBaseViewModel, SearchFragment>()
    private val recommendAdapter by lazy { RecommendAdapter() }

    var onClickListener: ((String) -> Unit?)?
        get() = recommendAdapter.onClick
        set(value) {
            recommendAdapter.onClick = value
        }
    var onDismissListener: (() -> Unit?)? = null

    override fun initView(savedInstanceState: Bundle?) {
        setRecommend()
        setBackPressHandler()
    }

    override fun initListener() =  Unit

    override fun createObserver() {
        with(sharedViewModel) {
            launch(Lifecycle.State.RESUMED) {
                launch {
                    searchRecommendList.collect { list ->
                        recommendAdapter.submitList(list) {
                            mBinding.clSearchRecommend.visibility =
                                if (list.isNotEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                }
            }
        }
    }

    fun updateKeyword(keyword: String?) {
        recommendAdapter.updateMatchKeyword(keyword)
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
        sharedViewModel.clearSearchRecommendList()
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