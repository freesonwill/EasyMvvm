package com.walisport.module.gamedetail.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.data.model.GamePreviewBean
import com.walisport.module.gamedetail.data.model.PreviewType
import com.walisport.module.gamedetail.databinding.ItemGamePreviewBinding
import com.walisport.module.gamedetail.ui.viewholder.CarouselViewHolder

class GamePreviewAdapter : CarouselAdapter<GamePreviewAdapter.ViewHolder>() {

    private val previews: MutableList<GamePreviewBean> = mutableListOf()

    class ViewHolder(private val binding: ItemGamePreviewBinding): CarouselViewHolder(binding.root) {
        fun bind(data: GamePreviewBean) {
            with(binding) {
                when(data.type) {
                    PreviewType.IMAGE -> {
                        Glide.with(binding.root.context)
                            .load(data.url)
                            .placeholder(R.mipmap.img_game_preview)
                            .into(iv)
                        vv.visibility = View.GONE
                    }
                    PreviewType.VIDEO -> {
                        vv.setVideoURI(data.url.toUri())
                        iv.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return previews.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemGamePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(previews[position])
    }



    /**
     * 提供一個公開的方法來更新適配器的數據。
     * @param newData 新的數據列表。
     */
    fun setData(newData: List<GamePreviewBean>) {
        previews.clear()
        previews.addAll(newData)
        notifyDataSetChanged()
    }
}