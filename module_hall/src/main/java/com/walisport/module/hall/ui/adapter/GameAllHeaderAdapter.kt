package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.ItemGameAllHeaderBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.ui.fragment.DailyMatchInfoFragment
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
                val mockBannerList = onItemClickListener.getBannerList()
                proBanner.setTriggerListener {
                    vpBanner.setLoopTime(3_000/10)
                    vpBanner.isAutoLoop(true)
                    vpBanner.start()
                    vpBanner.postDelayed({
                        vpBanner.stop()                    // 停止自动轮播
                        vpBanner.isAutoLoop(false)         // 关闭自动轮播功能 // 可选：允许下次再次触发
                    }, 50)
                }


                // 自定义适配器
                val adapter = BannerImageMatchAdapter(mockBannerList)
                vpBanner.setAdapter(adapter)
                vpBanner.setBannerRound(9.dp2px.toFloat())
                vpBanner.isAutoLoop(false)
                // 设置滑动时长丝滑,不影响曲线,
                vpBanner.setScrollTime(600)  // 0.5 秒
                vpBanner.setPageTransformer(CustomCurveTransformer())
                // 启动轮播

                ivInviteFriend.clickNoRepeat {
                    onItemClickListener.onInviteFriendItemClick()
                }

                fragmentManager.beginTransaction().replace(
                    R.id.daily_match_fragment,
                    DailyMatchInfoFragment()
                ).commit()
            }
        }
    }


    fun restProBannerJob() {
        binding.proBanner.resetTriggerJob()
    }

    fun stopProBannerJob() {
        binding.proBanner.stopTriggerJob()
    }


    interface OnHeaderItemClickListener {
        fun onInviteFriendItemClick()
        suspend fun getBannerList():List<BannerImageMatchAdapter.ImageData>
    }

}