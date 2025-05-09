package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
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
        holder: BaseViewHolder, mBinding: ViewBinding, position: Int
    ) {
        val item = getItem(position)
        if (mBinding is ItemLeagueBinding) {
            mBinding.tvHomeName.text = item.homeName
            mBinding.tvAwayName.text = item.awayName
            mBinding.tvTime.text = convertStampToStr(item.startTime)
            mBinding.itemRoot.setOnClickListener {
                listener?.onItemClick(position)
            }
            if (item.matchId == matchID) {
                mBinding.itemRoot.background = AppCompatResources.getDrawable(
                    mBinding.root.context,
                    R.drawable.shape_bg_item_league_dark
                )
            } else {
                mBinding.itemRoot.background = AppCompatResources.getDrawable(
                    mBinding.root.context,
                    R.drawable.shape_bg_item_league
                )
            }
            Glide.with(mBinding.root).load(item.homeLogo).into(mBinding.ivHomeLogo)
            Glide.with(mBinding.root).load(item.awayLogo).into(mBinding.ivAwayLogo)
        } else if (mBinding is ItemWeekBinding) {
            mBinding.tvLeagueWeek.text = item.weekDay
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

    private fun convertStampToStr(timeStamp: Long): String {
        val date = Date(timeStamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }

    fun interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}