package com.walisport.module.search.ui.view;

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.walisport.module.search.R
import com.walisport.module.search.databinding.ItemSearchHistoryBinding
import com.walisport.module.search.ui.view.FlowAdapter.OnDataChangedListener
import kotlin.math.max

/**
 * 支援自動換行、折疊展開與刪除模式的自訂 ViewGroup。
 * 會根據設定的行數限制顯示項目，並自動顯示「展開/收合」按鈕。
 *
 * 適用場景：例如搜尋歷史紀錄、Tag 流式排列等。
 */
class FlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), OnDataChangedListener {

    // 子項目之間的水平/垂直間距（單位為 px）
    private val horizontalSpacing = 8f.dp2px
    private val verticalSpacing = 8.5f.dp2px

    // 最大可折疊行數，預設為 1 行
    private var maxFoldLines = 1

    // 是否處於折疊狀態
    private var isFold = true

    // 是否處於刪除模式（會強制展開、顯示關閉按鈕）
    private var isDeleteMode = false

    // Adapter 與子 View 管理
    private var adapter: FlowAdapter<String, FlowViewHolder, ItemSearchHistoryBinding>? = null
    private val visibleChildren = mutableListOf<View>()
    private val normalChildren = mutableListOf<View>()

    // 展開與收合按鈕
    private val expandButton: SkinnableImageView
    private val collapseButton: SkinnableImageView

    init {
        expandButton = R.drawable.ic_expand.genButton(true)
        collapseButton = R.drawable.ic_collapse.genButton(false)

        addView(expandButton)
        addView(collapseButton)
    }

    /**
     * 建立展開/收合按鈕，並綁定點擊事件。
     */
    private fun Int.genButton(isExpandBtn: Boolean): SkinnableImageView {
        return SkinnableImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            tag = if (isExpandBtn) "expandButton" else "collapseButton"
            setOnClickListener { setFold(!isExpandBtn) }
            setImageResource(this@genButton)
        }
    }

    /**
     * 設定是否為折疊狀態，並重繪 layout。
     */
    private fun setFold(fold: Boolean) {
        if (isDeleteMode) return

        if (isFold != fold) {
            isFold = fold
            requestLayout()
        }
    }

    /**
     * 將 adapter 的子項目 View 加入 ViewGroup。
     */
    private fun updateView() {
        removeAllTagViews()

        adapter?.let { adapter ->
            for (i in 0 until adapter.getItemCount()) {
                val holder = adapter.onCreateViewHolder(this, 0)
                adapter.onBindViewHolder(holder, i)
                addView(holder.itemView)
            }
        } ?: throw RuntimeException("adapter cannot be empty")
    }

    /**
     * 移除所有資料子 View，保留 expand/collapse 按鈕。
     */
    private fun removeAllTagViews() {
        val preserved = listOf(expandButton, collapseButton)
        val toRemove = (0 until childCount)
            .map { getChildAt(it) }
            .filter { it !in preserved }

        toRemove.forEach { removeView(it) }
    }

    /**
     * 設定最大折疊行數，至少為 1。
     */
    fun setMaxFoldLines(lines: Int) {
        maxFoldLines = max(1, lines)
        requestLayout()
    }

    /**
     * 開啟或關閉刪除模式。
     */
    fun setDeleteMode(deleteMode: Boolean) {
        if (isDeleteMode != deleteMode) {
            isDeleteMode = deleteMode
            isFold = !isDeleteMode
            requestLayout()
        }
    }

    /**
     * 綁定資料 Adapter。
     */
    fun setAdapter(adapter: FlowAdapter<String, FlowViewHolder, ItemSearchHistoryBinding>?) {
        this.adapter = adapter
        this.adapter?.apply {
            setOnDataChangedListener(this@FlowLayout)
            notifyDataChanged()
        }
    }

    /**
     * FlowAdapter 資料變動時會呼叫，重新繪製內容。
     */
    override fun onChanged() {
        updateView()
    }

    /**
     * 排列與測量所有子項目，依據是否折疊決定顯示數量。
     * 並根據狀態決定是否顯示展開/收合按鈕。
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 取得寬度（包含 padding）
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        // 扣掉左右 padding 得到實際可用寬度
        val layoutWidth = widthSize - paddingLeft - paddingRight

        // 如果寬度為 0，直接設定高度為 0 並結束
        if (layoutWidth <= 0) {
            setMeasuredDimension(widthSize, 0)
            return
        }

        // Step 1: 收集要排列的子 View（排除 expand/collapse 按鈕）
        visibleChildren.clear()
        normalChildren.clear()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child === expandButton || child === collapseButton) continue
            normalChildren.add(child)
        }

        // Step 2: 初始化排版資訊
        var lineWidth = 0          // 當前行的寬度
        var lineHeight = 0         // 當前行的最大高度
        var totalHeight = paddingTop + paddingBottom
        var lineCount = 1
        var visibleCount = normalChildren.size  // 最後要顯示的 item 數量，預設全顯示

        // Step 3: 若是折疊模式 + 非刪除模式，計算最多能顯示多少個 child
        if (isFold && !isDeleteMode) {
            visibleCount = 0
            lineWidth = 0
            lineHeight = 0
            lineCount = 1

            for (child in normalChildren) {
                // 先測量 child 大小，預估其寬高
                child.measure(
                    MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )

                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                // 預估加進這個 child 後是否會超出本行寬度
                val nextLineWidth = if (lineWidth == 0) childWidth else lineWidth + horizontalSpacing + childWidth

                if (nextLineWidth > layoutWidth) {
                    // 換行
                    lineCount++
                    if (lineCount > maxFoldLines) {
                        // 超過最大行數，停止計算
                        break
                    }

                    // 計算上一行的高度並 reset 本行參數
                    totalHeight += verticalSpacing + lineHeight
                    lineWidth = childWidth
                    lineHeight = childHeight
                } else {
                    // 不需換行，繼續累加寬度與更新本行最大高度
                    lineWidth = nextLineWidth
                    lineHeight = max(lineHeight, childHeight)
                }

                visibleCount++
            }

            // 把最後一行高度也加進 totalHeight
            totalHeight += lineHeight
        } else {
            // Step 4: 若是展開狀態或刪除模式，全顯示 child 並排版高度
            for (child in normalChildren) {
                child.measure(
                    MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )

                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                val nextLineWidth = if (lineWidth == 0) childWidth else lineWidth + horizontalSpacing + childWidth

                if (nextLineWidth > layoutWidth) {
                    // 換行
                    totalHeight += verticalSpacing + lineHeight
                    lineWidth = childWidth
                    lineHeight = childHeight
                } else {
                    // 同一行繼續排
                    lineWidth = nextLineWidth
                    lineHeight = max(lineHeight, childHeight)
                }
            }

            // 最後一行高度補上
            totalHeight += lineHeight
        }

        // Step 5: 根據計算結果記錄實際要顯示的 View
        visibleChildren.addAll(normalChildren.take(visibleCount))

        // Step 6: 測量展開與收合按鈕的尺寸（以便後面判斷是否放得下）
        val heightSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        expandButton.measure(
            MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST),
            heightSpec
        )
        collapseButton.measure(
            MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST),
            heightSpec
        )

        // Step 7: 判斷是否需要顯示按鈕
        val shouldShowButton = when {
            isDeleteMode -> false  // 刪除模式禁止顯示按鈕
            isFold -> normalChildren.size > visibleChildren.size  // 折疊但被折掉了才顯示展開
            else -> true  // 展開狀態總是要顯示「收合」按鈕
        }

        if (shouldShowButton) {
            val btn = if (isFold) expandButton else collapseButton
            val otherBtn = if (isFold) collapseButton else expandButton
            val btnWidth = btn.measuredWidth

            // 按鈕是否能塞入最後一行？否的話移除一個 item 給它空間
            val fitsInLastLine = lineWidth + horizontalSpacing + btnWidth <= layoutWidth
            if (!fitsInLastLine && visibleChildren.isNotEmpty()) {
                visibleChildren.removeAt(visibleChildren.lastIndex)
            }

            btn.visibility = VISIBLE
            otherBtn.visibility = GONE
        } else {
            // 不需顯示按鈕時，兩顆都隱藏
            expandButton.visibility = GONE
            collapseButton.visibility = GONE
        }

        // Step 8: 設定 FlowLayout 的實際寬高
        setMeasuredDimension(widthSize, resolveSize(totalHeight, heightMeasureSpec))
    }

    /**
     * 將所有 visible children 與展開/收合按鈕定位到正確位置。
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val layoutWidth = width - paddingLeft - paddingRight
        var x = paddingLeft
        var y = paddingTop
        var lineHeight = 0

        for (child in visibleChildren) {
            val cw = child.measuredWidth
            val ch = child.measuredHeight

            if (x + cw > layoutWidth + paddingLeft) {
                x = paddingLeft
                y += lineHeight + verticalSpacing
                lineHeight = ch
            } else {
                lineHeight = max(lineHeight, ch)
            }

            child.visibility = VISIBLE
            child.layout(x, y, x + cw, y + ch)
            x += cw + horizontalSpacing
        }

        // 隱藏不可見的項目（刪除模式會顯示所有）
        normalChildren
            .filter { it !in visibleChildren }
            .forEach {
                it.visibility = if (isFold) GONE else VISIBLE
            }

        // 設定展開/收合按鈕位置
        if (!isDeleteMode && (!isFold || normalChildren.size > visibleChildren.size)) {
            val btn = if (isFold) expandButton else collapseButton
            val btnW = btn.measuredWidth
            val btnH = btn.measuredHeight

            if (x + btnW > layoutWidth + paddingLeft) {
                x = paddingLeft
                y += lineHeight + verticalSpacing
            }

            btn.layout(x, y, x + btnW, y + btnH)
        }
    }
}