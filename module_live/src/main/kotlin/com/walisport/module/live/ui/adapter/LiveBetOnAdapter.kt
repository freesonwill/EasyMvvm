package com.walisport.module.live.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.StatesArrange
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveBetOnAdapter(var callback: LivBetListCallback) :
    BaseAdapter<LiveMarketListBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        LiveMarketListBeanDiffCallback()
    ) {
    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""
    private var selectionComboId: Long? = null
    private var beforePosition: Int = 0
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
            viewBinding.lbBet.viewInit()
            item.list.withIndex().forEach { (index, listIt) ->
                if (position == 0 || listIt.style == StatesArrange.BO_DIAN.code) {
                    viewBinding.clBet.visibility = View.VISIBLE
                    viewBinding.awayName.text = awayName
                    viewBinding.homeName.text = homeName
                    loadLogoImage(viewBinding.roots, viewBinding.homeLogo, homeLogo)
                    loadLogoImage(viewBinding.roots, viewBinding.awayLogo, awayLogo)
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
                if (isCombo) {
                    beforePosition = position
                }
                var status =
                    notifySelectionsId?.find { it.selectionId == listIt.selectionId }?.selectionId
                        ?: 0L
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
                    callback.itemListCallback(
                        it, listIt.selectionId, x, y, position, beforePosition
                    )
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

    fun setSelectionComboId(selectionComboId: Long?, isNotify: Boolean = true) {
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
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        val binding = AdapterLiveBetItemLayoutBinding.inflate(inflater, parent, false)
        return binding
    }

    override fun createViewHolder(
        binding: ViewBinding, viewType: Int
    ): LiveBetOnViewHolder {
        val holder = LiveBetOnViewHolder(binding)
        return holder
    }
}

fun loadLogoImage(context: View, imageView: ImageView, url: String?) {
    val requestOptions = RequestOptions().override(20.dp2px, 20.dp2px) // 指定宽高
        .format(DecodeFormat.PREFER_RGB_565)
    Glide.with(context).load(url).apply(requestOptions).thumbnail(0.5f).into(imageView)
}

interface LivBetListCallback {
    fun itemListCallback(
        marketI: Long, selectionId: Long, x: Float, y: Float, position: Int, beforePosition: Int
    )
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
        return oldList.size == newList.size
    }
}