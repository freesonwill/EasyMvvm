package com.walisport.module.live.ui.popup

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import com.walisport.module.live.R
import com.walisport.module.live.data.model.VideoResolutionBean
import com.walisport.module.live.databinding.PopupVideoResolutionLayoutBinding
import com.walisport.module.live.ui.adapter.VideoResolutionAdapter

class VideoResolutionHelper {
    private var popupWindow: PopupWindow? = null
    private var mBinding: PopupVideoResolutionLayoutBinding? = null

    private lateinit var adapter: VideoResolutionAdapter

    fun showPopUp(
        attachView: View,
        beanList: List<VideoResolutionBean>,
        itemClick: (String) -> Unit
    ) {
        // 关闭上一个pop
        dismissPopup()
        mBinding =
            PopupVideoResolutionLayoutBinding.inflate(LayoutInflater.from(attachView.context))
                .apply {
                    // 建立 PopupWindow
                    popupWindow = PopupWindow(
                        this.root,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        isOutsideTouchable = true
                    }

                    adapter = VideoResolutionAdapter {
                        dismissPopUpAnimated()
                        itemClick.invoke(it)
                    }

                    rvVideoResolution.adapter = adapter
                    rvVideoResolution.layoutManager = LinearLayoutManager(rvVideoResolution.context)
                    rvVideoResolution.addItemDecoration(
                        VideoResolutionDividerDecoration(
                            rvVideoResolution.context,
                            VideoResolutionDividerDecoration.VERTICAL,
                            ColorDrawable(R.color.video_resolution_divider_line.getColor())
                        )
                    )

                    adapter.submitList(beanList)

                    // 先進行測量
                    this.root.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )

                    // 計算 PopupWindow 的顯示位置
                    val offX =
                        -(this.rvVideoResolution.layoutParams.width / 2 - attachView.width / 2)
                    val offY =
                        -(this.rvVideoResolution.layoutParams.height + this.ivArrow.layoutParams.height + attachView.height)

                    root.pivotX = 40.dp2px.toFloat()
                    root.pivotY = 119.dp2px.toFloat()

                    root.scaleX = 0f
                    root.scaleY = 0f
                    root.alpha = 0f

                    // 开始动画
                    root.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(200)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())

                        .start()

                    //显示popupWindow
                    popupWindow?.showAsDropDown(attachView, offX, offY)
                }
    }

    private fun dismissPopup() {
        popupWindow?.dismiss()
        popupWindow = null
        mBinding = null
    }

    private fun dismissPopUpAnimated(){
        mBinding?.root!!.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withStartAction {
            }
            .withEndAction {
                dismissPopup()
            }
            .start()
    }
} 