package com.walisport.module.live.ui.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import arch.cayenne.lib.common.utils.ThreadUtils
import com.walisport.module.live.data.model.VideoResolutionBean
import com.walisport.module.live.databinding.PopupVideoResolutionLayoutBinding
import com.walisport.module.live.ui.adapter.VideoResolutionAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoResolutionHelper {
    private var popupWindow: PopupWindow? = null
    private var mBinding: PopupVideoResolutionLayoutBinding? = null

    private lateinit var adapter: VideoResolutionAdapter

    companion object {
        private const val SHOW_TIME = 3_000L
    }

    fun showPopUp(attachView: View) {
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
                    }

                    rvVideoResolution.adapter = adapter

                    ThreadUtils.mainScope.launch {
                        delay(1000)
                        adapter.submitList(
                            listOf(
                                VideoResolutionBean("1080p", true),
                                VideoResolutionBean("720p", false),
                                VideoResolutionBean("540p", false)
                            )
                        )

                        adapter.notifyDataSetChanged()
                    }


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

                    // 顯示 PopupWindow
                    popupWindow?.showAsDropDown(attachView, offX, offY)

//            this.root.postDelayed({ dismissTips() }, SHOW_TIME)
                }
    }

    private fun dismissPopup() {
        popupWindow?.dismiss()
        popupWindow = null
        mBinding = null
    }
} 