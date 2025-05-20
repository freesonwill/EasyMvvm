package com.walisport.module.live.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.databinding.FragmentLiveBetOnMenuBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxLayoutBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxTextViewBinding
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import kotlin.math.abs
import kotlin.reflect.KClass
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseSideSheetDialogFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import kotlin.math.atan2
import kotlin.math.sqrt

class LiveBetOnMenuFragment :
    BaseSideSheetDialogFragment<LiveBetOnMenuViewModel, FragmentLiveBetOnMenuBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnMenuBinding> = FragmentLiveBetOnMenuBinding::class
    override val vmClass: KClass<LiveBetOnMenuViewModel> = LiveBetOnMenuViewModel::class
    private val betOnViewModel: LiveBetOnViewModel by sharedViewModel<LiveBetOnViewModel, LiveBetOnFragment>()
    private var startX = 0f
    private var startY = 0f
    private var translationX = 0f
    private var isSwipingDialog = false
    private var isHorizontalSwipe = false
    private val touchSlop by lazy { ViewConfiguration.get(mBinding.main.context).scaledTouchSlop }
    private val angleTolerance = 80 // 角度容忍范围
    private val swipeThreshold = 0.3f // 滑动距离阈值，占宽度的比例
    //选择中颜色的ID
    private var selectCode: String = ""
    private var selectId: Long = 0
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        setStatusBar(StatusBarConfig)
        mViewModel.getMarketType()
        mViewModel.marketType.observe(viewLifecycleOwner) { it ->
            if (it == null) return@observe
            mBinding.llc.removeAllViews()
            var currentIndex = 0
            it.withIndex().forEach { (indexItems, items) ->
                val binding = LiveBetMenuFlexboxLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    mBinding.llc,
                    false
                )
                binding.apply {
                    if (currentIndex == it.size - 1) {
                        VLin.visibility = View.GONE
                    }
                    tvName.text = items.name
                }
                items.marketMenuBean.withIndex().forEach {(index, bean)->
                    val textBinding = LiveBetMenuFlexboxTextViewBinding.inflate(
                        LayoutInflater.from(context),
                        mBinding.llc,
                        false
                    )
                    if (bean.isSelect) {
                        selectCode = items.code
                        selectId = bean.marketId
                    }
                    textBinding.apply {
                        tvContent.text = bean.marketName
                        betOnViewModel.observeMarketMenu.value?.let {
                            if (indexItems== (it[0]-1)&&index==it[1]){
                                tvContent.isSelected = true
                            }
                        }
                        tvContent.clickNoRepeat {
                            betOnViewModel.setMarketMenuPosition((indexItems+1),index)
                            animateDismiss()
                        }
                    }
                    binding.flexboxLayout.addView(textBinding.root)
                }
                mBinding.llc.addView(binding.root)
                currentIndex++
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
        mBinding.llc.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    translationX = mBinding.root.translationX
                    isSwipingDialog = false
                    isHorizontalSwipe = false
                    // 阻止父容器拦截触摸事件
                    mBinding.main.requestDisallowInterceptTouchEvent(true)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - startX
                    val deltaY = event.rawY - startY
                    val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

                    if (!isSwipingDialog && distance > touchSlop) {
                        isSwipingDialog = true
                        val angle = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))
                        isHorizontalSwipe = abs(angle) < angleTolerance || abs(angle - 180) < angleTolerance
                        // 如果是垂直滑动，允许父容器处理
                        if (!isHorizontalSwipe) {
                            mBinding.main.requestDisallowInterceptTouchEvent(false)
                        }
                    }

                    if (isSwipingDialog && isHorizontalSwipe) {
                        if (deltaX >= 0) {
                            mBinding.root.translationX = translationX + deltaX
                        }
                        true
                    } else {
                        false
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isSwipingDialog && isHorizontalSwipe) {
                        val swipeDistance = mBinding.root.translationX
                        if (swipeDistance > mBinding.root.width * swipeThreshold) {
                            animateDismiss()
                        } else {
                            animateReset()
                        }
                    }
                    isSwipingDialog = false
                    isHorizontalSwipe = false
                    // 恢复父容器的触摸拦截
                    mBinding.main.requestDisallowInterceptTouchEvent(false)
                    true
                }

                else -> false
            }
        }
    }

    private fun animateDismiss() {
        mBinding.root.animate()
            .translationX(mBinding.root.width * 0.8f)
            .setDuration(200)
            .setInterpolator(AccelerateDecelerateInterpolator())
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
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(null)
            .start()
    }

}