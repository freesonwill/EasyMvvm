package com.walisport.module.live.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.databinding.AdapterLiveBetMenuLayoutBinding
import kotlin.math.ceil
import kotlin.math.min


class LiveBetOnMenuAdapter(compare: DiffUtil.ItemCallback<String>) :
    BaseAdapter<String, LiveBetOnMenuAdapter.LiveBetOnMenuViewHolder, ViewBinding>(
        compare
    ) {
    private val SPAN_COUNT: Int = 3

    //模拟数据
    private var list: List<String> = listOf(
        "全场让球",
        "角球大小",
        "罚牌大小",
        "谁先开球&谁先进球",
        "谁先进球",
        "先进球",
        "先进球",
        "先进球"
    )

    inner class LiveBetOnMenuViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetMenuLayoutBinding =
            binding as AdapterLiveBetMenuLayoutBinding

        init {
            setOnClickListener()
        }

        private fun setOnClickListener() {

        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateItem(position: Int) {
            if (position == itemCount) {
                viewBinding.VLin.visibility = View.GONE
            }
            val item = getItem(position)
            viewBinding.tvName.text = item
            viewBinding.rvContent.apply {
                itemAnimator = null
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
                adapter = LiveBetMenuContentAdapter(object : DiffUtil.ItemCallback<String>() {
                    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                        return oldItem == newItem
                    }

                    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                        return oldItem == newItem
                    }
                }).apply {
                    post {
                        (layoutManager as GridLayoutManager).spanSizeLookup =
                            object : SpanSizeLookup() {
                                override fun getSpanSize(position: Int): Int {
                                    val tag: String = list[position]
                                    val paint = TextPaint()
                                    paint.textSize =
                                        14 * resources.displayMetrics.scaledDensity // 字体大小 14sp
                                    val textWidth =
                                        paint.measureText(tag) + 8.dp2px * resources.displayMetrics.scaledDensity // 加上 padding
                                    val columnWidth: Int = width / SPAN_COUNT // 每列宽度（3列）
                                    val spanCount =
                                        ceil((textWidth / columnWidth).toDouble()).toInt() // 计算需要的列数
                                    return min(spanCount.toDouble(), 3.0).toInt() // 最多占3列
                                }
                            }
                        adapter?.notifyDataSetChanged() // 刷新布局
                        val decoration = GridSpacingItemDecoration()
                        addItemDecoration(decoration)
                        submitList(list)
                    }
                }
            }
        }
    }

    class GridSpacingItemDecoration(

    ) : ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            outRect.top = 8.dp2px

        }
    }

    override fun convertPlus(holder: LiveBetOnMenuViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        val binding = AdapterLiveBetMenuLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveBetOnMenuViewHolder {
        val holder = LiveBetOnMenuViewHolder(binding)
        return holder
    }
}