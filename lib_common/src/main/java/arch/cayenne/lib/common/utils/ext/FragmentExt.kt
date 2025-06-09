package arch.cayenne.lib.common.utils.ext

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @author: zhangsan
 * @date: 2025/5/28 10:13
 * @description:
 */
object FragmentExt {
    private val TAG = "FragmentExt"

    /**
     * Fragment是否和Activity一样大
     *
     * @return
     */
    suspend fun Fragment.isFullWithActivity(): Boolean {
        val activityRoot = requireActivity().window.decorView
        val activityRoot2 = requireActivity().findViewById<View>(android.R.id.content)
        val fragmentRoot = view ?: return false
        val isMeasured = (fragmentRoot.width != 0 || fragmentRoot.height != 0) &&
                (activityRoot.width != 0 || activityRoot.height != 0)
        //没有测量结束，等待
        if (!isMeasured) {
            suspendCancellableCoroutine { continuation ->
                fragmentRoot.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        fragmentRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        continuation.resume(Unit)
                    }
                })
            }
        }
        // 获取系统 Insets（状态栏 + 导航栏高度）
        val rootInsets = ViewCompat.getRootWindowInsets(fragmentRoot)
        val insets = rootInsets?.getInsets(WindowInsetsCompat.Type.systemBars())

        val systemBarsHeight = (insets?.top ?: 0) + (insets?.bottom ?: 0)
        val systemBarsWidth = (insets?.left ?: 0) + (insets?.right ?: 0)

        val visibleActivityWidth = activityRoot.width - systemBarsWidth
        val visibleActivityHeight = activityRoot.height - systemBarsHeight

        """
            fragmentRoot.width:${fragmentRoot.width},fragmentRoot.height:${fragmentRoot.height}
            activityRoot.width:${activityRoot.width},activityRoot.height:${activityRoot.height}
            activityRoot2.width:${activityRoot2.width},activityRoot2.height:${activityRoot2.height}
            visibleActivityWidth:${visibleActivityWidth},visibleActivityHeight:${visibleActivityHeight},top:${insets?.top},bottom:${insets?.bottom}
        """.logd(TAG)
        val sameWidth = fragmentRoot.width == visibleActivityWidth
        val sameHeight = fragmentRoot.height == visibleActivityHeight
        return sameWidth && sameHeight
    }



    @JvmSynthetic
    internal operator fun Fragment.plusAssign(newBundle: Bundle?) {
        if (newBundle == null) {
            return
        }

        val oldArgs: Bundle? = this.arguments
        if (oldArgs == null) {
            this.arguments = newBundle
            return
        }

        oldArgs.putAll(newBundle)
    }

    @JvmSynthetic
    internal fun ArrayDeque<Int>.replaceAll(array: IntArray?) {
        if (array == null) return
        clear()
        for (value in array) {
            add(value)
        }
    }
}