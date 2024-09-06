package com.cn.game.sdk2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.compare.GameHallItemCompare
import com.cn.game.sdk2.ui.viewholder.BaseViewHolder
import com.xcjh.base_lib2.utils.LogUtils
import java.io.File

class GameHallItemAdapter : BaseAdapter<GameHallItem, BaseViewHolder, ItemGamehallPageItemBinding>(
    GameHallItemCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemGamehallPageItemBinding,
        item: GameHallItem
    ) {
        binding.ivGame.apply {
            try {
                when(item.iconType){
                    0 -> setImageResource(item.icon.toInt())
                    1 -> Glide.with(holder.itemView.context).load(item.icon).into(this)
                    2 -> Glide.with(holder.itemView.context).load(File(item.icon)).into(this)
                }
            }catch (e:Exception){
                e.printStackTrace()
                LogUtils.e("error: ivGame load img:${e.message}")
            }
        }

        binding.tvName.text = item.name
        binding.tvOnline.text = item.online.toString()
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGamehallPageItemBinding {
        return ItemGamehallPageItemBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGamehallPageItemBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}