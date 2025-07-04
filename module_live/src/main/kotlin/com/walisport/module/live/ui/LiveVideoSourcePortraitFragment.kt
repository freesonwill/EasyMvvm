package com.walisport.module.live.ui

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimensionPixelSize
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import com.walisport.module.live.R
import com.walisport.module.live.compare.VideoSourceBeanCompare
import com.walisport.module.live.databinding.FragmentLiveSourcePortraitBinding
import com.walisport.module.live.ui.adapter.LiveVideoSourceHorizontalAdapter
import com.walisport.module.live.ui.viewmodel.LiveVideoSourceViewModel
import kotlin.reflect.KClass

/**
 * 竖屏播放时的视频源页面
 */
class LiveVideoSourcePortraitFragment :
    LocationFixedDialogFragment<LiveVideoSourceViewModel, FragmentLiveSourcePortraitBinding>() {

    override val vbClass: KClass<FragmentLiveSourcePortraitBinding> =
        FragmentLiveSourcePortraitBinding::class
    override val vmClass: KClass<LiveVideoSourceViewModel> = LiveVideoSourceViewModel::class

    private var isDismissing = false


    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        with(mBinding) {

            rvSource.apply {
                itemAnimator = null
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = LiveVideoSourceHorizontalAdapter(VideoSourceBeanCompare()).apply {
                    post {
                        addItemDecoration(HorizontalItemDecoration())
                        submitList(mViewModel.liveVideoBean.value?.source)
                    }

                    setOnClickListener {
                        mViewModel.setPlayingVideoId(it)
                    }
                }
            }
        }

        //进入时展示动画
        mBinding.root.addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                mBinding.root.removeOnLayoutChangeListener(this)
                mBinding.root.post {
                    playEnterAnimations()
                }
            }
        })

    }

    override fun initListener() {
        with(mBinding) {
            ivClose.clickNoRepeat {
                dismiss()
            }
        }


    }

    override fun createObserver() {

        with(mViewModel) {

            liveVideoBean.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveVideoSourceHorizontalAdapter).apply {
                        val list = mViewModel.liveVideoBean.value
                        val size = list?.source?.size ?: 0
                        submitList(list?.source)

                        notifyItemRangeChanged(0, size)
                    }
                }
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : ComponentDialog(requireContext()) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    this@LiveVideoSourcePortraitFragment.dismiss()
                    return true
                } else {
                    return super.dispatchKeyEvent(event)
                }
            }

            override fun onTouchEvent(event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN && isOutOfBounds(event)) {
                    this@LiveVideoSourcePortraitFragment.dismiss() // 关闭对话框
                    return true
                }
                return super.onTouchEvent(event)
            }

            private fun isOutOfBounds(event: MotionEvent): Boolean {
                val x = event.x.toInt()
                val y = event.y.toInt()
                val contentView = window?.decorView?.findViewById<View>(android.R.id.content)
                val rect = Rect()
                contentView?.getHitRect(rect)
                return !rect.contains(x, y)
            }
        }
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.setWindowAnimations(R.style.NoAnimationDialog)

        return dialog
    }

    private fun playEnterAnimations() {
        mBinding.llRoot.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofInt(
                    R.dimen.video_source_portrait_margin_top.getDimensionPixelSize(),
                    0
                ).apply {
                    addUpdateListener {
                        val lp = mBinding.llRoot.layoutParams as FrameLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.llRoot.layoutParams = lp
                    }
                },
            )
        }, duration = 150L, start = true)

    }


    override fun dismiss() {
        if (isDismissing) return
        isDismissing = true

        mBinding.llRoot.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofInt(
                    0,
                    R.dimen.video_source_portrait_margin_top.getDimensionPixelSize()
                ).apply {
                    addUpdateListener {
                        val lp = mBinding.llRoot.layoutParams as FrameLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.llRoot.layoutParams = lp
                    }
                },
            )
            doOnEnd {
                superDismiss()
            }
        }, duration = 150L, start = true)
    }

    private fun superDismiss() {
        super.dismiss()
    }


    class HorizontalItemDecoration(
        private val spacing: Int = 12.dp2px,         // 常规间距大小（像素）
        private val leftRight: Int = 16.dp2px,         // 左右边距（像素）
        private val bottomSpacing: Int = 6.dp2px,   // 最后一个 item 的右边距离（像素）
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val itemCount = parent.adapter?.itemCount ?: 0 // 总 item 数

            // 包含边缘的情况
            outRect.top = 0
            outRect.bottom = 0
            outRect.left = if (position == 0) leftRight else 0
            outRect.right = if (position == itemCount - 1) bottomSpacing else spacing
        }
    }
}