package com.walisport.module.live.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.BaseSideSheetDialogFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.databinding.FragmentLiveBetOnMenuBinding
import com.walisport.module.live.ui.adapter.LiveBetOnMenuAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import kotlin.math.abs
import kotlin.reflect.KClass


class LiveBetOnMenuFragment :
    BaseSideSheetDialogFragment<LiveBetOnMenuViewModel, FragmentLiveBetOnMenuBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnMenuBinding> = FragmentLiveBetOnMenuBinding::class
    override val vmClass: KClass<LiveBetOnMenuViewModel> = LiveBetOnMenuViewModel::class
    private var mList: List<String> = listOf("热门", "让球", "大小")
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var translationX: Float = 0f
    private var isSwipingDialog: Boolean = false
    private val swipeThreshold = 0.5f
    private val touchSlop = 10f

    class MenuItemDecoration(

    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = 30.dp2px
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rv.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(
                this@LiveBetOnMenuFragment.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = LiveBetOnMenuAdapter(object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }
            }).apply {
                post {
                    addItemDecoration(MenuItemDecoration())
                    submitList(mList)
                }
            }
        }
    }

    override fun initListener() {
    }

    override fun onStart() {
        super.onStart()
        // 设置 Dialog 的宽度和高度
        if (dialog != null && dialog!!.window != null) {
            // 获取屏幕高度
            val screenWidth = resources.displayMetrics.widthPixels
            // 设置宽度为屏幕的 89%（可调整）
            val dialogWidth = (screenWidth * 0.89) // 自定义宽度
            // 设置宽度为屏幕宽度
            dialog!!.window!!.setLayout(dialogWidth.toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
        }
        // 设置靠右显示
        dialog!!.window!!.setGravity(Gravity.END)
        // 设置手势监听
        setupSwipeGesture()
        //解决底部虚拟键盘变白情况
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeGesture() {
        mBinding.main.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    translationX = mBinding.root.translationX
                    isSwipingDialog = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - startX
                    val deltaY = event.rawY - startY
                    if (!isSwipingDialog && (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop)) {
                        isSwipingDialog = abs(deltaX) > Math.abs(deltaY) && deltaX >= 0
                        if (isSwipingDialog) {
                        }
                    }
                    if (isSwipingDialog) {
                        if (deltaX >= 0) {
                            mBinding.root.translationX = translationX + deltaX
                        }
                    }
                    false
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isSwipingDialog) {
                        val swipeDistance = mBinding.root.translationX
                        if (swipeDistance > mBinding.root.width * swipeThreshold) {
                            animateDismiss()
                        } else {
                            animateReset()
                        }
                    }
                    isSwipingDialog = true
                    true
                }

                else -> false
            }
        }
    }

    private fun animateDismiss() {
        mBinding.root.animate()
            .translationX(mBinding.root.width.toFloat())
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dismiss()
                }
            })
            .start()
    }

    private fun animateReset() {
        mBinding.root.animate()
            .translationX(0f)
            .setDuration(200)
            .setListener(null)
            .start()
    }


}