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
import arch.cayenne.lib.base.ui.BaseSideSheetDialogFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.databinding.FragmentLiveBetOnMenuBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxLayoutBinding
import com.walisport.module.live.databinding.LiveBetMenuFlexboxTextViewBinding
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import kotlin.math.abs
import kotlin.reflect.KClass
import android.view.ViewConfiguration
import arch.cayenne.lib.base.data.StatusBarConfig
import kotlin.math.atan2
import kotlin.math.sqrt

class LiveBetOnMenuFragment :
    BaseSideSheetDialogFragment<LiveBetOnMenuViewModel, FragmentLiveBetOnMenuBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnMenuBinding> = FragmentLiveBetOnMenuBinding::class
    override val vmClass: KClass<LiveBetOnMenuViewModel> = LiveBetOnMenuViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    private var startX = 0f
    private var startY = 0f
    private var translationX = 0f
    private var isSwipingDialog = false
    private var isHorizontalSwipe = false
    private val touchSlop by lazy { ViewConfiguration.get(mBinding.main.context).scaledTouchSlop }
    private val swipeThreshold = 0.3f // 滑动阈值，30% 宽度
    private val angleTolerance = 30.0 // 允许的偏差角度（度）

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.getMarketType()
        mViewModel.updateMarket.observe(viewLifecycleOwner){
            mViewModel.getMarketType()
        }
        mViewModel.marketType.observe(viewLifecycleOwner){ it ->
            mBinding.llc.removeAllViews()
            val groupedByName: Map<String, List<MarketTypeBean>>? = it?.groupBy {
                it.name
            }
            var currentIndex = 0
            groupedByName?.forEach { (name, items) ->
                val binding = LiveBetMenuFlexboxLayoutBinding.inflate(LayoutInflater.from(context), mBinding.llc, false)
                binding.apply {
                    if (currentIndex == groupedByName.size - 1) {
                        VLin.visibility = View.GONE
                    }
                    tvName.text = name
                }
                //选择中颜色的ID
               var select:Long = 0
                items.forEach {
                    val textBinding = LiveBetMenuFlexboxTextViewBinding.inflate(LayoutInflater.from(context), mBinding.llc, false)
                    if (it.isSelect){
                        select = it.marketId
                    }
                    textBinding.apply {
                        tvContent.text = it.marketName
                        tvContent.isSelected = it.isSelect
                        tvContent.clickNoRepeat {s->
                            mViewModel.setMarketSelect(it.marketId,select,true)
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
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - startX
                    val deltaY = event.rawY - startY
                    val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

                    // 仅在滑动距离超过 touchSlop 时判断方向
                    if (!isSwipingDialog && distance > touchSlop) {
                        isSwipingDialog = true
                        // 计算滑动角度（相对于水平方向）
                        val angle = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))
                        // 水平滑动：角度接近 0° 或 180°，允许 ±angleTolerance 偏差
                        isHorizontalSwipe = abs(angle) < angleTolerance || abs(angle - 180) < angleTolerance
                    }

                    // 处理滑动
                    if (isSwipingDialog && isHorizontalSwipe) {
                        if (deltaX >= 0) {
                            mBinding.root.translationX = translationX + deltaX
                        }
                        true // 消费水平滑动事件
                    } else {
                        false // 允许垂直滑动事件传递
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