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
import androidx.core.view.isVisible
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
}