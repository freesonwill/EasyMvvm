package arch.cayenne.lib.common.utils.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Color
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper.Companion.getAnimHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

fun ViewPager2.doSmartAnim(targetPosition: Int) {
    getAnimHelper()
        .let { helper ->
            helper.printLog("收到 doSmartAnim 請求，lastPosition=${if(helper.targetHistory.isEmpty()) "為空" else helper.lastPosition},targetPosition=$targetPosition")

            if (helper.targetHistory.isEmpty()) {
                helper.printLog("targetHistory 為空，等待加入目前頁面")
                post {
                    helper.printLog("目前頁面為 $currentItem，加入 targetHistory，並重新發送需求")
                    helper.targetHistory.add(currentItem)
                    doSmartAnim(targetPosition)
                }
                return
            }

            if (targetPosition == helper.lastPosition) {
                helper.printLog("目的地與上一個相同（$targetPosition），跳過動畫")
                return
            }

            if (targetPosition in listOf(helper.lastPosition - 1, helper.lastPosition + 1)) {
                helper.printLog("目的地（$targetPosition）與上一個（${helper.lastPosition}）相鄰，執行 ViewPager 預設動畫")
                helper.targetHistory.add(targetPosition)
                setCurrentItem(targetPosition, true)
                return
            }

            val secondLast = if (helper.targetHistory.size >= 2) helper.secondLastPosition else null
            helper.targetHistory.add(targetPosition)

            val isSwitchingBack = targetPosition == secondLast
            val isAnimating = helper.job?.isActive

            if (isSwitchingBack && isAnimating == true) {
                helper.printLog("偵測到動畫中來回切換，執行 updateAnimValue")
                helper.updateAnimValue()
            } else {
                helper.printLog("執行正常動畫 doAnim")
                helper.doAnim()
            }
        }
}

class ViewPagerAnimHelper(private val viewPager: ViewPager2) {
    internal val targetHistory = mutableListOf<Int>()
    internal val lastPosition: Int
        get() = targetHistory.last()
    internal val secondLastPosition: Int
        get() = targetHistory.dropLast(1).last()
    private val isPrev: Boolean
        get() = lastPosition > secondLastPosition

    private val scope: CoroutineScope = viewPager.findViewTreeLifecycleOwner()!!.lifecycleScope
    internal var job: Job? = null

    private val fakeViewPager: ImageView

    private var viewPagerAnimator: ValueAnimator? = null
    private var fakeViewPagerAnimator: ValueAnimator? = null
    private var animatorSet: AnimatorSet? = null
    private var animatorListener: AnimatorListenerAdapter? = null

    private val defaultDelayStart = 0L
    private val defaultDuration = 300L
    private val canLog = false

    init {
        check(viewPager.parent is ConstraintLayout) {
            "ViewPagerAnimHelper -> ViewPager 的父層必須為 ConstraintLayout"
        }

        // 自動創建 fakeViewPager
        fakeViewPager = ImageView(viewPager.context).apply {
            id = ImageView.generateViewId()
            isVisible = false
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            ).apply {
                topToTop = viewPager.id
                bottomToBottom = viewPager.id
                startToStart = viewPager.id
                endToEnd = viewPager.id
            }
            (viewPager.parent as ConstraintLayout).addView(this)
        }
    }

    fun resetHistory() {
        printLog("resetHistory: 清空 targetHistory")
        targetHistory.clear()
    }

    internal fun printLog(message: String) {
        val fragment = viewPager.findFragment<Fragment>()
        val name = fragment::class.java.simpleName
        if(canLog) println("[$TAG][$name] $message")
    }

    internal fun doAnim() {
        animatorSet?.cancel()
        job?.cancel()
        printLog("doAnim: 取消舊動畫 job=${job != null}")

        job = scope.launch {
            printLog("doAnim: 啟動新動畫 job")
            suspendCancellableCoroutine {
                val reset = {
                    printLog("動畫結束，執行 reset()")
                    fakeViewPager.apply {
                        isVisible = false
                        translationX = 0f
                        setImageDrawable(null)
                    }
                    viewPager.apply {
                        alpha = 1f
                        translationX = 0f
                    }
                    job?.cancel()
                    job = null

                    //TODO debug用
                    if(canLog) {
                        viewPager.setBackgroundColor(Color.TRANSPARENT)
                        fakeViewPager.setBackgroundColor(Color.TRANSPARENT)
                    }
                }

                if (animatorListener == null) {
                    printLog("初始化 animatorListener")
                    animatorListener = object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                            printLog("動畫開始")
                            viewPager.alpha = 1f

                            //TODO debug用
                            if(canLog) {
                                viewPager.setBackgroundColor(Color.RED)
                                fakeViewPager.setBackgroundColor(Color.YELLOW)
                            }
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            printLog("動畫正常結束")
                            reset()
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            printLog("動畫被取消")
                            reset()
                        }
                    }
                }

                captureViewPagerFroFake()
                viewPager.apply {
                    alpha = 0f
                    setCurrentItem(lastPosition, false)
                    doOnPreDraw {
                        printLog("開始設定並啟動動畫")
                        configAndStartAnimatorSet(
                            vpStart = if (isPrev) viewPager.width * 1f else viewPager.width * -1f,
                            vpEnd = 0f,
                            fakeStart = 0f,
                            fakeEnd = if (isPrev) viewPager.width * -1f else viewPager.width * 1f
                        )
                    }
                }
            }
        }
    }

    internal fun updateAnimValue() {
        val oldVpX = viewPager.translationX
        val oldFakeX = fakeViewPager.translationX
        printLog("updateAnimValue: oldVpX=$oldVpX, oldFakeX=$oldFakeX")

        animatorSet?.cancel()
        job?.cancel()
        printLog("updateAnimValue: 取消舊動畫 job=${job != null}")

        job = scope.launch {
            printLog("updateAnimValue: 啟動新動畫 job")
            suspendCancellableCoroutine {
                captureViewPagerFroFake(oldVpX)
                viewPager.apply {
                    alpha = 0f
                    setCurrentItem(lastPosition, false)
                    doOnLayout {
                        printLog("開始設定並啟動動畫（來回切換）")
                        viewPager.translationX = oldFakeX
                        configAndStartAnimatorSet(
                            vpStart = oldFakeX,
                            vpEnd = 0f,
                            fakeStart = oldVpX,
                            fakeEnd = if (isPrev) viewPager.width * -1f else viewPager.width * 1f
                        )
                    }
                }
            }
        }
    }

    private fun captureViewPagerFroFake(x: Float = 0f) {
        fakeViewPager.apply {
            setImageBitmap(viewPager.drawToBitmap())
            bringToFront()
            translationX = x
            isVisible = true
        }
    }

    private fun configAndStartAnimatorSet(vpStart: Float, vpEnd: Float, fakeStart: Float, fakeEnd: Float) {
        viewPager.doOnPreDraw {
            printLog("configAndStartAnimatorSet: 開始設定動畫值")
            setValueAnimators(vpStart, vpEnd, fakeStart, fakeEnd)
            animatorSet = viewPager.startSafeAnimateSet(
                duration = defaultDuration,
                start = true,
                config = {
                    printLog("啟動 animatorSet")
                    startDelay = defaultDelayStart
                    playTogether(viewPagerAnimator, fakeViewPagerAnimator)
                    addListener(animatorListener)
                }
            )
        }
    }

    private fun setValueAnimators(vpStart: Float, vpEnd: Float, fakeStart: Float, fakeEnd: Float) {
        printLog("setValueAnimators: 初始化 animator")
        printLog("ViewPager translationX: $vpStart → $vpEnd")
        printLog("FakeViewPager translationX: $fakeStart → $fakeEnd")

        viewPagerAnimator = null
        fakeViewPagerAnimator = null

        viewPagerAnimator = ValueAnimator.ofFloat(vpStart, vpEnd).apply {
            addUpdateListener { animation ->
                viewPager.translationX = animation.animatedValue as Float
            }
        }
        fakeViewPagerAnimator = ValueAnimator.ofFloat(fakeStart, fakeEnd).apply {
            addUpdateListener { animation ->
                fakeViewPager.translationX = animation.animatedValue as Float
            }
        }

        printLog("setValueAnimators: animator 建立完成")
    }

    companion object {
        private const val TAG = "ViewPagerAnimHelper"
        private const val KEY = 0x7f5a0123

        fun ViewPager2.getAnimHelper(): ViewPagerAnimHelper {
            return getTag(KEY) as? ViewPagerAnimHelper
                ?: ViewPagerAnimHelper(this)
                    .also { setTag(KEY, it) }
        }
    }
}