package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.SelectionsEdit
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.StatesArrange
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding

class LiveBetOnAdapter(var callback: LivBetListCallback) :
    BaseAdapter<MarketMenuBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        ItemDiffCallback()
    ) {
    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""
    private var selectionComboId: Long? = null
    private var beforePosition:Int = 0
    private lateinit var map: Map<Long, List<LiveSelectionBean>>
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
            var lists = map[item.marketId]
            viewBinding.lbBet.removeAllViews()
            viewBinding.lbBet.viewInit()
            lists?.withIndex()?.forEach { (index, listIt) ->
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
                        if (lists[0].style == StatesArrange.BO_DIAN.code) View.VISIBLE else View.GONE
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
        map: Map<Long, List<LiveSelectionBean>>,
        isNotify: Boolean,
        notifySelectionsId: List<SelectionsEdit>?
    ) {
        this.notifySelectionsId = emptyList()
        this.homeName = homeName
        this.homeLogo = homeLogo
        this.awayName = awayName
        this.awayLogo = awayLogo
        this.map = map
        this.isNotify = isNotify
        this.notifySelectionsId = notifySelectionsId
    }

    fun setSelectionComboId(selectionComboId: Long?,isNotify: Boolean = true) {
        this.isNotify = isNotify
        this.selectionComboId = selectionComboId
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
    fun itemListCallback(marketI: Long, selectionId: Long, x: Float, y: Float,position:Int,beforePosition:Int)
}

class ItemDiffCallback : DiffUtil.ItemCallback<MarketMenuBean>() {
    override fun areItemsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return oldItem.marketId == newItem.marketId
    }

    override fun areContentsTheSame(oldItem: MarketMenuBean, newItem: MarketMenuBean): Boolean {
        return oldItem == newItem
    }
}