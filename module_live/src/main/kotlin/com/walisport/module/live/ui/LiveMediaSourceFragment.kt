package com.walisport.module.live.ui

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimensionPixelSize
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import com.walisport.module.live.R
import com.walisport.module.live.compare.MediaSourceBeanCompare
import com.walisport.module.live.compare.VideoSourceBeanCompare
import com.walisport.module.live.databinding.FragmentLiveSourcePortraitBinding
import com.walisport.module.live.ui.adapter.LiveMediaSourceHorizontalAdapter
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoSourceViewModel
import kotlin.reflect.KClass


class LiveMediaSourceFragment :
    BaseDialogFragment<LiveVideoSourceViewModel, FragmentLiveSourcePortraitBinding>() {

    override val vbClass: KClass<FragmentLiveSourcePortraitBinding> =
        FragmentLiveSourcePortraitBinding::class
    override val vmClass: KClass<LiveVideoSourceViewModel> = LiveVideoSourceViewModel::class

    override val dialogBackground: Drawable?
        get() = ContextCompat.getDrawable(
            requireContext(),
            com.walisport.module.live.R.drawable.bg_source_dialog
        )

    private val mediaViewModel: LiveMatchMediaViewModel by sharedViewModel<LiveMatchMediaViewModel, LiveMatchMediaFragment>()

    private var isDismissing = false

    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        with(mBinding) {

            rvSource.apply {
                itemAnimator = null
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = LiveMediaSourceHorizontalAdapter(MediaSourceBeanCompare()).apply {
                    post {
                        addItemDecoration(HorizontalItemDecoration())
//                        submitList(mViewModel.liveVideoBean.value?.source)
                    }

                    setOnClickListener {
                        mediaViewModel.switchToVideo()
                        mViewModel.setPlayingVideoId(it)
                    }
                }
            }
        }

        //进入时展示动画
        mBinding.ctSource.addOnLayoutChangeListener(object : OnLayoutChangeListener {
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
                val location = IntArray(2)
                mBinding.ctSource.getLocationOnScreen(location)

                if (location[0] == 0) {
                    mBinding.ctSource.removeOnLayoutChangeListener(this)
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


    override suspend fun createObserver() {
        with(mViewModel) {

            liveVideoBean.observe(viewLifecycleOwner) {
                mBinding.rvSource.apply {
                    (adapter as LiveMediaSourceHorizontalAdapter).apply {
                        val list = mViewModel.liveVideoBean.value
                        val size = list?.source?.size ?: 0
//                        submitList(list?.source)

                        notifyItemRangeChanged(0, size)
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }


    override fun onStart() {
        super.onStart()
        if (this.dialog != null) {
            val window = this.dialog!!.window
            if (window != null) {
                val windowParams = window.attributes;
                windowParams.dimAmount = 0f// 50% dimming
                windowParams.gravity = Gravity.BOTTOM
                windowParams.width = WindowManager.LayoutParams.MATCH_PARENT // 宽度占满
                val height = requireArguments().getInt(HEIGHT, ViewGroup.LayoutParams.WRAP_CONTENT)
                windowParams.height = height
                window.setAttributes(windowParams);
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setWindowAnimations(R.style.NoAnimationDialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    this@LiveMediaSourceFragment.dismiss()
                    return true
                } else {
                    return false
                }
            }

        })

        dialog.window?.let { window ->
            val decorView = window.decorView
            decorView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val dialogBounds = Rect()
                    decorView.getHitRect(dialogBounds)

                    val contentBounds = Rect()
                    mBinding.llRoot.getHitRect(contentBounds)
                    if (!dialogBounds.contains(event.x.toInt(), event.y.toInt())) {
                        dismiss() // 手动调用 dismiss
                        true
                    } else if (!contentBounds.contains(event.x.toInt(), event.y.toInt())) {
                        dismiss() // 手动调用 dismiss
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }

        }

        return dialog
    }


    private fun playEnterAnimations() {
        mBinding.ctSource.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofInt(
                    com.walisport.module.live.R.dimen.video_source_portrait_margin_top.getDimensionPixelSize(),
                    0
                ).apply {
                    addUpdateListener {
//                        val lp = mBinding.ctSource.layoutParams as LinearLayout.LayoutParams
//                        lp.topMargin = it.animatedValue as Int
//
//                        mBinding.ctSource.layoutParams = lp
                        mBinding.ctSource.translationY = (it.animatedValue as Int).toFloat()
                    }
                },
            )
        }, duration = ANIMATION_DURATION, start = true)

    }

    override fun dismiss() {
        if (isDismissing) return
        isDismissing = true

        mBinding.ctSource.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofInt(
                    0,
                    com.walisport.module.live.R.dimen.video_source_portrait_margin_top.getDimensionPixelSize()
                ).apply {
                    addUpdateListener {
                        mBinding.ctSource.translationY = (it.animatedValue as Int).toFloat()
                    }
                },
            )
            doOnEnd {
                superDismiss()
            }
        }, duration = ANIMATION_DURATION, start = true)
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


    companion object {

        const val WIDTH = "width"
        const val HEIGHT = "height"

        const val ANIMATION_DURATION = 120L
    }

}
