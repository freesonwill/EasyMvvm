package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.walisport.module.live.data.constants.StatesArrange
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding
import com.walisport.module.live.ui.widget.LiveBetListLayout

class LiveBetOnAdapter(var callback: LivBetListCallback) :
    BaseAdapter<MarketMenuBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        ItemDiffCallback()
    ) {
    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""
    private lateinit var map: Map<Long, List<LiveSelectionBean>>
    private var isNotify = false
    inner class LiveBetOnViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetItemLayoutBinding =
            binding as AdapterLiveBetItemLayoutBinding

        init {
            setOnClickListener()
        }
        private fun setOnClickListener() {

        }

        fun updateItem(position: Int) {
            val item = getItem(position)
            viewBinding.tvBetName.text = item.marketName
            var lists = map[item.marketId]
            viewBinding.lbBet.removeAllViews()
            viewBinding.lbBet.viewInit()

            lists?.withIndex()?.forEach { (index, listIt) ->
                if (position==0||listIt.style == StatesArrange.BO_DIAN.code){
                        viewBinding.clBet.visibility = View.VISIBLE
                        viewBinding.awayName.text = awayName
                        viewBinding.homeName.text = homeName
                        Glide.with(viewBinding.roots).load(homeLogo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                            .into(viewBinding.homeLogo)
                        Glide.with(viewBinding.roots).load(awayLogo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                            .into(viewBinding.awayLogo)
                        viewBinding.andName.visibility =
                            if (lists[0].style == StatesArrange.BO_DIAN.code) View.VISIBLE else View.GONE
                    } else {
                        viewBinding.clBet.visibility = View.GONE
                    }
                viewBinding.lbBet.submitList(
                    StatesArrange.getStates(listIt.style),
                    index,
                    listIt.shortName,
                    listIt.odds, listIt.selectionId, listIt.active,listIt.oddsStatus,isNotify
                ) { it ->
                    callback.itemListCallback(it, listIt.selectionId)
                }
            }
        }
    }

    fun setHomeAway(
        homeName: String,
        homeLogo: String,
        awayName: String,
        awayLogo: String,
        map: Map<Long, List<LiveSelectionBean>>,
        isNotify : Boolean
    ) {
        this.homeName = homeName
        this.homeLogo = homeLogo
        this.awayName = awayName
        this.awayLogo = awayLogo
        this.map = map
        this.isNotify = isNotify
    }


    fun setIsNotify( isNotify : Boolean){
        this.isNotify = isNotify
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

interface LivBetListCallback {
    fun itemListCallback(marketI: Long, selectionId: Long)
}

class ItemDiffCallback : DiffUtil.ItemCallback<MarketMenuBean>() {
    override fun areItemsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return false
    }
}