package com.walisport.module.live.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.live.R
import com.walisport.module.live.compare.MatchEventCompare
import com.walisport.module.live.data.EventEnum
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
            //分钟
            binding.tvMatchMinutes.text = String.format("%s'", item.time)
            //主队
            binding.tvMatchPlayerLeft.text = item.homePlayer
            binding.tvMatchPlayerLeftTwo.text = item.homeTwoPlayer
            binding.tvMatchTypeLeft.text = getEventType(item.homeType)
            binding.tvMatchTypeLeftTwo.text = getEventType(item.homeTwoType)
            binding.ivMatchTypeLeft.background = getEventIcon(item.homeType)
            binding.ivMatchTypeLeftTwo.background = getEventIcon(item.homeTwoType)
            //客队
            binding.tvMatchPlayerRight.text = item.awayPlayer
            binding.tvMatchPlayerRightTwo.text = item.awayTwoPlayer
            binding.tvMatchTypeRight.text = getEventType(item.awayType)
            binding.tvMatchTypeRightTwo.text = getEventType(item.awayTwoType)
            binding.ivMatchTypeRight.background = getEventIcon(item.awayType)
            binding.ivMatchTypeRightTwo.background = getEventIcon(item.awayTwoType)
        } else if (binding is ItemMatchEventTitleBinding) {
            if (position == itemCount - 1) {
                binding.tvMatchTitle.text = context.getString(R.string.standings_over)
            } else {
                binding.tvMatchTitle.text = context.getString(R.string.standings_start)
            }
        }
    }

    private fun getEventType(type: Int): String {
        return EventEnum.getEventByCode(type)?.desc ?: ""
    }

    private fun getEventIcon(type: Int): Drawable? {
        if (EventEnum.getEventByCode(type) != null) {
            val icon = EventEnum.getEventByCode(type)?.icon
            if (icon != null) {
                return AppCompatResources.getDrawable(context, icon)
            }
        }
        return null
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