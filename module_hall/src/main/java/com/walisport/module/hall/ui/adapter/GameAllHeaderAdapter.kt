package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.ItemGameAllHeaderBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.ui.fragment.DailyMatchInfoFragment
import com.walisport.module.business.common.utils.ext.setGlobalIndicator
import com.walisport.module.business.common.utils.ext.setGlobalBasicConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 全部類型的遊戲頭部Adapter，包含左方的廣告位、右方的邀請朋友和每日比賽
 * */
class GameAllHeaderAdapter(
    private val scope:CoroutineScope,
    private val fragmentManager: FragmentManager,
    private val onItemClickListener: GameAllHeaderViewHolder.OnHeaderItemClickListener
) : RecyclerView.Adapter<GameAllHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllHeaderViewHolder {
        val binding = ItemGameAllHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllHeaderViewHolder(scope,fragmentManager, binding, onItemClickListener)
    }

    override fun onBindViewHolder(
        holder: GameAllHeaderViewHolder,
        position: Int
    ) {
        holder.init()
    }

    override fun getItemCount(): Int = 1

}

class GameAllHeaderViewHolder(
    private val scope:CoroutineScope,
    val fragmentManager: FragmentManager,
    val binding: ItemGameAllHeaderBinding,
    private val onItemClickListener: OnHeaderItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun init() {
        scope.launch {
            with(binding) {
                 onItemClickListener.getBannerList().let {list ->
                    // 自定义适配器
                    val adapter = BannerImageMatchAdapter(list)
                    vpBanner.setAdapter(adapter)
                    vpBanner.setGlobalBasicConfig()
                    vpBanner.setGlobalIndicator()
                    vpBanner.start()
                 }

                fragmentManager.beginTransaction().replace(
                    R.id.daily_match_fragment,
                    DailyMatchInfoFragment()
                ).commit()

                onItemClickListener.getInviteFriend().let {list->
                    val adapter = BannerImageMatchAdapter(list)
                    ivInviteFriend.setAdapter(adapter)
                    ivInviteFriend.setGlobalBasicConfig()
                    ivInviteFriend.setGlobalIndicator()
                    ivInviteFriend.start()
                }
            }
        }
    }



    interface OnHeaderItemClickListener {
        suspend fun getBannerList():List<BannerImageMatchAdapter.ImageData>
        suspend fun getInviteFriend():List<BannerImageMatchAdapter.ImageData>
    }

}