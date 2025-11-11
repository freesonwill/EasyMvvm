package arch.cayenne.module.chat.utils

import android.view.VelocityTracker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.max

/**
 * @author: wenxi
 * @date: 9/8/25 14:54
 * @description:
 */
class EmojiScrollAlphaAnimHelper(private val recyclerView: RecyclerView) {
    private val layoutManager: GridLayoutManager by lazy {
        recyclerView.layoutManager as GridLayoutManager
    }
    private val spanCount: Int get() = layoutManager.spanCount
    private var velocityTracker: VelocityTracker? = null
    private val minVelocityForFastScroll = 2000f
    private var deleteButtonBottom: Int = 0
    private var deleteButtonTop: Int = 0
    private val deleteButtonHeight: Int = 32.dp2px + 12.dp2px

    //    private var isHide = false
    private val TAG = EmojiScrollAlphaAnimHelper::class.java.simpleName

    init {
        setup()
    }

//    fun updateUi(value:Boolean){
//        isHide = value
//    }

    private fun setup() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        velocityTracker = VelocityTracker.obtain()
//                        "SCROLL_STATE_DRAGGING".logd(TAG)
                    }

                    RecyclerView.SCROLL_STATE_IDLE -> {
//                        "SCROLL_STATE_IDLE".logd(TAG)
                        velocityTracker?.recycle()
                        velocityTracker = null
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTargetItemsVisibility()
                ensureAllNonTargetItemsVisible()
            }
        })
        recyclerView.post {
            deleteButtonBottom =
                recyclerView.bottom - deleteButtonHeight  //deleteButton top为底部  小于底部的alpha为0
            deleteButtonTop =
                deleteButtonBottom - deleteButtonHeight - 5.dp2px //减去item高度为顶部边界，当进入边界时对应的alpha值不断变小 大于顶部的alpha为1
//            "deleteButton $deleteButtonBottom  top ${deleteButtonTop}".logd(TAG)
            updateTargetItemsVisibility()
        }

        // 添加布局完成监听确保状态正确
//        recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
//            ensureAllNonTargetItemsVisible()
//        }
    }

    private fun updateTargetItemsVisibility() {
//        if(!isHide){
//            return
//        }

        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION) return

        // 1. 计算当前需要隐藏的position（最后两行的最后两个item）
        val lastRowStart = lastVisible - (lastVisible % spanCount)
        val secondLastRowStart = max(firstVisible, lastRowStart - spanCount)

        //最后一行到底部的距离
        val lastTop = layoutManager.findViewByPosition(lastRowStart)?.let {
            it.top //减去向上的4dp
        } ?: 0
//        //倒数第二行到底部的距离
        val secondTop = layoutManager.findViewByPosition(secondLastRowStart)?.let {
            it.top//减去向上的4dp
        } ?: 0

        val bottom = deleteButtonBottom - deleteButtonHeight / 2

        //竖直距离一值变大
        val lastAlpha =
            if (lastTop in deleteButtonTop + 1..<bottom) { // 进入到删除按钮-itemHeight区域  top -> bottom
                val value = BigDecimal(lastTop - deleteButtonTop).divide(
                    BigDecimal(deleteButtonHeight),
                    1,
                    RoundingMode.HALF_UP
                ).subtract(BigDecimal(1)).abs().toFloat()
                value
            } else if (lastTop >= bottom) { // 大于bottom alpha = 0 进入到button区域
                0f
            } else { //
                1f
            }

//        //最后一行最后两个item的位置
        val lastTargetList = mutableListOf<Int>().apply {
            add(lastRowStart)
            add(lastRowStart - 1)
            add(lastRowStart - 2)
        }

        //竖直距离一值变大
        val secondAlpha =
            if (secondTop in deleteButtonTop + 1..<bottom) { // 进入到删除按钮-itemHeight区域  top -> bottom
                val value = BigDecimal(secondTop - deleteButtonTop).divide(
                    BigDecimal(deleteButtonHeight),
                    1,
                    RoundingMode.HALF_UP
                ).subtract(BigDecimal(1)).abs().toFloat()
                value
            } else if (secondTop >= bottom) { // 大于bottom alpha = 0 进入到button区域
                0f
            } else { //
                1f
            }

        //倒数第二行最后两个item的位置
        val secondTargetList = mutableListOf<Int>().apply {
            add(secondLastRowStart)
            add(secondLastRowStart - 1)
            add(secondLastRowStart - 2)
        }

//        "secondAlpha $secondAlpha deletButtonm $deleteButtonBottom deleteTop $deleteButtonTop scecondTop $secondTop   secondLastRowStart $secondLastRowStart \n ${secondTargetList.toList()} ".logd(
//            TAG
//        )
//        "lastAlpha $lastAlpha deletButtonm $deleteButtonBottom deleteTop $deleteButtonTop lastTop $lastTop   lastRowStart $lastRowStart \n  ${lastTargetList.toList()}".logd(
//            TAG
//        )
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
        val list = arrayListOf(
            lastRowStart,
            lastRowStart - 1,
            lastRowStart - 2,
            secondLastRowStart,
            secondLastRowStart - 1,
            secondLastRowStart - 2,
        )
        return pos in list
    }

    private fun isFastScrolling(): Boolean {
        return velocityTracker?.let {
            it.computeCurrentVelocity(1000)
            abs(it.yVelocity) > minVelocityForFastScroll
        } ?: false
    }

    private fun applyAlpha(list: List<Int>, alpha: Float) {
        list.forEach {
            layoutManager.findViewByPosition(it)?.let {
                it.alpha = alpha
            }
        }
    }

    fun cleanup() {
        velocityTracker?.recycle()
        // 恢复所有被修改过的view
    }
}