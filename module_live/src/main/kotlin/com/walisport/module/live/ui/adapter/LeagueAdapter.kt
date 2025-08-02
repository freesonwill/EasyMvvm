package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.compare.LeagueMatchCompare
import com.walisport.module.live.data.model.MatchBean
import com.walisport.module.live.databinding.ItemLeagueBinding
import com.walisport.module.live.databinding.ItemWeekBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeagueAdapter : BaseAdapter<MatchBean, BaseViewHolder, ViewBinding>(
    LeagueMatchCompare()
) {
    private var listener: OnItemClickListener? = null
    private var matchID = 0L;

    companion object {
        const val TYPE_WEEK = 0
        const val TYPE_ITEM = 1
    }

    //设置当前比赛ID，列表中如果存在当前比赛则显示深一点的背景
    fun setMatchID(matchID: Long) {
        this.matchID = matchID
    }

    override fun convertPlus(
        holder: BaseViewHolder, binding: ViewBinding, position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemLeagueBinding) {
            binding.tvHomeName.text = item.homeName
            binding.tvAwayName.text = item.awayName
            binding.tvTime.text = convertStampToStr(item.startTime, item.isToday)
            binding.itemRoot.setOnClickListener {
                listener?.onItemClick(position)
            }
            if (item.matchId == matchID) {
                binding.itemRoot.background = R.drawable.shape_bg_item_league_dark.getDrawable()
            } else {
                binding.itemRoot.background = R.drawable.shape_bg_item_league.getDrawable()
            }
            Glide.with(binding.root).load(item.homeLogo).error(R.drawable.icon_error_logo_big)
                .placeholder(R.drawable.icon_error_logo_big).into(binding.ivHomeLogo)
            Glide.with(binding.root).load(item.awayLogo).error(R.drawable.icon_error_logo_big)
                .placeholder(R.drawable.icon_error_logo_big).into(binding.ivAwayLogo)
        } else if (binding is ItemWeekBinding) {
            binding.tvLeagueWeek.text = item.weekDay
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater, parent: ViewGroup, viewType: Int
    ): ViewBinding {
        if (viewType == TYPE_WEEK) {
            return ItemWeekBinding.inflate(inflater, parent, false)
        }
        return ItemLeagueBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isWeekHeader(position)) TYPE_WEEK else TYPE_ITEM
    }

    private fun isWeekHeader(position: Int): Boolean {
        val item = getItem(position)
        return item.isWeekHead
    }

    private fun convertStampToStr(timeStamp: Long, isToday: Boolean): String {
        val date = Date(timeStamp)
        var str = ""
        if (isToday) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            str = format.format(date)
        } else {
            val format = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            str = format.format(date)
        }
        return str
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }

    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}