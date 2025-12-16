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
                // Clear previous content first to prevent memory leaks
                Glide.with(iv).clear(iv)
                vv.stopNestedScroll()

                when(data.type) {
                    PreviewType.IMAGE -> {
                        // Use imageView itself as lifecycle owner for better memory management
                        Glide.with(iv)
                            .load(data.url)
                            .placeholder(R.mipmap.img_game_preview)
                            .into(iv)
                        iv.visibility = View.VISIBLE
                        vv.visibility = View.GONE
                    }
                    PreviewType.VIDEO -> {
                        vv.setVideoURI(data.url.toUri())
                        vv.visibility = View.VISIBLE
                        iv.visibility = View.GONE
                    }
                }
            }
        }

        fun unbind() {
            with(binding) {
                // Clean up resources when ViewHolder is recycled
                Glide.with(iv).clear(iv)
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

    override fun onViewRecycled(holder: ViewHolder) {
        // Clean up resources when ViewHolder is recycled to prevent memory leaks
        holder.unbind()
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