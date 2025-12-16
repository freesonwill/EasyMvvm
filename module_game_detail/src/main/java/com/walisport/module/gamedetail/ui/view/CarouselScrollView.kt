package com.walisport.module.gamedetail.ui.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.isEmpty
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.ui.adapter.CarouselAdapter
import com.walisport.module.gamedetail.ui.viewholder.CarouselViewHolder
import kotlin.math.abs
import kotlin.math.roundToInt

class CarouselScrollView(context: Context, attrs: AttributeSet?) :
    HorizontalScrollView(context, attrs), GestureDetector.OnGestureListener {

    /**
     * 定義輪播視圖的兩種主要狀態。
     */
    enum class State {
        CAROUSEL,   // 輪播狀態：所有項目都是縮小尺寸，可以自由滾動。
        MAGNIFIED   // 放大狀態：中心項目被放大，視圖處於靜止狀態。
    }

    /**
     * 定義觸發狀態變更的來源。
     */
    enum class TriggerSource {
        USER_ACTION,    // 由用戶手勢（點擊、滑動）觸發。
        PROGRAMMATIC    // 由程式碼呼叫（如 gotoPage）觸發。
    }

    /**
     * CarouselScrollView 中子項可以實現的接口。
     * 用於接收來自 CarouselScrollView 的狀態通知，例如居中或失焦。
     */
    interface CarouselItem {
        // 當此項目滾動到中心並被放大時呼叫。
        fun onCentered()

        // 當此項目失去中心焦點並被縮小時呼叫。
        fun onLostFocus()
    }


    private val adapterDataObserver = {
        // 當 adapter.notifyDataSetChanged() 被呼叫時，執行此處的邏輯
        resetAndPopulate()
    }

    var adapter: CarouselAdapter<*>? = null
        set(value) {
            if(field != null) {
                // 移除舊 adapter 的觀察者
                field?.unregisterAdapterDataObserver(adapterDataObserver)
                field?.detachFromCarousel()
            }
            field = value
            indicatorView?.setPageCount(value?.getItemCount() ?: 0)
            if(value != null) {
                // 註冊新 adapter 的觀察者並附加
                value.registerAdapterDataObserver(adapterDataObserver)
                value.attachToCarousel(this)
                // 重置並重新填充視圖
                resetAndPopulate()
            } else {
                // 如果設置為 null，清理所有資源，防止 memory leak
                cleanupAllViews()
            }
        }

    // 內部狀態
    private var currentState = State.CAROUSEL
    private var currentIndex = 0
    private var totalItemCount = 0

    // 尺寸與佈局
    var itemWidth = 0
    var itemHeight = 0
    var shrinkTopOffset = 0// shrink 狀態下的額外垂直偏移量
    var shrinkBottomOffset = 0
    var itemSpacing = 0
    var itemCornerRadius = 0f
    private var screenCenter = 0
    private var verticalAlignToId: Int = -1 // 用於儲存外部 Guideline 的資源 ID
    private var verticalAlignToBottomId: Int = -1
    private var indicatorId: Int = -1
    private var indicatorView: CarouselIndicator? = null

    // 滾動與動畫
    private val scroller: OverScroller = OverScroller(context)
    private val handler = Handler(Looper.getMainLooper())

    // 追蹤所有活躍的動畫，以便在 view detach 時取消，防止 memory leak
    private val activeAnimators = mutableListOf<ValueAnimator>()

    // 程式碼控制
    private var isProgrammaticScroll = false    // 標記當前的滾動是否由程式碼（如 `gotoPage`）觸發。由 `gotoPage` 和 `magnifyCenterItem` 的回調共同管理，確保其覆蓋整個非同步動畫鏈。
    private var pendingMagnify = false          // true 表示當前的滾動動畫結束後，應該觸發一次放大

    // 監聽器
    private var onPageChangeListener: ((Int) -> Unit)? = null
    private var onStateChangeListener: ((newState: State, source: TriggerSource) -> Unit)? = null

    // 視圖與手勢
    private val container: LinearLayout = LinearLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        orientation = LinearLayout.HORIZONTAL
    }
    private val gestureDetector: GestureDetector = GestureDetector(context, this)


    init {
        clipToPadding = false

        if(attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CarouselScrollView
            )
            try {
                itemWidth = typedArray.getDimensionPixelSize(
                    R.styleable.CarouselScrollView_carousel_itemWidth,
                    0 // 預設值
                )
                itemHeight = typedArray.getDimensionPixelSize(
                    R.styleable.CarouselScrollView_carousel_itemHeight,
                    0 // 預設值
                )
                itemSpacing = typedArray.getDimensionPixelSize(
                    R.styleable.CarouselScrollView_carousel_itemSpacing,
                    0 // 預設值
                )
                itemCornerRadius = typedArray.getDimension(
                    R.styleable.CarouselScrollView_carousel_itemCornerRadius,
                    0f // 預設值
                )
                verticalAlignToId = typedArray.getResourceId(
                    R.styleable.CarouselScrollView_carousel_verticalAlignTo,
                    -1 // 如果 XML 中未設置，則為 -1
                )
                verticalAlignToBottomId = typedArray.getResourceId(
                    R.styleable.CarouselScrollView_carousel_verticalAlignToBottom,
                    -1 // 如果 XML 中未設置，則為 -1
                )
                shrinkTopOffset = typedArray.getDimensionPixelSize(
                    R.styleable.CarouselScrollView_carousel_shrinkTopOffset,
                    0 // 預設值
                )
                shrinkBottomOffset = typedArray.getDimensionPixelSize(
                    R.styleable.CarouselScrollView_carousel_shrinkBottomOffset,
                    0 // 預設值
                )
                indicatorId = typedArray.getResourceId(
                    R.styleable.CarouselScrollView_carousel_indicator,
                    -1
                )
            } finally {
                // 回收 typedArray，這一步非常重要，防止記憶體洩漏
                typedArray.recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // 在 XML 佈局解析完成後，將我們的內部容器添加到視圖中
        if (isEmpty()) {
            super.addView(container)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        // 首次獲取到寬度時，計算螢幕中心點
        if (width > 0 && screenCenter == 0) {
            screenCenter = width / 2
        }

        // 當子 View 數量變化時，更新計數
        if (totalItemCount != container.childCount) {
            totalItemCount = container.childCount
        }

        // 在佈局完成後設置初始的 Padding，以確保第一個項目能居中顯示
        post { setupInitialPadding() }
    }

    override fun computeScroll() {
        // 如果 scroller 正在計算滾動偏移，則更新滾動位置並繼續。
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            invalidate()
        } else {
            // 檢查是否有一個待處理的放大意圖
            if (pendingMagnify && !isPressed) {
                // 執行意圖
                magnifyCenterItem()
                // 意圖已完成，立即重置旗標
                pendingMagnify = false
            }
            // 處理用戶手勢的吸附邏輯（這部分可以保持不變或與 pendingMagnify 結合）
            else if (!isProgrammaticScroll && !isPressed && currentState == State.CAROUSEL) {
                val itemTotalWidth = itemWidth + itemSpacing
                if (itemTotalWidth <= 0) return

                val targetIndex = (scrollX.toFloat() / itemTotalWidth).roundToInt()
                val currentTargetX = targetIndex * itemTotalWidth

                if (scrollX != currentTargetX) {
                    // 如果位置有偏差，滾動到目標點，並期望後續放大
                    scrollToPage(targetIndex)
                } else {
                    // 如果已經剛好在目標點，但還沒有放大，則直接觸發放大。
                    // 這解決了「拖拽到完美位置後不放大」的 Bug。
                    magnifyCenterItem()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // 將所有觸控事件交給 GestureDetector 處理
        gestureDetector.onTouchEvent(ev)

        // 根據 MotionEvent 的 action 更新 isPressed 狀態
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> isPressed = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                // 在手指抬起時，手動呼叫 invalidate() 來喚醒 computeScroll，
                invalidate()
            }
        }

        // 始終返回 true，以確保手勢序列的完整性
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Check if the gesture is horizontal or vertical
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                gestureDetector.onTouchEvent(ev)
                // Always return false on DOWN to allow both parent and child to see subsequent events
                // until one of them claims it. However, for ScrollView, we usually want super to handle logic.
                // But we need to stop parent ViewPager from intercepting if it's a horizontal scroll.
                // Standard ScrollView logic in onInterceptTouchEvent handles this by checking direction.
                return super.onInterceptTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                // standard logic in super will handle nested scrolling
                return super.onInterceptTouchEvent(ev)
            }
            else -> return super.onInterceptTouchEvent(ev)
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        if (!scroller.isFinished) {
            // 停止正在進行的滾動
            scroller.abortAnimation()
        }

        pendingMagnify = false

        // 必須返回 true，後續的 onFling, onScroll 才會被觸發
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (abs(velocityX) < abs(velocityY) || currentState != State.CAROUSEL) {
            return false
        }

        // 根據滑動方向確定目標索引
        // 如果向左快速滑動 (velocityX > 0)，目標是右邊的頁面
        // 如果向右快速滑動 (velocityX < 0)，目標是左邊的頁面
        val targetIndex = if (velocityX < 0) {
            // 向右滑，目標 index 增加
            (currentIndex + 1)
        } else {
            // 向左滑，目標 index 減少
            (currentIndex - 1)
        }.coerceIn(0, totalItemCount - 1) // 確保 index 不越界


        // 直接呼叫 scrollToPage，讓它去處理滾動和後續的放大邏輯
        // 統一所有頁面切換的入口，無論是手勢還是程式碼觸發。
        if (targetIndex != currentIndex) {
            // 只有在目標頁面和當前頁面不同時才觸發滾動
            scrollToPage(targetIndex, withAnimation = true)
        } else {
            // 如果 Fling 的結果還是停留在當前頁（比如在列表邊緣），
            // 我們需要確保它能正確地吸附並放大。
            // 這裡可以依賴 onTouchEvent 中的 invalidate() 來觸發 computeScroll 的吸附邏輯。
            // 或者更保險地，自己觸發一次滾動到當前位置。
            scrollToPage(currentIndex, withAnimation = true)
        }

        // onFling 已經處理了手勢，返回 true
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (abs(distanceX) < abs(distanceY)) {
            return false
        }

        // 優化 Nested Scroll：只有當我們還能往該方向滾動時，才禁止父 View (如 ViewPager2) 攔截。
        // 如果已經滾動到邊緣且用戶試圖繼續往邊緣外滑動，允許父 View 攔截事件以切換頁面。
        val direction = if (distanceX > 0) 1 else -1
        if (canScrollHorizontally(direction)) {
            parent?.requestDisallowInterceptTouchEvent(true)
        }

        if (currentState == State.MAGNIFIED) {
            val isAtBoundary = (currentIndex == 0 || currentIndex == totalItemCount - 1)
            val isMovingInwardFromStart = (currentIndex == 0 && distanceX > 0)
            val isMovingInwardFromEnd = (currentIndex == totalItemCount - 1 && distanceX < 0)

            // 如果是從邊界向內滑動，或是非邊界的兩頁，則觸發縮小
            if (isMovingInwardFromStart || isMovingInwardFromEnd || !isAtBoundary) {
                shrinkAllItems()
                // 返回 true，讓後續的 onScroll 事件能在 CAROUSEL 狀態下處理滾動
                return true
            }
        }

        if (currentState != State.CAROUSEL) {
            return false
        }

        val itemTotalWidth = itemWidth + itemSpacing
        if (itemTotalWidth <= 0) {
            // 如果尺寸信息還未就緒，不執行任何操作
            return false
        }

        // 計算當前中心點左右兩邊的滾動邊界
        // 左邊界：不能超過前一個 item 的中心點
        // 右邊界：不能超過後一個 item 的中心點
        val leftPageIndex = (currentIndex - 1).coerceAtLeast(0)
        val rightPageIndex = (currentIndex + 1).coerceAtMost(totalItemCount - 1)
        val minScrollX = leftPageIndex * itemTotalWidth
        val maxScrollX = rightPageIndex * itemTotalWidth

        // 預計算本次滾動後，scrollX 將要到達的位置
        val nextScrollX = scrollX + distanceX.toInt()

        // 根據預計算的位置，決定實際應該滾動的距離
        val actualDistance =
            if (nextScrollX < minScrollX) {
                // 如果目標位置超出了左邊界，則實際滾動距離就是「到左邊界的距離」
                minScrollX - scrollX
            } else if (nextScrollX > maxScrollX) {
                // 如果目標位置超出了右邊界，則實際滾動距離就是「到右邊界的距離」
                maxScrollX - scrollX
            } else {
                // 如果在邊界內，就正常滾動
                distanceX.toInt()
            }

        // 執行計算好的、安全的滾動
        if (actualDistance != 0) {
            scrollBy(actualDistance, 0)
        }

        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (indicatorId != -1 && parent is ViewGroup) {
            try {
                indicatorView = (parent as ViewGroup).findViewById(indicatorId)
            } catch (e: ClassCastException) {
                Log.e("CarouselScrollView", "The view referenced by carousel_indicator is not a CarouselIndicator.${e.message}")
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 取消所有運行中的動畫，防止 memory leak
        cancelAllAnimations()
        // 移除所有待處理的消息和回調，防止內存洩漏
        handler.removeCallbacksAndMessages(null)
        // 清除監聽器引用，防止 memory leak
        onPageChangeListener = null
        onStateChangeListener = null
        // 清除 indicator 引用
        indicatorView = null
    }

    /**
     * 取消所有運行中的動畫，防止 memory leak
     */
    private fun cancelAllAnimations() {
        // 取消所有 ValueAnimator
        activeAnimators.forEach { animator ->
            if (animator.isRunning) {
                animator.cancel()
            }
        }
        activeAnimators.clear()

        // 停止 scroller
        if (!scroller.isFinished) {
            scroller.abortAnimation()
        }
    }

    /**
     * 清理所有視圖和資源，防止 memory leak
     */
    private fun cleanupAllViews() {
        // 先取消所有動畫
        cancelAllAnimations()

        // 移除所有子視圖
        container.removeAllViews()

        // 重置狀態
        currentIndex = 0
        totalItemCount = 0
        currentState = State.CAROUSEL
    }

    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onLongPress(e: MotionEvent) {}

    private fun populateFromAdapter() {
        val localAdapter = this.adapter ?: return
        if (localAdapter.getItemCount() == 0) return

        for (i in 0 until localAdapter.getItemCount()) {
            // 創建 ViewHolder
            val holder = localAdapter.onCreateViewHolder(container, 0) // viewType 暫時為 0

            // 獲取 itemView 並設置佈局參數
            val itemView = holder.itemView.apply {
                clipToOutline =itemCornerRadius > 0
                outlineProvider = RoundedOutlineProvider(itemCornerRadius)
            }
            val desiredParams = LinearLayout.LayoutParams(itemWidth, itemHeight).apply {
                marginEnd = itemSpacing
            }

            // 綁定數據
            @Suppress("UNCHECKED_CAST")
            (localAdapter as CarouselAdapter<CarouselViewHolder>).onBindViewHolder(holder, i)

            // 將 itemView 添加到容器
            container.addView(itemView, desiredParams)
        }

        // 更新總數
        totalItemCount = container.childCount
        // 重新計算 padding 和佈局
        requestLayout()
    }

    private fun resetAndPopulate() {
        // 先取消所有運行中的動畫，防止 memory leak
        cancelAllAnimations()

        // 移除所有舊的視圖
        container.removeAllViews()

        // 重置內部狀態
        currentIndex = 0
        totalItemCount = 0
        currentState = State.CAROUSEL
        scrollTo(0, 0)

        // 從 adapter 重新填充
        populateFromAdapter()
    }

    /**
     * 計算並設置左右的 Padding，使得任何一個項目都可以滾動到視圖的正中心。
     */
    private fun setupInitialPadding() {
        if (itemWidth == 0 || screenCenter == 0 || container.isEmpty()) return
        val padding = screenCenter - (itemWidth / 2)
        if (paddingLeft != padding) {
            setPadding(padding, 0, padding, 0)
        }
    }

    /**
     * 滾動到指定頁面的核心方法。
     * 這是所有滾動和頁面切換邏輯的入口點。
     *
     * @param pageIndex     要前往的index
     * @param withAnimation 是否使用平滑滾動動畫。
     */
    private fun scrollToPage(pageIndex: Int, withAnimation: Boolean = true) {
        if (totalItemCount == 0 || itemWidth == 0) return

        pendingMagnify = false

        // 如果當前是放大狀態，則必須先執行縮小。
        // 使用回調機制確保縮小動畫結束後，才執行後續的滾動邏輯。
        if (currentState == State.MAGNIFIED) {
            shrinkAllItems {
                // 在縮小完成後，重新呼叫自己以執行滾動。
                scrollToPage(pageIndex, withAnimation)
            }
            return
        }

        val newIndex = pageIndex.coerceIn(0, totalItemCount - 1)
        val itemTotalWidth = itemWidth + itemSpacing
        val targetX = newIndex * itemTotalWidth

        // 如果目標索引發生變化，更新內部狀態並通知監聽器。
        if (currentIndex != newIndex) {
            currentIndex = newIndex
            indicatorView?.setCurrentPage(currentIndex)
            onPageChangeListener?.invoke(currentIndex)
        }

        if (withAnimation) {
            val dx = targetX - scrollX
            if (dx != 0) {
                // 使用 OverScroller 啟動平滑滾動。
                // 滾動結束後，由 `computeScroll` 負責觸發吸附和放大。
                pendingMagnify = true
                scroller.startScroll(scrollX, 0, dx, 0, 400)
                invalidate()
            } else {
                // 如果已經在目標位置，直接放大，沒有待處理任務
                pendingMagnify = false
                magnifyCenterItem()
            }
        } else {
            // 無動畫，直接滾動並放大，沒有待處理任務
            pendingMagnify = false
            scrollTo(targetX, 0)
            magnifyCenterItem(duration = 0)
        }
    }

    /**
     * 將當前中心的項目放大。
     * @param duration 動畫時長。為 0 表示立即完成。
     */
    private fun magnifyCenterItem(duration: Long = 200) {
        if (width == 0 || (currentState == State.MAGNIFIED && duration > 0)) return

        currentState = State.MAGNIFIED
        val source = if (isProgrammaticScroll) TriggerSource.PROGRAMMATIC else TriggerSource.USER_ACTION
        onStateChangeListener?.invoke(currentState, source)

        animateVerticalAlignment(duration)

        val animationEndCallback = {
            // 只有當這是一個由程式碼觸發的動畫序列時，我們才需要重置旗標
            if (isProgrammaticScroll) {
                isProgrammaticScroll = false
            }
        }

        val widthIncrease = width - itemWidth
        if (widthIncrease > 0) {
            val startScrollX = scrollX
            // 最終要滾動到的目標位置（向右滾動增加量的一半）
            val endScrollX = scrollX + (widthIncrease / 2)

            ValueAnimator.ofInt(startScrollX, endScrollX).apply {
                this.duration = duration
                addUpdateListener { scrollTo(it.animatedValue as Int, 0) }
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        activeAnimators.remove(this@apply)
                        animationEndCallback.invoke()
                    }
                    override fun onAnimationCancel(animation: android.animation.Animator) {
                        activeAnimators.remove(this@apply)
                    }
                })
                activeAnimators.add(this)
                start()
            }
        } else {
            // 如果沒有滾動動畫，也要確保回調能被執行
            animationEndCallback.invoke()
        }

        // 遍歷所有子View，放大當前項目。
        // animateSize 現在只負責尺寸和圓角的變化。
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (i == currentIndex) {
                // 通知新的中心項目
                (child as? CarouselItem)?.onCentered()
                animateSize(child, width, height, duration)
            }
        }
    }

    /**
     * 將所有已放大的項目縮小到標準尺寸。
     * @param duration 動畫時長。
     * @param onEnd 所有縮小動畫都完成後執行的回調。
     */
    private fun shrinkAllItems(duration: Long = 200, onEnd: (() -> Unit)? = null) {
        if (currentState == State.CAROUSEL) {
            onEnd?.invoke()
            return
        }

        // 在縮小之前，通知當前被放大的項目它將要失去焦點
        if (currentIndex in 0 until container.childCount) {
            val currentCenterView = container.getChildAt(currentIndex)
            (currentCenterView as? CarouselItem)?.onLostFocus()
        }

        currentState = State.CAROUSEL
        val source = if (isProgrammaticScroll) TriggerSource.PROGRAMMATIC else TriggerSource.USER_ACTION
        onStateChangeListener?.invoke(currentState, source)

        animateVerticalAlignment(duration)

        if (container.isEmpty()) {
            onEnd?.invoke()
            return
        }

        // 1. 先找出所有需要執行動畫的任務 (優化：只處理真正需要縮小的項目)
        val viewsToAnimate = mutableListOf<View>()
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            // 修改判斷邏輯：如果當前尺寸不等於目標尺寸(itemWidth, shrinkHeight)，才需要動畫
            if (child.width != itemWidth || child.height != itemHeight) {
                viewsToAnimate.add(child)
            }
        }

        val widthDecrease = width - itemWidth
        val animateScroll = widthDecrease > 0

        // 2. 計算總任務數
        // 注意：即使 duration = 0，animateSize 也會調用 callback，所以如果是透過 animateSize 處理的都要算
        var pendingTasks = viewsToAnimate.size
        // 只有當 duration > 0 時，滾動動畫才需要作為一個異步任務等待
        if (animateScroll && duration > 0) {
            pendingTasks++
        }

        // 3. 如果沒有異步任務，處理同步滾動後直接回調
        if (pendingTasks == 0) {
            // 處理滾動位置補償 (同步)
            if (widthDecrease > 0) scrollBy(-(widthDecrease / 2), 0)
            else if (widthDecrease < 0) scrollBy(-(widthDecrease / 2), 0)

            handler.post { onEnd?.invoke() }
            return
        }

        // 4. 使用原子計數器跟蹤任務
        val animationCounter = java.util.concurrent.atomic.AtomicInteger(pendingTasks)
        val taskEndCallback = {
            if (animationCounter.decrementAndGet() == 0) {
                handler.post { onEnd?.invoke() }
            }
        }

        // 5. 執行 View 尺寸動畫
        for (child in viewsToAnimate) {
            animateSize(child, itemWidth, itemHeight, duration, taskEndCallback)
        }

        // 6. 執行滾動動畫
        if (animateScroll) {
            if (duration > 0) {
                val startScrollX = scrollX
                val endScrollX = scrollX - (widthDecrease / 2)

                ValueAnimator.ofInt(startScrollX, endScrollX).apply {
                    this.duration = duration
                    addUpdateListener { scrollTo(it.animatedValue as Int, 0) }
                    addListener(object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: android.animation.Animator) {
                            activeAnimators.remove(this@apply)
                            taskEndCallback.invoke()
                        }
                        override fun onAnimationCancel(animation: android.animation.Animator) {
                            activeAnimators.remove(this@apply)
                        }
                    })
                    activeAnimators.add(this)
                    start()
                }
            } else {
                // 如果不需要動畫但需要位移，直接執行 (理論上應在 pendingTasks=0 時處理，但這裡作為保險)
                scrollBy(-(widthDecrease / 2), 0)
            }
        } else {
            // 保持原邏輯：如果 widthDecrease <= 0 (雖然少見)，執行位移
            if (widthDecrease < 0) scrollBy(-(widthDecrease / 2), 0)
        }
    }

    /**
     * 執行從當前尺寸到目標尺寸的平滑動畫，並同步處理圓角。
     *
     * @param view 要執行動畫的視圖。
     * @param targetWidth 目標寬度。
     * @param targetHeight 目標高度。
     * @param duration 動畫時長。
     * @param onEnd 動畫結束後的回調。
     */
    private fun animateSize(
        view: View,
        targetWidth: Int,
        targetHeight: Int,
        duration: Long = 200,
        onEnd: (() -> Unit)? = null
    ) {
        val startWidth = view.width
        val startHeight = view.height

        // 判斷目標圓角：如果目標寬度是 itemWidth (即縮小)，則使用 itemCornerRadius，否則為 0 (放大變直角)
        val targetRadius = if (targetWidth == itemWidth) itemCornerRadius else 0f
        // 獲取當前圓角
        val startRadius = (view.outlineProvider as? RoundedOutlineProvider)?.radius ?: 0f

        // 如果 duration 為 0，直接設定尺寸和圓角，跳過動畫
        if (duration == 0L) {
            if (startWidth != targetWidth || startHeight != targetHeight) {
                view.layoutParams.width = targetWidth
                view.layoutParams.height = targetHeight
                view.requestLayout()
            }

            // 立即設定圓角
            view.clipToOutline = targetRadius > 0
            view.outlineProvider = RoundedOutlineProvider(targetRadius)

            onEnd?.invoke()
            return
        }

        if (startWidth == targetWidth && startHeight == targetHeight && startRadius == targetRadius) {
            onEnd?.invoke()
            return
        }

        ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration

            addUpdateListener {
                val fraction = it.animatedFraction

                // 更新尺寸
                val newWidth = startWidth + ((targetWidth - startWidth) * fraction).toInt()
                val newHeight = startHeight + ((targetHeight - startHeight) * fraction).toInt()
                view.layoutParams.width = newWidth
                view.layoutParams.height = newHeight

                // 更新圓角
                val newRadius = startRadius + (targetRadius - startRadius) * fraction
                // 只有當圓角大於0時才需要裁剪
                view.clipToOutline = newRadius > 0
                view.outlineProvider = RoundedOutlineProvider(newRadius)
                view.requestLayout()
            }

            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    activeAnimators.remove(this@apply)
                    // 當動畫真正結束時，執行回調
                    onEnd?.invoke()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {
                    activeAnimators.remove(this@apply)
                }
            })
            activeAnimators.add(this)
            start()
        }
    }

    /**
     * 使用動畫平滑地調整垂直對齊。
     * @param duration 動畫時長。
     */
    private fun animateVerticalAlignment(duration: Long = 200) {
        // 計算在縮小狀態下，所有小圖應有的基礎 paddingTop/topMargin
        val anchorView = (parent as ViewGroup).findViewById<View>(verticalAlignToId)
        val anchorViewBottom = (parent as ViewGroup).findViewById<View>(verticalAlignToBottomId)
        val baseTopMargin = anchorView.top + shrinkTopOffset
        itemHeight = anchorViewBottom.top - anchorView.top - shrinkBottomOffset

        // 遍歷所有子項目，為每個項目獨立設置動畫
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val lp = child.layoutParams as LinearLayout.LayoutParams

            // 根據全局狀態和當前項目的索引，決定其【目標 topMargin】
            val targetTopMargin = when {
                // 全局是放大狀態，且當前是中心項目 -> 貼頂 (topMargin = 0)
                currentState == State.MAGNIFIED && i == currentIndex -> 0
                // 其他所有情況 (縮小狀態的所有項目，或放大狀態下的旁邊項目) -> 使用基礎 Margin
                else -> baseTopMargin
            }

            // 如果目標與當前值不同，則為這個子項目獨立啟動動畫
            val startTopMargin = lp.topMargin
            if (startTopMargin != targetTopMargin) {
                ValueAnimator.ofInt(startTopMargin, targetTopMargin).apply {
                    this.duration = duration
                    addUpdateListener { animator ->
                        lp.topMargin = animator.animatedValue as Int
                        child.layoutParams = lp // 應用新的佈局參數
                    }
                    addListener(object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: android.animation.Animator) {
                            activeAnimators.remove(this@apply)
                        }
                        override fun onAnimationCancel(animation: android.animation.Animator) {
                            activeAnimators.remove(this@apply)
                        }
                    })
                    activeAnimators.add(this)
                    start()
                }
            }
        }
    }

    /**
     * 輔助類，用於創建帶有可變圓角的 OutlineProvider
     */
    private class RoundedOutlineProvider(var radius: Float) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }

    /**
     * 設置頁面變更監聽器。
     * @param listener 當中心頁面索引改變時觸發的回調。
     */
    fun setOnPageChangeListener(listener: (Int) -> Unit) {
        this.onPageChangeListener = listener
    }

    /**
     * 設置狀態變更監聽器（輪播/放大）。
     * @param listener 當視圖狀態在 `CAROUSEL` 和 `MAGNIFIED` 之間切換時觸發的回調。
     */
    fun setOnStateChangeListener(listener: (newState: State, source: TriggerSource) -> Unit) {
        this.onStateChangeListener = listener
    }

    /**
     * 獲取當前居中的頁面索引。
     */
    fun getCurrentIndex(): Int {
        return currentIndex
    }

    /**
     * 從外部程式碼控制視圖滾動到指定頁面。
     *
     * @param pageIndex 要跳轉到的目標頁面索引。
     * @param magnifyImmediately 如果為 true，則無動畫跳轉並立即放大；
     *                           如果為 false，則執行平滑的 "縮小 -> 滾動 -> 放大" 動畫序列。
     */
    fun gotoPage(pageIndex: Int, magnifyImmediately: Boolean) {
        if (totalItemCount == 0 || itemWidth == 0) return

        // 標記動畫序列開始
        isProgrammaticScroll = true

        val newIndex = pageIndex.coerceIn(0, totalItemCount - 1)
        pendingMagnify = false
        scroller.abortAnimation()

        if (magnifyImmediately) {
            // 無動畫，立即跳轉並放大
            scrollToPage(newIndex, false)
        } else {
            // 執行完整的 "縮小 -> 滾動 -> 放大" 動畫序列
            scrollToPage(newIndex, true)
        }
    }
}
