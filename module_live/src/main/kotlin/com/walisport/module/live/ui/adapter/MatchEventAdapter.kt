package com.walisport.module.live.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.walisport.lib.base.adapter.BaseAdapter
import com.walisport.lib.base.viewholder.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.compare.MatchEventCompare
import com.walisport.module.live.data.model.MatchEventBean
import com.walisport.module.live.databinding.ItemMatchEventBinding
import com.walisport.module.live.databinding.ItemMatchEventTitleBinding

class MatchEventAdapter(private val context: Context) :
    BaseAdapter<MatchEventBean, BaseViewHolder, ViewBinding>(
        MatchEventCompare()
    ) {
    companion object {
        const val TYPE_HEAD = 0
        const val TYPE_ITEM = 1
        const val TYPE_FOOT = 2
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val item = getItem(position)
        if (binding is ItemMatchEventBinding) {
            binding.tvMatchMinutes.text = String.format("%s'", item.minutes)
            binding.tvMatchPlayerLeft.text = item.homePlayer
            binding.tvMatchPlayerLeftTwo.text = item.homePlayerTwo
            binding.tvMatchPlayerRight.text = item.awayPlayer
            binding.tvMatchPlayerRightTwo.text = item.awayPlayerTwo
            setMatchEventData(item.homeType, binding.tvMatchTypeLeft, binding.ivMatchTypeLeft)
            setMatchEventData(
                item.homeTypeTwo,
                binding.tvMatchTypeLeftTwo,
                binding.ivMatchTypeLeftTwo
            )
            setMatchEventData(item.awayType, binding.tvMatchTypeRight, binding.ivMatchTypeRight)
            setMatchEventData(
                item.awayTypeTwo,
                binding.tvMatchTypeRightTwo,
                binding.ivMatchTypeRightTwo
            )
        } else if (binding is ItemMatchEventTitleBinding) {
            if (position == itemCount - 1) {
                binding.tvMatchTitle.text = context.getString(R.string.standings_over)
            } else {
                binding.tvMatchTitle.text = context.getString(R.string.standings_start)
            }
        }
    }

    private fun setMatchEventData(type: Int, text: AppCompatTextView, icon: AppCompatImageView) {
        when (type) {
            1 -> {
                text.text = context.getString(R.string.standings_goal)
                icon.background =
                    AppCompatResources.getDrawable(context, R.mipmap.icon_live_football)
            }

            2 -> {
                text.text = context.getString(R.string.standings_yellow)
                icon.background = AppCompatResources.getDrawable(context, R.mipmap.icon_live_yellow)
            }

            3 -> {
                text.text = context.getString(R.string.standings_help)
                icon.background = AppCompatResources.getDrawable(context, R.mipmap.icon_help_attack)
            }

            4 -> {
                text.text = context.getString(R.string.standings_up)
                icon.background = AppCompatResources.getDrawable(context, R.mipmap.icon_standing_up)
            }

            5 -> {
                text.text = context.getString(R.string.standings_down)
                icon.background =
                    AppCompatResources.getDrawable(context, R.mipmap.icon_standing_down)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        if (viewType == TYPE_HEAD || viewType == TYPE_FOOT) {
            return ItemMatchEventTitleBinding.inflate(inflater, parent, false)
        }
        return ItemMatchEventBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEAD
        } else if (position == itemCount - 1) {
            return TYPE_FOOT
        }
        return TYPE_ITEM
    }
}