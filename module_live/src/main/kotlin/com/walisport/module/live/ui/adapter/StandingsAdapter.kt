package com.walisport.module.live.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.walisport.lib.base.adapter.BaseAdapter
import com.walisport.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.compare.StandingsCompare
import com.walisport.module.live.data.model.StandingsBean
import com.walisport.module.live.databinding.ItemStandingsBinding
import com.walisport.module.live.databinding.ItemStandingsLayBinding
import com.walisport.module.live.databinding.ItemWorldCupBinding

class StandingsAdapter(private val context: Context) :
    BaseAdapter<StandingsBean, BaseViewHolder, ViewBinding>(
        StandingsCompare()
    ) {
    companion object {
        const val TYPE_HEAD = 0
        const val TYPE_ITEM = 1
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemStandingsBinding) {
            binding.tvStandingsTeam.text = holder.getString(R.string.standings_a)
            binding.layTeam.removeAllViews()
            for (i in 0 until 4) {
                val view =
                    ItemStandingsLayBinding.inflate(LayoutInflater.from(context), null, false)
                val lay = view.root.findViewById<ConstraintLayout>(R.id.item_standings)
                if (i < 2) {
                    lay.background = context.getDrawable(R.color.tran_08_ac8e6a)
                } else {
                    lay.background = null
                }
                val rank = view.root.findViewById<AppCompatTextView>(R.id.tv_standings_rank)
                val country = view.root.findViewById<AppCompatTextView>(R.id.tv_standings_country)
                rank.text = item.teams[i].rank.toString()
                country.text = item.teams[i].name
                binding.layTeam.addView(view.root)
            }
        } else if (binding is ItemWorldCupBinding) {
            binding.tvWorldCup.text = holder.getString(R.string.live_word_cup)
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