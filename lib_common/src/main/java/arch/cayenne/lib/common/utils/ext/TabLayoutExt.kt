package arch.cayenne.lib.common.utils.ext

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * @date: 2025/5/30 11:55
 * @description: tabLayout扩展
 */
object TabLayoutExt {
    //设置tab之间的外边距
    fun TabLayout.reflexMargin(leftMargin: Int, rightMargin: Int, margin: Int) {
        val tabLayout = this
        val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until mTabStrip.childCount) {
            val tabView = mTabStrip.getChildAt(i)
            val params = tabView.layoutParams as LinearLayout.LayoutParams
            when (i) {
                0 -> {//第一个tab
                    params.leftMargin = leftMargin
                    params.rightMargin = margin
                }

                mTabStrip.childCount - 1 -> {//最后一个tab
                    params.leftMargin = margin
                    params.rightMargin = rightMargin
                }

                else -> {//中间tab
                    params.leftMargin = margin
                    params.rightMargin = margin
                }
            }
            tabView.layoutParams = params
        }
    }

    /**
     * 設定最後一個 tab 跟手動畫，ivMore/llMore 跟手縮放顯示
     * @param ivMore 一般更多按鈕
     * @param llMore 展開後的更多區塊
     */
    @SuppressLint("ClickableViewAccessibility")
    fun TabLayout.setupEndTabMoreAnimation(
        ivMore: View,
        llMore: View,
        onStateChanged: ((isLlMoreVisible: Boolean) -> Unit)? = null
    ) {
        var isExpanded = false
        var isAnimating = false
        var lastTabCount = 0
        var isInTransition = false
        var lockState = 0L
        var isInitialized = false
        var startupLock = 0L
        var userDragActiveUntil = 0L

        fun isUserDragging(): Boolean = SystemClock.uptimeMillis() < userDragActiveUntil

        // 設置初始狀態
        fun setInitialState() {
            ivMore.visibility = View.GONE
            llMore.visibility = View.GONE
            llMore.alpha = 0f
            llMore.scaleX = 1f
            llMore.scaleY = 1f
        }

        setInitialState()

        // 檢查最後一個 tab 是否完全可見
        fun isLastTabFullyVisible(): Boolean {
            val tabStrip = getChildAt(0) as? LinearLayout ?: return false
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1) ?: return false
            if (lastTab.width == 0) return false

            val lastTabWidth = lastTab.width
            val tabLayoutRect = IntArray(2)
            getLocationOnScreen(tabLayoutRect)
            val lastTabRect = IntArray(2)
            lastTab.getLocationOnScreen(lastTabRect)

            val tabLayoutRightEdge = tabLayoutRect[0] + width
            val lastTabLeftEdge = lastTabRect[0]
            val lastTabRightEdge = lastTabRect[0] + lastTabWidth

            return lastTabLeftEdge >= tabLayoutRect[0] &&
                    lastTabRightEdge <= tabLayoutRightEdge &&
                    lastTabWidth > 0
        }

        // 展開動畫：ivMore -> llMore
        fun animateToLlMore() {
            isExpanded = true
            isInTransition = true
            isAnimating = true

            ivMore.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(android.view.animation.AnticipateInterpolator(1.0f))
                .withEndAction {
                    ivMore.visibility = View.GONE
                }
                .start()

            llMore.alpha = 0f
            llMore.scaleX = 0.8f
            llMore.scaleY = 0.8f
            llMore.visibility = View.VISIBLE
            llMore.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(android.view.animation.OvershootInterpolator(1.0f))
                .withEndAction {
                    isInTransition = false
                    isAnimating = false
                    onStateChanged?.invoke(true)
                }
                .start()
        }

        // 收起動畫：llMore -> ivMore
        fun animateToIvMore() {
            isExpanded = false
            isInTransition = true
            isAnimating = true

            llMore.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(android.view.animation.AnticipateInterpolator(1.0f))
                .withEndAction {
                    llMore.visibility = View.GONE
                }
                .start()

            ivMore.visibility = View.VISIBLE
            ivMore.alpha = 0f
            ivMore.scaleX = 0.8f
            ivMore.scaleY = 0.8f
            ivMore.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(android.view.animation.OvershootInterpolator(1.0f))
                .withEndAction {
                    isInTransition = false
                    isAnimating = false
                    onStateChanged?.invoke(false)
                }
                .start()
        }

        val updateUI = fun() {
            // 動畫進行中，跳過更新
            if (isAnimating) return

            // 未初始化完成，保持初始狀態
            if (!isInitialized) {
                setInitialState()
                return
            }

            // 初始鎖定，確保動畫不自動展開
            val currentTime = SystemClock.uptimeMillis()
            if (currentTime < startupLock) {
                setInitialState()
                return
            }

            // Tab 數量少，保持 ivMore 顯示
            if (this.tabCount <= 1) {
                setInitialState()
                return
            }

            // 檢查 TabLayout 是否已經完全佈局完成
            if (width <= 0 || height <= 0) {
                return
            }

            val tabStrip = getChildAt(0) as? LinearLayout ?: return
            val contentWidth = tabStrip.width
            val visibleWidth = width

            // 檢查佈局是否完全準備好
            if (contentWidth <= 0 || visibleWidth <= 0) {
                return
            }

            // 如果內容寬度小於可見寬度，表示所有 tab 都能顯示，不需要更多按鈕
            if (contentWidth <= visibleWidth) {
                setInitialState()
                return
            }

            // 檢查最後一個 tab 是否完全可見
            val isLastTabFullyVisible = isLastTabFullyVisible()

            // 展開：最後一個 tab 完全可見且為使用者拖動時觸發
            val shouldExpand =
                isLastTabFullyVisible && !isExpanded && !isInTransition && isUserDragging()

            // 收起：最後一個 tab 不再完全可見時觸發
            val shouldCollapse = !isLastTabFullyVisible && isExpanded && !isInTransition

            val hasNewTabAdded = tabCount > lastTabCount

            // 更新追蹤的 tab 數量
            lastTabCount = tabCount

            // 檢測是否為超寬 tab
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1)
            val lastTabWidth = lastTab?.width ?: 0
            val averageTabWidth = tabStrip.width / tabCount.toFloat()
            val isExtraWideTab = lastTabWidth > averageTabWidth * 1.5f

            // 檢查是否在鎖定期間
            val now = SystemClock.uptimeMillis()
            val isLocked = now < lockState

            // 新增 tab 時啟動鎖定，避免閃爍
            if (hasNewTabAdded) {
                val lockDuration = if (isExtraWideTab) 800L else 500L
                lockState = now + lockDuration

                // 新增 tab 後延遲檢查是否需要展開
                if (isInitialized) {
                    post {
                        val tabStrip = getChildAt(0) as? LinearLayout ?: return@post
                        val contentWidth = tabStrip.width
                        val visibleWidth = width
                        val scrolledX = scrollX
                        val endEpsilon = 4
                        val isAtTrueEndAfterLayout =
                            scrolledX + visibleWidth >= contentWidth - endEpsilon
                        val isLastTabFullyVisibleAfterLayout = isLastTabFullyVisible()

                        // 如果新增 tab 後確實會讓列表滾動到末端且最後一個 tab 完全可見，則強制顯示 llMore
                        if ((isAtTrueEndAfterLayout || isLastTabFullyVisibleAfterLayout) && !isExpanded) {
                            animateToLlMore()
                        }
                    }
                }
                return
            }

            // 鎖定期間，不改變 UI 狀態
            if (isLocked) {
                return
            }

            // 展開邏輯：滑動到末端時顯示 llMore
            if (shouldExpand) {
                animateToLlMore()
            } else if (shouldCollapse) {
                // 收起邏輯：離開末端時顯示 ivMore
                animateToIvMore()
            }
        }

        // 註冊滾動監聽，添加保護機制避免分頁切換時異常觸發
        viewTreeObserver.addOnScrollChangedListener {
            // 檢查是否在鎖定期間
            val currentTime = SystemClock.uptimeMillis()
            if (currentTime < startupLock) {
                return@addOnScrollChangedListener
            }
            // 立即檢測，確保跟手
            updateUI()
        }

        // 註冊觸控監聽，確保拖動時能及時檢測
        setOnTouchListener { _, event ->
            val currentTime = SystemClock.uptimeMillis()
            if (currentTime < startupLock) return@setOnTouchListener false

            when (event.action) {
                android.view.MotionEvent.ACTION_MOVE -> {
                    // 標記使用者拖動，給 300ms 寬限以覆蓋佈局延遲
                    userDragActiveUntil = SystemClock.uptimeMillis() + 300L
                    updateUI()
                }

                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    // 放手後仍保留 150ms 寬限
                    userDragActiveUntil = SystemClock.uptimeMillis() + 150L
                    post { updateUI() }
                }
            }
            false
        }

        // 延遲初始化，避免在佈局過程中觸發
        post {
            // 初始化完成標記
            lastTabCount = tabCount
            isInitialized = true
            // 初始鎖定 800ms，避免進頁會觸發展開
            startupLock = SystemClock.uptimeMillis() + 800L
            // 初始不視為使用者拖動
            userDragActiveUntil = 0L
        }
    }

    /**
     * OnTabSelectedListener增加isTabClick属性
     */
    interface OnTabSelectedListener2 {
        fun onTabSelected(tab: Tab, isTabClick: Boolean)
        fun onTabUnselected(tab: Tab, isTabClick: Boolean)
        fun onTabReselected(tab: Tab, isTabClick: Boolean)
    }

    fun TabLayout.addOnTabSelectedListener2(lis: OnTabSelectedListener2) {
        clearOnTabSelectedListener()
        var isTabClick = false
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                lis.onTabSelected(tab, isTabClick)
            }

            override fun onTabUnselected(tab: Tab) {
                lis.onTabUnselected(tab, isTabClick)
            }

            override fun onTabReselected(tab: Tab) {
                lis.onTabReselected(tab, isTabClick)
            }
        }.apply { setTag(88888888, this) })

        //setOnClickListener
        findViewTreeLifecycleOwner()!!.lifecycleScope.launch {
            withTimeout(200) { while (tabCount == 0) delay(1) }
            for (i in 0 until tabCount) {
                val tabView = (getChildAt(0) as ViewGroup).getChildAt(i)
                tabView.setOnClickListener {
                    isTabClick = true
                    post { isTabClick = false }
                }
            }
        }
    }

    fun TabLayout.clearOnTabSelectedListener() {
        removeOnTabSelectedListener(getTag(88888888) as OnTabSelectedListener?)
    }
}

