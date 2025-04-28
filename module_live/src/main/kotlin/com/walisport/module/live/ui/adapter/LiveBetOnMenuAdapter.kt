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
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.databinding.AdapterLiveBetMenuLayoutBinding
import galaxy.common.proto.Common
import org.koin.core.component.getScopeName
import kotlin.math.ceil
import kotlin.math.min


class LiveBetOnMenuAdapter(compare: DiffUtil.ItemCallback<Common.MarketType>) :
    BaseAdapter<Common.MarketType, LiveBetOnMenuAdapter.LiveBetOnMenuViewHolder, ViewBinding>(
        compare
    ) {
    private val SPAN_COUNT: Int = 3

    inner class LiveBetOnMenuViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetMenuLayoutBinding =
            binding as AdapterLiveBetMenuLayoutBinding

        init {
            setOnClickListener()
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setOnClickListener() {

        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateItem(position: Int) {
            if (position == itemCount) {
                viewBinding.VLin.visibility = View.GONE
            }
            val item :Common.MarketType = getItem(position)
            viewBinding.tvName.text = item.name
            viewBinding.rvContent.apply {
                isNestedScrollingEnabled = false
                itemAnimator = null
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
                adapter = LiveBetMenuContentAdapter(object : DiffUtil.ItemCallback<Common.MarketBase>() {
                    override fun areItemsTheSame(oldItem: Common.MarketBase, newItem: Common.MarketBase): Boolean {
                        return oldItem == newItem
                    }

                    override fun areContentsTheSame(oldItem: Common.MarketBase, newItem: Common.MarketBase): Boolean {
                        return oldItem == newItem
                    }
                }).apply {
                    post {
                        (layoutManager as GridLayoutManager).spanSizeLookup =
                            object : SpanSizeLookup() {
                                override fun getSpanSize(position: Int): Int {
                                    val tag: String = item.marketBaseList[position].marketName
                                    val paint = TextPaint()
                                    paint.textSize =  14 * resources.displayMetrics.scaledDensity // 字体大小 14sp
                                    val textWidth = paint.measureText(tag) + 8.dp2px * resources.displayMetrics.scaledDensity // 加上 padding
                                    val columnWidth: Int = width / SPAN_COUNT // 每列宽度（3列）
                                    val spanCount = ceil((textWidth / columnWidth).toDouble()).toInt() // 计算需要的列数
                                    return min(spanCount.toDouble(), 3.0).toInt() // 最多占3列
                                }
                            }
                        adapter?.notifyDataSetChanged() // 刷新布局
                        val decoration = GridSpacingItemDecoration()
                        addItemDecoration(decoration)
                        submitList(item.marketBaseList)
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