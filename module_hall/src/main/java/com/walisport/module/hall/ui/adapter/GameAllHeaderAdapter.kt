package com.walisport.module.hall.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.common.ui.adapter.BannerImageMatchAdapter
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import com.walisport.module.hall.R
import com.walisport.module.hall.databinding.ItemGameAllHeaderBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.hall.ui.fragment.DailyMatchInfoFragment

/**
 * 全部類型的遊戲頭部Adapter，包含左方的廣告位、右方的邀請朋友和每日比賽
 * */
class GameAllHeaderAdapter(
    private val fragmentManager: FragmentManager,
    private val onItemClickListener: GameAllHeaderViewHolder.OnHeaderItemClickListener?
) : RecyclerView.Adapter<GameAllHeaderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAllHeaderViewHolder {
        val binding = ItemGameAllHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameAllHeaderViewHolder(fragmentManager, binding, onItemClickListener)
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
    val fragmentManager: FragmentManager,
    val binding: ItemGameAllHeaderBinding,
    private val onItemClickListener: OnHeaderItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        with(binding) {
            val mockBannerList = listOf(
                R.drawable.image_banner_demo,
                R.drawable.image_cover_demo,
                R.drawable.image_banner_demo,
                R.drawable.image_cover_demo,
                R.drawable.image_banner_demo
            )
//            vpBanner.adapter = bannerAdapter
//            bannerAdapter.submitList(mockBannerList)
//            vpBanner.isUserInputEnabled = true
            //触摸事件会影响vpBanner的滑动个,如果想做到触摸事件不影响Banner,需要添加一个蒙层,然后拦截事件,需下发给底层view
//            vpBanner.setOnTouchListener { v, event ->
//               when(event.action){
//                   MotionEvent.ACTION_DOWN->{
//                       LogUtils.e("vpBanner.setOnTouchListener-----ACTION_DOWN")
//                       stopProBannerJob()
//                   }
//
//                   MotionEvent.ACTION_UP->{
//                       LogUtils.e("vpBanner.setOnTouchListener----ACTION_UP")
//                       restProBannerJob()
//                   }
//
//                   MotionEvent.ACTION_CANCEL->{
//                       LogUtils.e("vpBanner.setOnTouchListener-------ACTION_CANCEL")
//                       restProBannerJob()
//                   }
//
//               }
//                false
//            }

            proBanner.setTriggerListener {
                vpBanner.setLoopTime(50)
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
                onItemClickListener?.onInviteFriendItemClick()
            }

            fragmentManager.beginTransaction().replace(
                R.id.daily_match_fragment,
                DailyMatchInfoFragment()
            ).commit()



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
    }

}