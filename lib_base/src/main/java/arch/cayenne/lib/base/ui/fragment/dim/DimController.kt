package arch.cayenne.lib.base.ui.fragment.dim

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge

class DimController private constructor() {

    companion object {
        private const val TAG = "DimController"
        const val TARGET_DIM = 0.75f
        private val instance: DimController by lazy {
            DimController()
        }
        fun getInstance(host: DimInterface): DimController {
            instance.init(host)
            return instance
        }
    }

    private var dimView: View? = null
    private var hostMap = mutableMapOf<Int, DimInterface>()
    private var canChangeDim = true

    fun findAnyShowing(host: DimInterface): Boolean {
        return if (hostMap.isEmpty()) {
            false
        } else {
            hostMap.any {
                !it.value.getIsDismissing() && it.value != host
            }
        }
    }

    private fun init(host: DimInterface) {
        register(host)
        if (dimView != null) {
            return
        }
        val context = host.getHostFragment().requireActivity()
        val v = DimView(context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
            z = 100f
            translationY = -(getNavigationBarHeight(context) + 1).toFloat()
            isVisible = false
        }.apply {
            setOnClickListener {
                "Dim View Clicked,alpha:${it.alpha}, translationX:${it.translationX},translationY:${it.translationY}".loge(TAG)
            }
            setOnLongClickListener {
                "Dim View removed".loge(TAG)
                hideDim()
                true
            }
        }

        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        context.addContentView(v, params)
        dimView = v
        v.doOnDetach {
            "Dim View Detached".logd(TAG)
            dimView = null // 置空避免内存泄漏
        }
    }

    private fun getNavigationBarHeight(context: Context): Int {
        val key = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            "navigation_bar_height"
        } else {
            "navigation_bar_height_landscape"
        }
        return getInternalDimensionSize(context, key)
    }

    private fun getInternalDimensionSize(context: Context, key: String): Int {
        val result = 0
        try {
            val resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android")
            if (resourceId > 0) {
                val sizeOne = context.resources.getDimensionPixelSize(resourceId)
                val sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId)

                if (sizeTwo >= sizeOne && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                            key != "status_bar_height")
                ) {
                    return sizeTwo
                } else {
                    val densityOne = context.resources.displayMetrics.density
                    val densityTwo = Resources.getSystem().displayMetrics.density
                    val f = sizeOne * densityTwo / densityOne
                    return (if ((f >= 0)) (f + 0.5f) else (f - 0.5f)).toInt()
                }
            }
        } catch (ignored: Resources.NotFoundException) {
            return 0
        }
        return result
    }

    private fun register(host: DimInterface) {
        val code = host.hashCode()
        if (hostMap.containsKey(code)) return
        val f = host.getHostFragment()
        if(f.view == null) {
            "Can't access the Fragment View's LifecycleOwner for $f when getView() is null i.e., before onCreateView() or after onDestroyView()".loge(TAG)
            return
        }
        val lifecycleOwner = f.viewLifecycleOwner
        hostMap[code] = host
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                hostMap.remove(code)
                owner.lifecycle.removeObserver(this) // 移除 observer，避免多餘引用
            }
        })
    }

    private fun getBasicLayoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        return params
    }

    fun stopChangeDim() {
        canChangeDim = false
    }

    fun allowChangeDim() {
        canChangeDim = true
    }

    fun prepareShowDim() {
        val v = dimView ?: return
        v.isVisible = true
        v.bringToFront()
    }

    fun showDim() {
        val v = dimView ?: return
        if (v.alpha == TARGET_DIM || !canChangeDim) return
        v.alpha = TARGET_DIM
    }

    fun hideDim() {
        val v = dimView ?: return
        if (v.alpha == 0f || !canChangeDim) return
        v.alpha = 0f
        v.post {
            v.isVisible = false
        }
    }

    fun setDimAlpha(alpha: Float) {
        if (!canChangeDim) return
        dimView?.alpha = alpha.coerceIn(0f, 1f)
    }

    fun getHideAnimator(): ObjectAnimator? {
        val v = dimView ?: return null
        return ObjectAnimator.ofFloat(v, "alpha", v.alpha, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                if (!canChangeDim) {
                    v.alpha = value
                }
            }
            doOnEnd {
                v.post {
                    v.isVisible = false
                }
            }
        }
    }

    fun setRect(rect: Rect, radius: Float) {
        val centerX = rect.centerX()
        val centerY = rect.centerY()
        val width = rect.width()
        val height = rect.height()
        (dimView as? DimView)?.setRect(centerX, centerY, width, height, radius)
    }

    fun clearRect() {
        (dimView as? DimView)?.clearRect()
    }
}