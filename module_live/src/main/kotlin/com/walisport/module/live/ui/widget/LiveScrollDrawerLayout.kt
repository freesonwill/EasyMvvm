package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

class LiveScrollDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DrawerLayout(context, attrs, defStyleAttr) {

    private var isAnimationRunning = true
    private var drawerListener: DrawerListener? = null
    var mScrollDrawerEvents: ScrollDrawerEvents? = null
    private var initialX: Float = 0f // 记录触摸起始 X 坐标
    private var initialY: Float = 0f // 记录触摸起始 Y 坐标
    private val touchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop // 触摸灵敏度阈值
    private  var gestureDetector: GestureDetectorCompat
    private var parentFragment: Fragment? = null // 关联的 Fragment

    init {
        setDrawerView()
        // 初始化手势检测器
        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null || e2 == null) return false

                // 检测向右滑动到屏幕最左侧
                val deltaX = e2.x - e1.x
                val isAtLeftEdge = e1.x < 100 // 屏幕左侧 100px 范围内
                if (deltaX > 200 && velocityX > 200 && isAtLeftEdge) {
                    // 触发返回上一级页面
                    parentFragment?.requireActivity()?.onBackPressedDispatcher?.onBackPressed()
                    return true
                }
                return false
            }
        })
    }

    // 设置关联的 Fragment
    fun setParentFragment(fragment: Fragment) {
        this.parentFragment = fragment
    }

    fun setIsAnimationRunning(isAnimationRunning: Boolean) {
        this.isAnimationRunning = isAnimationRunning
    }

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
                setDrawerLockMode(LOCK_MODE_UNLOCKED, GravityCompat.END)
            }

            override fun onDrawerClosed(drawerView: View) {
                mScrollDrawerEvents?.onDrawerClosed(drawerView)
                setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 可根据需要处理状态变化
                if (newState == STATE_IDLE) {
                    isAnimationRunning = true
                } else {
                    isAnimationRunning = false
                }
            }
        }
        addDrawerListener(drawerListener!!)

        // 设置抽屉锁定模式，允许通过代码控制抽屉
        setDrawerLockMode(LOCK_MODE_UNLOCKED, GravityCompat.END)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isAnimationRunning) return false

        // 将触摸事件传递给 GestureDetector
        gestureDetector.onTouchEvent(ev)

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
                    // 检测左滑（deltaX < 0）且抽屉未打开，禁止左滑打开抽屉
                    if (deltaX < 0 && !isDrawerOpen(GravityCompat.END)) {
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