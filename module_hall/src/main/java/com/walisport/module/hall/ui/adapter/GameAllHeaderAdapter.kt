package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllBannerData
import com.walisport.module.hall.databinding.ItemGameAllHeaderBinding

/**
 * 全部類型的遊戲頭部Adapter，包含左方的廣告位、右方的邀請朋友和每日比賽
 * */
class GameAllHeaderAdapter: RecyclerView.Adapter<GameAllHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllHeaderViewHolder {
        val binding = ItemGameAllHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: GameAllHeaderViewHolder,
        position: Int
    ) {
        holder.init()
    }

    override fun getItemCount(): Int = 1

    fun restProBannerJob(recyclerView: RecyclerView) {
        (recyclerView.findViewHolderForAdapterPosition(0) as? GameAllHeaderViewHolder)?.restProBannerJob()
    }

    fun stopProBannerJob(recyclerView: RecyclerView) {
        (recyclerView.findViewHolderForAdapterPosition(0) as? GameAllHeaderViewHolder)?.stopProBannerJob()
    }
}

class GameAllHeaderViewHolder(val binding: ItemGameAllHeaderBinding): RecyclerView.ViewHolder(binding.root) {
    val mockBannerList = arrayListOf(
        GameAllBannerData(R.drawable.image_banner_demo),
        GameAllBannerData(R.drawable.image_banner_demo),
        GameAllBannerData(R.drawable.image_banner_demo),
        GameAllBannerData(R.drawable.image_banner_demo),
        GameAllBannerData(R.drawable.image_banner_demo),
    )
    private val bannerAdapter by lazy { GameAllBannerAdapter() }
    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        with(binding) {
            vpBanner.adapter = bannerAdapter
            bannerAdapter.submitList(mockBannerList)
            vpBanner.isUserInputEnabled = true
            vpBanner.getChildAt(0).setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }

            vpBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    proBanner.resetTriggerJob()
                }
            })
            proBanner.setTriggerListener {
                vpBanner.currentItem = (vpBanner.currentItem + 1) % bannerAdapter.itemCount
            }
        }
    }

    fun restProBannerJob() {
        binding.proBanner.resetTriggerJob()
    }

    fun stopProBannerJob() {
        binding.proBanner.job?.cancel()
    }
}