package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class LiveScrollDrawerLayout : DrawerLayout {
    private var drawerListener: DrawerListener? = null
    var mScrollDrawerEvents: ScrollDrawerEvents? = null
    private var initialX: Float = 0f // 记录触摸起始 X 坐标
    private var initialY: Float = 0f // 记录触摸起始 Y 坐标
    private val touchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop // 触摸灵敏度阈值

    constructor(context: Context) : this(context, null)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        setDrawerView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun setDrawerView() {
        if (drawerListener != null) {
            removeDrawerListener(drawerListener!!)
        }
        drawerListener = object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 禁用内容视图的平移效果，确保内容不随侧边栏滑动
                val contentView = getChildAt(0)
                contentView.translationX = 0f // 固定内容位置
            }

            override fun onDrawerOpened(drawerView: View) {
                mScrollDrawerEvents?.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                mScrollDrawerEvents?.onDrawerClosed(drawerView)
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 可根据需要处理状态变化
            }
        }
        addDrawerListener(drawerListener!!)

        // 设置抽屉锁定模式，允许通过代码控制抽屉
        setDrawerLockMode(LOCK_MODE_UNLOCKED, GravityCompat.END)
    }

    /**
     * 拦截触摸事件，禁止左滑打开抽屉
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = ev.x - initialX
                val deltaY = ev.y - initialY
                // 确保是水平滑动（避免误判垂直滑动）
                if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > touchSlop) {
                    // 检测左滑（从屏幕左侧向右滑动，deltaX > 0）且抽屉未打开
                    if (deltaX < 0 && !isDrawerOpen(GravityCompat.END)) {
                        // 禁止左滑打开抽屉
                        return false
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }


    /**
     * 设置抽屉事件监听器
     */
    fun setScrollDrawerListener(mDrawerEvents: ScrollDrawerEvents) {
        this.mScrollDrawerEvents = mDrawerEvents
    }

    /**
     * 抽屉事件接口
     */
    interface ScrollDrawerEvents {
        fun onDrawerOpened(drawerView: View) {}
        fun onDrawerClosed(drawerView: View) {}
    }

    /**
     * 打开右侧抽屉
     */
    fun openRightDrawer() {
        openDrawer(GravityCompat.END)
    }

    /**
     * 关闭右侧抽屉
     */
    fun closeRightDrawer() {
        closeDrawer(GravityCompat.END)
    }
}