package arch.cayenne.lib.common.utils.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.math.max
import kotlin.math.min

fun ViewPager2.doSmartAnim(targetPosition: Int) {
    /**
     * doSmartAnim 切換邏輯：
     * 1. targetHistory 為空 → 表示未初始化，記錄目前頁面並重新執行
     * 2. 目標頁等於當前頁面的位置 → 無需動畫，跳過執行
     * 3. 動畫未開始，且目標頁與當前頁面相鄰 → 使用 ViewPager 預設動畫 setCurrentItem
     * 4. 動畫進行中，目標頁 = 原動畫的起始頁 → 判斷為相同兩頁快速來回切換，更新動畫值不中斷 (helper.doSwitchBack)
     * 5. 動畫進行中，目標頁 ≠ 原動畫的起始頁 →
     *    - 若目標頁 > 或 < 原動畫目標頁，且與原先目標頁相鄰 → 加速完成原動畫後 (helper.doSpeedUp)，再執行 ViewPager 預設動畫 setCurrentItem
     *    - 若目標頁 > 或 < 原動畫目標頁，但與原先目標頁不相鄰 → 加速完成原動畫後 (helper.doSpeedUp)，再執行新動畫(加速版)
     *    - 若目標頁介於起始頁與目標頁之間 → 加速完成原動畫後，再執行新動畫(加速版)
     * 6. 其他 → 執行自定義動畫（helper.doAnim）
     */
    getAnimHelper()
        .let { helper ->
            helper.preJob?.apply {
                cancel()
                helper.printLog("doSmartAnim: 取消 preJob")
            }

            helper.preJob = helper.scope.launch {
                helper.printLog("doSmartAnim: 啟動新的 preJob")
                helper.printLog("當前 targetHistory=[${helper.targetHistory.joinToString(",")}], targetPosition=$targetPosition")
                helper.printLog("收到 doSmartAnim 請求，lastPosition=${if (helper.targetHistory.isEmpty()) "為空" else helper.lastPosition},targetPosition=$targetPosition")

                if (helper.targetHistory.isEmpty()) {
                    helper.printLog("targetHistory 為空，等待加入目前頁面")
                    helper.printLog("doSmartAnim: 取消當前 retryJob")
                    helper.retryJob?.cancel()
                    helper.retryJob = helper.scope.launch {
                        helper.printLog("doSmartAnim: 啟動新的 retryJob")
                        delay(50L)
                        helper.printLog("目前頁面為 $currentItem，加入 targetHistory，並重新發送需求")
                        helper.pushToHistory(currentItem)
                        doSmartAnim(targetPosition)
                    }
                    return@launch
                }

                if (targetPosition == helper.lastPosition) {
                    helper.printLog("目的地與上一個相同（$targetPosition），跳過動畫")
                    return@launch
                }

                val secondLast =
                    if (helper.targetHistory.size >= 2) helper.secondLastPosition else null
                val isSwitchingBack = targetPosition == secondLast
                val isAnimating = helper.job?.isActive == true

                if (!isAnimating) {
                    if (targetPosition in listOf(helper.lastPosition - 1, helper.lastPosition + 1)) {
                        helper.printLog("目的地（$targetPosition）與上一個（${helper.lastPosition}）相鄰，執行 ViewPager 預設動畫")
                        helper.pushToHistory(targetPosition)
                        setCurrentItem(targetPosition, true)
                        return@launch
                    } else {
                        helper.printLog("執行正常動畫 doAnim")
                        helper.pushToHistory(targetPosition)
                        helper.doAnim()
                    }
                } else {
                    if (isSwitchingBack) {
                        helper.printLog("偵測到動畫中來回切換，執行 doSwitchBack")
                        helper.pushToHistory(targetPosition)
                        helper.doSwitchBack()
                    } else {
                        val isInBetween = targetPosition in min(
                            helper.lastPosition,
                            helper.secondLastPosition
                        )..max(helper.lastPosition, helper.secondLastPosition)
                        val isNearBy = targetPosition in listOf(
                            helper.lastPosition - 1,
                            helper.lastPosition + 1
                        )
                        val dealyDuration = 5L

                        val action =
                            if (isInBetween) {
                                {
                                    helper.printLog("目標頁（$targetPosition）在原動畫起（${helper.secondLastPosition}）訖（${helper.lastPosition}）頁之間")
                                    helper.pushToHistory(targetPosition)
                                    postDelayed({ helper.doAnim(helper.secondAnimDuration) }, dealyDuration)
                                    Unit
                                }
                            } else if (isNearBy) {
                                {
                                    helper.printLog("目的地（$targetPosition）與上一個（${helper.lastPosition}）相鄰，執行 ViewPager 預設動畫")
                                    helper.pushToHistory(targetPosition)
                                    setCurrentItem(targetPosition, true)
                                }
                            } else {
                                {
                                    val direction =
                                        if (targetPosition > helper.lastPosition) "大於原動畫目標頁" else "小於原動畫起始頁"
                                    helper.printLog("目標頁（$targetPosition）$direction（${helper.lastPosition}），執行新動畫 doAnim")
                                    helper.pushToHistory(targetPosition)
                                    postDelayed({ helper.doAnim(helper.secondAnimDuration) }, dealyDuration)
                                    Unit
                                }
                            }
                        helper.doSpeedUp(action)
                    }
                }
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

    internal val scope: CoroutineScope = viewPager.findViewTreeLifecycleOwner()!!.lifecycleScope
    internal var preJob: Job? = null
    internal var job: Job? = null
    internal var retryJob: Job? = null

    private val fakeViewPager: ImageView

    private var viewPagerAnimator: ValueAnimator? = null
    private var fakeViewPagerAnimator: ValueAnimator? = null
    private var animatorSet: AnimatorSet? = null
    private var animatorListener: AnimatorListenerAdapter? = null
    private var reset: (() -> Unit)? = null
    private var customOnAnimEnd: (() -> Unit)? = null

    private val defaultDelayStart = 0L
    private val defaultDuration = 300L
    internal val secondAnimDuration = defaultDuration / 3L
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
        preJob?.apply {
            cancel()
            printLog("resetHistory: 取消當前 preJob")
        }
        job?.apply {
            cancel()
            printLog("resetHistory: 取消當前 job")
        }
        retryJob?.apply {
            cancel()
            printLog("resetHistory: 取消當前 retryJob")
        }
    }

    internal fun pushToHistory(position: Int) {
        if (targetHistory.size >= 2) {
            targetHistory.removeAt(0)
        }
        targetHistory.add(position)
    }

    internal fun printLog(message: String) {
        val fragment = viewPager.findFragment<Fragment>()
        val name = fragment::class.java.simpleName
        val jobHash = preJob?.hashCode()?.let { "[$it]" }
        if(canLog) println("[$TAG][$name]$jobHash $message")
    }

    internal fun doAnim(duration: Long = defaultDuration) {
        animatorSet?.cancel()
        job?.apply {
            cancel()
            printLog("doAnim: 取消舊動畫")
        }

        job = scope.launch {
            printLog("doAnim: 啟動新動畫 job, 從（$secondLastPosition）到（${lastPosition}）")
            suspendCancellableCoroutine {
                if(reset == null) {
                    reset = {
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
                        customOnAnimEnd = null


                    }
                }

                if (animatorListener == null) {
                    printLog("初始化 animatorListener")
                    animatorListener = object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                            printLog("動畫開始")
                            viewPager.alpha = 1f
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            printLog("動畫正常結束")
                            customOnAnimEnd?.invoke()
                            reset?.invoke()
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            printLog("動畫被取消")
                            reset?.invoke()
                        }
                    }
                }

                captureViewPagerForFake {
                    viewPager.apply {
                        alpha = 0f
                        setCurrentItem(lastPosition, false)
                        doOnPreDraw {
                            printLog("開始設定並啟動動畫")
                            configAndStartAnimatorSet(
                                vpStart = if (isPrev) viewPager.width * 1f else viewPager.width * -1f,
                                vpEnd = 0f,
                                fakeStart = 0f,
                                fakeEnd = if (isPrev) viewPager.width * -1f else viewPager.width * 1f,
                                duration = duration
                            )
                        }
                    }
                }
            }
        }
    }

    internal fun doSwitchBack() {
        val oldVpX = viewPager.translationX
        val oldFakeX = fakeViewPager.translationX
        printLog("doSwitchBack: oldVpX=$oldVpX, oldFakeX=$oldFakeX")

        animatorSet?.cancel()
        job?.apply {
            cancel()
            printLog("doSwitchBack: 取消舊動畫")

        }
        job = scope.launch {
            printLog("doSwitchBack: 啟動新動畫 job, 從（$secondLastPosition）到（${lastPosition}）")
            suspendCancellableCoroutine {
                captureViewPagerForFake(oldVpX) {
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
    }

    internal fun doSpeedUp(onEnd: (() -> Unit)? = null) {
        val oldVpX = viewPager.translationX
        val oldFakeX = fakeViewPager.translationX
        printLog("doSpeedUp: oldVpX=$oldVpX, oldFakeX=$oldFakeX")

        animatorSet?.cancel()
        job?.apply {
            cancel()
            printLog("doSpeedUp: 取消舊動畫")
        }

        customOnAnimEnd = onEnd

        job = scope.launch {
            printLog("doSpeedUp: 啟動新動畫 job")
            suspendCancellableCoroutine {
                viewPager.apply {
                    printLog("doSpeedUp: 開始設定並啟動動畫")
                    configAndStartAnimatorSet(
                        vpStart = oldVpX,
                        vpEnd = 0f,
                        fakeStart = oldFakeX,
                        fakeEnd = if (isPrev) viewPager.width * -1f else viewPager.width * 1f,
                        duration = secondAnimDuration
                    )
                }
            }
        }
    }

    private fun captureViewPagerForFake(x: Float = 0f, onReady: (() -> Unit)? = null) {
        fakeViewPager.apply {
            setImageBitmap(viewPager.drawToBitmap())
            bringToFront()
            translationX = x
            isVisible = true

            doOnLayout {
                onReady?.invoke()
            }
        }
    }

    private fun configAndStartAnimatorSet(vpStart: Float, vpEnd: Float, fakeStart: Float, fakeEnd: Float, duration: Long = defaultDuration) {
        viewPager.doOnPreDraw {
            printLog("configAndStartAnimatorSet: 開始設定動畫值")
            setValueAnimators(vpStart, vpEnd, fakeStart, fakeEnd)
            animatorSet = viewPager.startSafeAnimateSet(
                duration = duration,
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