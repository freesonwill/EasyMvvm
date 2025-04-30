package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.compare.LeagueMatchCompare
import com.walisport.module.live.data.model.MatchBean
import com.walisport.module.live.databinding.ItemLeagueBinding
import com.walisport.module.live.databinding.ItemWeekBinding

class LeagueAdapter : BaseAdapter<MatchBean, BaseViewHolder, ViewBinding>(
    LeagueMatchCompare()
) {
    companion object {
        const val TYPE_WEEK = 0
        const val TYPE_ITEM = 1
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemLeagueBinding) {
            binding.tvHomeName.text = item.homeTeamName
            binding.tvAwayName.text = item.awayTeamName
        } else if (binding is ItemWeekBinding) {
            binding.tvLeagueWeek.text = item.weekDay
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
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
        return item.isWeek
    }
}