package com.walisport.module.live.ui.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.MarketMenuBean
import com.bumptech.glide.Glide
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding

class LiveBetOnAdapter(compare: DiffUtil.ItemCallback<MarketMenuBean>) :
    BaseAdapter<MarketMenuBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        compare
    ) {
    //注区内容 模拟数据
    private var list: List<String> = listOf("-0/0.5", "-0/0.5", "0", "0", "0.5", "+0.5")

    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""

    inner class LiveBetOnViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetItemLayoutBinding =
            binding as AdapterLiveBetItemLayoutBinding

        init {
            setOnClickListener()
        }

        private fun setOnClickListener() {

        }

        fun updateItem(position: Int) {
            if (position != 0) {
                viewBinding.clBet.visibility = View.GONE
            }
            viewBinding.awayName.text = awayName
            viewBinding.homeName.text = homeName
            Glide.with(viewBinding.roots).load(homeLogo).into(viewBinding.awayLogo)
            Glide.with(viewBinding.roots).load(awayLogo).into(viewBinding.homeLogo)
            val item = getItem(position)
            viewBinding.tvBetName.text = item.marketName
            viewBinding.rvBet.apply {
                itemAnimator = null
                layoutManager = GridLayoutManager(context, 2)
                adapter = LiveBetContentAdapter(object : DiffUtil.ItemCallback<String>() {
                    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                        return oldItem == newItem
                    }

                    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                        return oldItem == newItem
                    }
                }).apply {
                    post {
                        val decoration = GridSpacingItemDecoration(2, 7.dp2px, false)
                        addItemDecoration(decoration)
                        submitList(list)
                    }
                }
            }

        }
    }

    fun setHomeAway(
        homeName: String, homeLogo: String, awayName: String, awayLogo: String
    ){
        this.homeName = homeName
        this.homeLogo = homeLogo
        this.awayName = awayName
        this.awayLogo = awayLogo
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int, // 列数
        private val spacing: Int,   // 间距大小（像素）
        private val includeEdge: Boolean // 是否包含外侧边距
    ) : ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val column = position % spanCount // 当前列数

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount

                if (position < spanCount) { // 第一行
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }

    override fun convertPlus(holder: LiveBetOnViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        val binding = AdapterLiveBetItemLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): LiveBetOnViewHolder {
        val holder = LiveBetOnViewHolder(binding)
        return holder
    }
}