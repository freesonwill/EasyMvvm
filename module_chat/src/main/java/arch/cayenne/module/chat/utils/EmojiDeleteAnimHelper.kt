package arch.cayenne.module.chat.utils

import android.view.VelocityTracker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import kotlin.math.abs
import kotlin.math.max

/**
 * @author: wenxi
 * @date: 9/8/25 14:54
 * @description:
 */
class EmojiDeleteAnimHelper(private val recyclerView: RecyclerView) {
    private val layoutManager: GridLayoutManager by lazy {
        recyclerView.layoutManager as GridLayoutManager
    }
    private val spanCount: Int get() = layoutManager.spanCount
    private var velocityTracker: VelocityTracker? = null
    private val minVelocityForFastScroll = 2000f
    private var deleteButtonBottom:Int = 0
    private var deleteButtonTop:Int = 0
    private val deleteButtonHeight:Int = 46.dp2px
    private var isHide = false

    init {
        setup()
    }

    fun updateUi(value:Boolean){
        isHide = value
    }

    private fun setup() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        velocityTracker = VelocityTracker.obtain()
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        velocityTracker?.recycle()
                        velocityTracker = null
                        ensureAllNonTargetItemsVisible()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTargetItemsVisibility()
            }
        })
        recyclerView.post {
            deleteButtonBottom = recyclerView.bottom
            deleteButtonTop = deleteButtonBottom - 46.dp2px-30.dp2px
            updateTargetItemsVisibility()
        }

        // 添加布局完成监听确保状态正确
        recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            ensureAllNonTargetItemsVisible()
        }
    }

    private fun updateTargetItemsVisibility() {
        if(!isHide){
            return
        }

        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION) return

        // 1. 计算当前需要隐藏的position（最后两行的最后两个item）
        val lastRowStart = lastVisible - (lastVisible % spanCount)
        val secondLastRowStart = max(firstVisible, lastRowStart - spanCount)

        //最后一行到底部的距离
        val lastTop = layoutManager.findViewByPosition(lastRowStart)?.let {
            it.top + 4.dp2px //减去向上的4dp
             } ?:0
//        //倒数第二行到底部的距离
        val secondTop = layoutManager.findViewByPosition(secondLastRowStart)?.let {
            it.top+4.dp2px//减去向上的4dp
        }?:0

        val lastBottom = lastTop+30.dp2px //emoji实际高度30dp
        val secondBottom = secondTop+30.dp2px

        val lastAlpha: Float = if (deleteButtonBottom in (lastTop + 1)..<lastBottom) { //item行从底部开始滑入到deleteButton距离但没有完全滑入 滑出同理
                val value = Math.abs(lastBottom - deleteButtonBottom) //随着外部bootom高度减少，alpha值越小
                val alpha = "%.1f".format((value.toFloat() / 42.dp2px)).toFloat()
                 alpha
            } else if (deleteButtonTop in (lastTop + 1)..<lastBottom) {// item行从deleteButton区域向上滑动,但没有完全滑出 滑入同理
                val value =Math.abs(lastTop-deleteButtonTop) //随着外部top高度减少，alpha值越小
                val alpha = "%.1f".format((value.toFloat()/42.dp2px)).toFloat()
              alpha
            } else if (lastTop > deleteButtonTop || lastBottom < deleteButtonBottom) {//item行完全滑入到了deleteButton区域，被隐藏了
                 0f
            } else {
                 1f
            }

        val secondAlpha: Float = if (deleteButtonBottom in (secondTop)..<secondBottom+1) { //item行从底部开始滑入到deleteButton距离但没有完全滑入 滑出同理
            val value = Math.abs(secondBottom - deleteButtonBottom)
            val alpha = "%.1f".format((value.toFloat() / 42.dp2px)).toFloat()
            alpha
        } else if (deleteButtonTop in (secondTop)..<secondBottom+1) {// item行从deleteButton区域向上滑动,但没有完全滑出 滑入同理
            val value =Math.abs(secondTop-deleteButtonTop)
            val alpha = "%.1f".format((value.toFloat()/42.dp2px)).toFloat()
            alpha
        } else if (secondTop >= deleteButtonTop && secondBottom <= deleteButtonBottom) {//item行完全滑入到了deleteButton区域，被隐藏了
            0f
        } else {
            1f
        }


        //最后一行最后两个item的位置
        val lastTargetList = mutableListOf<Int>().apply {
            (spanCount - 2 until spanCount).forEach { offset ->
                val pos = lastRowStart + offset
                if (pos <= lastVisible) add(pos)
            }
        }
        //倒数第二行最后两个item的位置
        val secondTargetList = mutableListOf<Int>().apply {
            (spanCount - 2 until spanCount).forEach { offset ->
                val pos = secondLastRowStart + offset
                if (pos <= lastVisible) add(pos)
            }
        }
        applyAlpha(secondTargetList, secondAlpha)
        applyAlpha(lastTargetList, lastAlpha)

//        val isFastScrolling = isFastScrolling()


        // 2. 处理所有可见item
//        (firstVisible..lastVisible).forEach { pos ->
//            layoutManager.findViewByPosition(pos)?.let { view ->
//                val shouldHide = pos in currentTargetPositions
//                val currentAlpha = view.alpha
//
//                when {
//                    // 是需要隐藏的item且当前未隐藏
//                    shouldHide && currentAlpha != 0f -> {
//                        modifiedViews[pos] = currentAlpha // 保存原始alpha
//                        applyAlpha(view, 0f, isFastScrolling)
//                    }
//
//                    // 不是目标item但被错误隐藏了
//                    !shouldHide && currentAlpha != 1f -> {
//                        // 恢复为记录的原始alpha或默认1f
//                        val targetAlpha = modifiedViews.remove(pos) ?: 1f
//                        applyAlpha(view, targetAlpha, isFastScrolling)
//                    }
//                }
//            }
//        }
    }

    private fun ensureAllNonTargetItemsVisible() {
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION) return

        // 强制恢复所有非目标item的显示状态
        (firstVisible..lastVisible).forEach { pos ->
            layoutManager.findViewByPosition(pos)?.let { view ->
                if (view.alpha != 1f && !isPositionInTargetArea(pos, firstVisible, lastVisible)) {
                    val targetAlpha = 1f
                    view.alpha = targetAlpha
                }
            }
        }
    }

    private fun isPositionInTargetArea(pos: Int, firstVisible: Int, lastVisible: Int): Boolean {
        val lastRowStart = lastVisible - (lastVisible % spanCount)
        val secondLastRowStart = max(firstVisible, lastRowStart - spanCount)

        return pos in listOf(secondLastRowStart, lastRowStart).flatMap { rowStart ->
            (spanCount - 2 until spanCount).map { rowStart + it }
        } && pos <= lastVisible
    }

    private fun isFastScrolling(): Boolean {
        return velocityTracker?.let {
            it.computeCurrentVelocity(1000)
            abs(it.yVelocity) > minVelocityForFastScroll
        } ?: false
    }

    private fun applyAlpha(list:List<Int>,alpha:Float) {
        list.forEach {
            layoutManager.findViewByPosition(it)?.let {
                it.alpha = alpha
            }
        }

//        if (fastScroll || view.alpha == targetAlpha) {
//            view.alpha = targetAlpha
//        } else {
//            view.animate().cancel() // 取消可能存在的动画
//        }
    }

//    private fun applyAlpha(view: View, targetAlpha: Float, fastScroll: Boolean) {
//        if (fastScroll || view.alpha == targetAlpha) {
//            view.alpha = targetAlpha
//        } else {
//            view.animate().cancel() // 取消可能存在的动画
//            view.animate()
//                .alpha(targetAlpha)
//                .setDuration(200)
//                .start()
//        }
//    }

    fun cleanup() {
        velocityTracker?.recycle()
        // 恢复所有被修改过的view
    }
}