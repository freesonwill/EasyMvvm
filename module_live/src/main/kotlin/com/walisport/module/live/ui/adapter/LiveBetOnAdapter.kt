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

class LiveBetOnAdapter() :
    BaseAdapter<MarketMenuBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        ItemDiffCallback()
    ) {
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
class ItemDiffCallback : DiffUtil.ItemCallback<MarketMenuBean>() {
    override fun areItemsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return oldItem.marketName == newItem.marketName
    }

    override fun areContentsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return oldItem == newItem
    }
}