package com.walisport.module.live.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.compare.TablesCompare
import com.walisport.module.live.data.model.StandingsBean
import com.walisport.module.live.databinding.ItemStandingsBinding
import com.walisport.module.live.databinding.ItemStandingsLayBinding

class StandingsAdapter :
    BaseAdapter<StandingsBean, BaseViewHolder, ViewBinding>(
        TablesCompare()
    ) {

    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemStandingsBinding) {
            when (item.group) {
                1 -> {
                    binding.tvStandingsTeam.text = holder.getString(R.string.standings_a)
                }

                2 -> {
                    binding.tvStandingsTeam.text = holder.getString(R.string.standings_b)
                }

                3 -> {
                    binding.tvStandingsTeam.text = holder.getString(R.string.standings_c)
                }

                4 -> {
                    binding.tvStandingsTeam.text = holder.getString(R.string.standings_d)
                }
            }
            binding.tvStandingsTeam.text = holder.getString(R.string.standings_a)
            binding.layTeam.removeAllViews()
            val size = item.rows.size
            for (i in 0..<size) {
                val temp = item.rows[i]
                val itemBinding =
                    ItemStandingsLayBinding.inflate(LayoutInflater.from(holder.itemView.context))
                itemBinding.tvTeamName.text = temp.name
                val index = i + 1
                itemBinding.tvStandingsRank.text = index.toString()
                Glide.with(holder.itemView.context).load(temp.logo)
                    .error(
                        getErrorDrawable(
                            holder.itemView.context,
                            R.drawable.icon_standings_error_logo
                        )
                    )
                    .placeholder(
                        getErrorDrawable(
                            holder.itemView.context,
                            R.drawable.icon_standings_error_logo
                        )
                    )
                    .into(itemBinding.ivTeamLogo)
                itemBinding.tvTotal.text = temp.total.toString()
                itemBinding.tvWonDrawLoss.text =
                    String.format("%d/%d/%d", temp.win, temp.draw, temp.loss)
                itemBinding.tvGoalsAgainst.text =
                    String.format("%d/%d", temp.goals, temp.fumble)
                itemBinding.tvPoints.text = temp.score.toString()
                binding.layTeam.addView(itemBinding.root)
            }
        }
    }

    private fun getErrorDrawable(context: Context, resId: Int): Drawable? {
        return SportSkinResourceManager.getDrawable(context, resId)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return ItemStandingsBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}