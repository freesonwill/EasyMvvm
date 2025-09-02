package arch.cayenne.lib.base.ui.fragment.dim

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class DimController private constructor() {

    companion object {
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
            checkLayoutParams()
            return
        }
        val context = host.getHostFragment().requireContext()
        val v = View(context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
        }
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = getBasicLayoutParams()
        windowManager.addView(v, params)
        dimView = v
    }

    private fun register(host: DimInterface) {
        val code = host.hashCode()
        if (hostMap.containsKey(code)) return
        val f = host.getHostFragment()
        val lifecycleOwner = f.viewLifecycleOwner
        hostMap[code] = host
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                hostMap.remove(code)
                owner.lifecycle.removeObserver(this) // 移除 observer，避免多餘引用
            }
        })
    }

    fun checkLayoutParams() {
        val v = dimView ?: return
        val params = v.layoutParams as WindowManager.LayoutParams
        val basicParams = getBasicLayoutParams()
        if (params.width != basicParams.width || params.height != basicParams.height) {
            updateLayoutParams(basicParams)
            v.translationY = 0f
        }
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

    fun updateLayoutParams(params: WindowManager.LayoutParams) {
        val v = dimView ?: return
        val currentParams = v.layoutParams as WindowManager.LayoutParams
        currentParams.width = params.width
        currentParams.height = params.height
        currentParams.x = params.x
        currentParams.y = params.y
        currentParams.flags = params.flags
        currentParams.format = params.format
        currentParams.gravity = params.gravity

        val windowManager = v.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.updateViewLayout(v, currentParams)
    }

    fun showDim() {
        if (dimView?.alpha == TARGET_DIM) return
        dimView?.alpha = TARGET_DIM
    }

    fun hideDim() {
        if (dimView?.alpha == 0f) return
        dimView?.alpha = 0f
    }

    fun setDimAlpha(alpha: Float) {
        dimView?.alpha = alpha.coerceIn(0f, 1f)
    }

    fun setTranslationY(y: Float) {
        dimView?.translationY = y
    }

    fun reset() {
        val v = dimView ?: return
        val params = getBasicLayoutParams()
        updateLayoutParams(params)
        v.translationY = 0f
    }

    fun getHideAnimator(): ObjectAnimator? {
        val v = dimView ?: return null
        return ObjectAnimator.ofFloat(v, "alpha", v.alpha, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                v.alpha = value
            }
            doOnEnd {
                hideDim()
            }
        }
    }
}