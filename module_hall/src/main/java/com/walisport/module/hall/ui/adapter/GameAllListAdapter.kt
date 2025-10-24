package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.databinding.ItemGameAllListBinding
import kotlin.random.Random

/**
 * 全部類型的遊戲頭部Adapter，包含左方的廣告位、右方的邀請朋友和每日比賽
 * */
class GameAllListAdapter(
    val onItemClickListener: (() -> Unit)? = null
): RecyclerView.Adapter<GameAllListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllListViewHolder {
        val binding = ItemGameAllListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: GameAllListViewHolder,
        position: Int
    ) {
        holder.init(onItemClickListener)
    }

    override fun getItemCount(): Int = 5

}

class GameAllListViewHolder(val binding: ItemGameAllListBinding): RecyclerView.ViewHolder(binding.root) {
    private val mockList by lazy {
        val l = ArrayList<GameContentData>()
        for (i in 0..9) {
            l.add(
                GameContentData(
                    cover = R.drawable.image_cover_demo,
                    hotOrCold = if (i % 2 == 0) HotColdType.HOT else HotColdType.COLD,
                    percent = 20.0f,
                    onlineCount = Random.nextInt(100,32767)
                )
            )
        }
        l
    }

    @SuppressLint("ClickableViewAccessibility")
    fun init(onItemClickListener: (() -> Unit)? = null) {
        with(binding) {
            rvInnerList.layoutManager =
                LinearLayoutManager(rvInnerList.context, LinearLayoutManager.HORIZONTAL, false)
            rvInnerList.adapter = GameAllListInnerAdapter().also {
                it.submitList(mockList)
            }
            val divider = DividerItemDecoration(
                rvInnerList.context,
                LinearLayoutManager.HORIZONTAL
            )
            val drawable = ContextCompat.getDrawable(rvInnerList.context, R.drawable.shape_game_all_inner_divider)
            divider.setDrawable(drawable!!)
            rvInnerList.addItemDecoration(divider)

            root.clickNoRepeat {
                onItemClickListener?.invoke()
            }
        }
    }
}