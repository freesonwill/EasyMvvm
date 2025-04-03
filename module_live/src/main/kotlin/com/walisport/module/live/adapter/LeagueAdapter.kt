package com.walisport.module.live.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.walisport.lib.common.ui.adapter.BaseAdapter
import com.walisport.lib.common.ui.adapter.BaseViewHolder
import com.walisport.module.live.compare.LeagueMatchCompare
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.databinding.ItemLeagueBinding

class LeagueAdapter : BaseAdapter<LeagueMatchBean, BaseViewHolder, ItemLeagueBinding>(
    LeagueMatchCompare()
) {

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemLeagueBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvHomeName.text = item.homeTeamName
        binding.tvAwayName.text = item.awayTeamName
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemLeagueBinding {
        return ItemLeagueBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemLeagueBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}