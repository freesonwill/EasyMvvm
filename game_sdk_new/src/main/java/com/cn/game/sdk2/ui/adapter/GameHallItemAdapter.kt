package com.cn.game.sdk2.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.compare.GameHallItemCompare
import com.cn.game.sdk2.ui.viewholder.BaseViewHolder
import com.cn.game.sdk2.utils.ext.CommonExt.getString
import com.xcjh.base_lib2.utils.LogUtils
import java.io.File

class GameHallItemAdapter : BaseAdapter<GameHallItem, BaseViewHolder, ItemGamehallPageItemBinding>(
    GameHallItemCompare()
) {
    private val IMAGE_SCALE_RATIO = 0.8f
    @SuppressLint("ClickableViewAccessibility")
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
        binding.tvOnline.text = binding.root.context.getString(R.string.g_game_list_type_online,item.online)
        binding.root.setOnTouchListener { _, event ->
            when(event.action) {
                ACTION_DOWN -> {
                    binding.ivGame.scaleX = IMAGE_SCALE_RATIO
                    binding.ivGame.scaleY = IMAGE_SCALE_RATIO
                }
                ACTION_UP -> {
                    binding.ivGame.scaleX = 1.0f
                    binding.ivGame.scaleY = 1.0f
                }
                else -> Unit
            }
            return@setOnTouchListener false
        }
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