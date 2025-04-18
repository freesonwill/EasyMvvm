package com.walisport.module.live.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.adapter.BaseAdapter
import arch.cayenne.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.compare.TablesCompare
import com.walisport.module.live.data.model.TableBean
import com.walisport.module.live.databinding.ItemStandingsBinding
import com.walisport.module.live.databinding.ItemStandingsLayBinding
import com.walisport.module.live.databinding.ItemWorldCupBinding

class StandingsAdapter :
    BaseAdapter<TableBean, BaseViewHolder, ViewBinding>(
        TablesCompare()
    ) {
    companion object {
        const val TYPE_HEAD = 0
        const val TYPE_ITEM = 1
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemStandingsBinding) {
            if (item.group == 1) {
                binding.tvStandingsTeam.text = holder.getString(R.string.standings_a)
            } else if (item.group == 2) {
                binding.tvStandingsTeam.text = holder.getString(R.string.standings_b)
            } else if (item.group == 3) {
                binding.tvStandingsTeam.text = holder.getString(R.string.standings_c)
            } else if (item.group == 4) {
                binding.tvStandingsTeam.text = holder.getString(R.string.standings_d)
            }
            binding.layTeam.removeAllViews()
            for (i in 0 until item.rows.size) {
                val view = ItemStandingsLayBinding.inflate(
                    LayoutInflater.from(holder.itemView.context),
                    null,
                    false
                )
                val lay = view.root.findViewById<ConstraintLayout>(R.id.item_standings)
                if (i < 2) {
                    lay.background = AppCompatResources.getDrawable(
                        holder.itemView.context,
                        R.color.tran_08_ac8e6a
                    )
                } else {
                    lay.background = null
                }
                val line = view.root.findViewById<AppCompatTextView>(R.id.tv_out_line)
                if (i == 0) {
                    line.visibility = View.VISIBLE
                } else {
                    line.visibility = View.INVISIBLE
                }
                val rank = view.root.findViewById<AppCompatTextView>(R.id.tv_standings_rank)
                val country = view.root.findViewById<AppCompatTextView>(R.id.tv_standings_country)
                val total = view.root.findViewById<AppCompatTextView>(R.id.tv_total)           //场次
                val draw = view.root.findViewById<AppCompatTextView>(R.id.tv_won_draw_loss)           //胜/平/负
                val against = view.root.findViewById<AppCompatTextView>(R.id.tv_goals_against) //进/失
                val points = view.root.findViewById<AppCompatTextView>(R.id.tv_points)         //积分
                val index = i + 1
                rank.text = index.toString()
                country.text = item.rows[i].team_name
                total.text = item.rows[i].total.toString()
                draw.text = String.format("%d/%d/%d", item.rows[i].won, item.rows[i].draw, item.rows[i].loss)
                against.text = String.format("%d/%d", item.rows[i].goals, item.rows[i].goals_against)
                points.text = item.rows[i].points.toString()
                binding.layTeam.addView(view.root)
            }
        } else if (binding is ItemWorldCupBinding) {
            binding.tvWorldCup.text = item.conference
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        if (viewType == TYPE_HEAD) {
            return ItemWorldCupBinding.inflate(inflater, parent, false)
        }
        return ItemStandingsBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEAD
        }
        return TYPE_ITEM
    }
}