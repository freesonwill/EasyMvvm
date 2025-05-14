package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import com.bumptech.glide.Glide
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding
import com.walisport.module.live.ui.widget.LiveBetListLayout

class LiveBetOnAdapter(var callback:LivBetListCallback) :
    BaseAdapter<MarketMenuBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        ItemDiffCallback()
    ) {
    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""
    private lateinit var map: Map<Long, List<LiveSelectionBean>>

    inner class LiveBetOnViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetItemLayoutBinding =
            binding as AdapterLiveBetItemLayoutBinding

        init {
            setOnClickListener()
        }

        private fun setOnClickListener() {

        }

        fun updateItem(position: Int) {
            if (position == 0) {
                viewBinding.clBet.visibility = View.VISIBLE
                viewBinding.awayName.text = awayName
                viewBinding.homeName.text = homeName
                Glide.with(viewBinding.roots).load(homeLogo).into(viewBinding.homeLogo)
                Glide.with(viewBinding.roots).load(awayLogo).into(viewBinding.awayLogo)
            } else {
                viewBinding.clBet.visibility = View.GONE
            }

            val item = getItem(position)
            viewBinding.tvBetName.text = item.marketName
            var positions = 0
            var lists = map[item.marketId]
            viewBinding.lbBet.removeAllViews()
            viewBinding.lbBet.viewInit()
            lists?.withIndex()?.forEach { (index,listIt) ->
                viewBinding.lbBet.submitList(
                    LiveBetListLayout.StatesArrange.getStates(listIt.style),
                    positions,
                    listIt.shortName,
                    listIt.odds.getOdds().toString(),listIt.selectionId,listIt.active,
                ) { it ->
                    callback.itemListCallback(it,listIt.selectionId)
                }
                positions++
            }
        }
    }

    fun setHomeAway(
        homeName: String,
        homeLogo: String,
        awayName: String,
        awayLogo: String,
        map: Map<Long, List<LiveSelectionBean>>
    ) {
        this.homeName = homeName
        this.homeLogo = homeLogo
        this.awayName = awayName
        this.awayLogo = awayLogo
        this.map = map
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

interface LivBetListCallback{
      fun itemListCallback(marketI:Long,selectionId: Long)
}

class ItemDiffCallback : DiffUtil.ItemCallback<MarketMenuBean>() {
    override fun areItemsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return false
    }
}