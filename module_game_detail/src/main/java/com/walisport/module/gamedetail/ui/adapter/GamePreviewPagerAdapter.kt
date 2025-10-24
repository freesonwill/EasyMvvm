package com.walisport.module.gamedetail.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.walisport.module.gamedetail.data.model.GamePreviewBean
import com.walisport.module.gamedetail.data.model.PreviewType
import com.walisport.module.gamedetail.ui.fragment.GamePreviewImageFragment
import com.walisport.module.gamedetail.ui.fragment.GamePreviewVideoFragment

class GamePreviewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val datas = mutableListOf<GamePreviewBean>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(datas: List<GamePreviewBean>) {
        this.datas.apply {
            clear()
            addAll(datas)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun createFragment(position: Int): Fragment {
        return datas[position].let { data ->
            when (data.type) {
                PreviewType.IMAGE -> GamePreviewImageFragment.newInstance(position, data.url)
                PreviewType.VIDEO -> GamePreviewVideoFragment.newInstance(position, data.url)
            }
        }
    }
}