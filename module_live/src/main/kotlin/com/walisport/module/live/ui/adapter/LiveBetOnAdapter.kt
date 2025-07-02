package com.walisport.module.live.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.LiveMarketListBean
import arch.cayenne.lib.database.entity.LiveMarketSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.SelectionsEdit
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.StatesArrange
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding

class LiveBetOnAdapter(var callback: LivBetListCallback) :
    BaseAdapter<LiveMarketListBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        LiveMarketListBeanDiffCallback()
    ) {
    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""
    private var selectionComboId: Long? = null
    private var beforePosition:Int = -1
    private var isNotify = false
    private var notifySelectionsId: List<SelectionsEdit>? = null

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
            viewBinding.lbBet.removeAllViews()
            viewBinding.lbBet.viewInit()
            item.list.withIndex().forEach { (index, listIt) ->
                if (position == 0 || listIt.style == StatesArrange.BO_DIAN.code) {
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
                        if (listIt.style == StatesArrange.BO_DIAN.code) View.VISIBLE else View.GONE
                } else {
                    viewBinding.clBet.visibility = View.GONE
                }
                var isCombo: Boolean = if (selectionComboId == null) {
                    false
                } else if (selectionComboId == listIt.selectionId) {
                    true
                } else {
                    false
                }
                if (isCombo){
                    beforePosition = position
                }
                var status = notifySelectionsId?.find { it.selectionId == listIt.selectionId }?.selectionId ?: 0L
              //  LogUtils.dTag("比赛推送","status----${status}---oddsStatus${listIt.oddsStatus},---isNotify${isNotify}--notifySelectionsId${notifySelectionsId}")
                var name =
                    if (listIt.style == StatesArrange.BO_DIAN.code) listIt.name else listIt.shortName
                viewBinding.lbBet.submitList(
                    StatesArrange.getStates(listIt.style),
                    index,
                    name,
                    listIt.odds,
                    listIt.selectionId,
                    listIt.active,
                    if (status == 0L) LiveOddsStatusEnum.SAME.status else listIt.oddsStatus,
                    isNotify,
                    isCombo,
                ) { it, x, y ->
                    callback.itemListCallback(it, listIt.selectionId, x, y,position,beforePosition)
                }
            }
        }
    }

    fun setData(
        homeName: String,
        homeLogo: String,
        awayName: String,
        awayLogo: String,
        isNotify: Boolean,
        notifySelectionsId: List<SelectionsEdit>?
    ) {
        this.notifySelectionsId = emptyList()
        this.homeName = homeName
        this.homeLogo = homeLogo
        this.awayName = awayName
        this.awayLogo = awayLogo
        this.isNotify = isNotify
        this.notifySelectionsId = notifySelectionsId
    }

    fun setSelectionComboId(selectionComboId: Long?,isNotify: Boolean = true) {
        this.isNotify = isNotify
        this.selectionComboId = selectionComboId
    }

    fun getBeforePosition():Int{
        return beforePosition
    }

    override fun convertPlus(holder: LiveBetOnViewHolder, binding: ViewBinding, position: Int) {
        holder.updateItem(position)
    }
    override fun onBindViewHolder(
        holder: LiveBetOnViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            holder.updateItem(position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
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
    fun itemListCallback(marketI: Long, selectionId: Long, x: Float, y: Float,position:Int,beforePosition:Int)
}

class LiveMarketListBeanDiffCallback : DiffUtil.ItemCallback<LiveMarketListBean>() {

    override fun areItemsTheSame(oldItem: LiveMarketListBean, newItem: LiveMarketListBean): Boolean {
        return oldItem.marketId == newItem.marketId
    }

    override fun areContentsTheSame(oldItem: LiveMarketListBean, newItem: LiveMarketListBean): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: LiveMarketListBean, newItem: LiveMarketListBean): Any? {
        return areListsEqual(oldItem.list,newItem.list)
    }

    private fun areListsEqual(
        oldList: List<LiveMarketSelectionBean>,
        newList: List<LiveMarketSelectionBean>
    ): Boolean {
        if (oldList.size != newList.size) return false
        return oldList.zip(newList).all { (old, new) ->
            old.code == new.code &&
                    old.selectionId == new.selectionId &&
                    old.name == new.name &&
                    old.shortName == new.shortName &&
                    old.odds == new.odds &&
                    old.active == new.active &&
                    old.parlay == new.parlay &&
                    old.marketId == new.marketId &&
                    old.marketName == new.marketName &&
                    old.style == new.style &&
                    old.oddsStatus == new.oddsStatus
        }
    }
}