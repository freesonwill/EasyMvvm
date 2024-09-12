package com.cn.game.sdk2.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
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
    private val IMAGE_SCALE_RATIO = 0.8f
    private val IMAGE_SCALE_DURATION = 300L
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
        binding.tvOnline.text = item.online.toString()
        binding.root.setOnTouchListener { _, event ->
            when(event.action) {
                ACTION_DOWN -> {
                    scaleIcon(binding.ivGame, false)
                }
                ACTION_UP, ACTION_CANCEL -> {
                    scaleIcon(binding.ivGame, true)
                }
                else -> Unit
            }
            return@setOnTouchListener false
        }
    }

    private fun scaleIcon(view: View, isReverse: Boolean) {
        val (start, end) = if (isReverse) {
            Pair(IMAGE_SCALE_RATIO, 1.0f)
        } else {
            Pair(1.0f, IMAGE_SCALE_RATIO)
        }
        ObjectAnimator.ofFloat(view, "scaleX", start, end).apply {
            this.duration = IMAGE_SCALE_DURATION
        }.start()
        ObjectAnimator.ofFloat(view, "scaleY", start, end).apply {
            this.duration = IMAGE_SCALE_DURATION
        }.start()
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