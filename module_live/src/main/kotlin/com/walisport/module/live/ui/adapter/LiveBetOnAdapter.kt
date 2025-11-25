package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.data.model.LiveMarketListBean
import com.walisport.module.live.data.model.LiveMarketSelectionBean
import arch.cayenne.lib.database.entity.SelectionsEdit
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.walisport.module.live.data.LiveOddsStatusEnum
import com.walisport.module.live.data.constants.StatesArrange
import com.walisport.module.live.databinding.AdapterLiveBetItemLayoutBinding
import java.lang.ref.WeakReference

class LiveBetOnAdapter(var callback: LivBetListCallback) :
    BaseAdapter<LiveMarketListBean, LiveBetOnAdapter.LiveBetOnViewHolder, ViewBinding>(
        LiveMarketListBeanDiffCallback()
    ) {
    private var homeName: String? = ""
    private var homeLogo: String? = ""
    private var awayName: String? = ""
    private var awayLogo: String? = ""
    private var notifySelectionsId: List<SelectionsEdit>? = null

    inner class LiveBetOnViewHolder(binding: ViewBinding) : BaseViewHolder(binding) {
        private val viewBinding: AdapterLiveBetItemLayoutBinding = binding as AdapterLiveBetItemLayoutBinding


        fun updateItem(position: Int) {
            val item = getItem(position)
            viewBinding.tvBetName.text = item.marketName
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
                val status =
                    notifySelectionsId?.find { it.selectionId == listIt.selectionId }?.selectionId
                        ?: 0L
                //  LogUtils.dTag("比赛推送","status----${status}---oddsStatus${listIt.oddsStatus},---isNotify${isNotify}--notifySelectionsId${notifySelectionsId}")
                val name =
                    if (listIt.style == StatesArrange.BO_DIAN.code) listIt.name else listIt.shortName
                viewBinding.lbBet.submitList(
                    StatesArrange.getStates(listIt.style),
                    index,
                    name,
                    listIt.odds,
                    listIt.selectionId,
                    listIt.active,
                    if (status == 0L) LiveOddsStatusEnum.SAME.status else listIt.oddsStatus,
                    isSelected = listIt.isSelect
                ) { v, it, x, y ->
                    callback.itemListCallback(
                        v, it, listIt.selectionId, x, y,
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
        notifySelectionsId: List<SelectionsEdit>?
    ) {
        this.notifySelectionsId = emptyList()
        this.homeName = homeName
        this.homeLogo = homeLogo
        this.awayName = awayName
        this.awayLogo = awayLogo
        this.notifySelectionsId = notifySelectionsId
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
        cell: WeakReference<View>, marketI: Long, selectionId: Long, x: Float, y: Float
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
        return oldList == newList
    }
}