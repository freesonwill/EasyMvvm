package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener

open class ToastDefaultAnimation : ToastAnimation {

    override val showAnimDuration: Long
        get() = 100L
    override val showDuration: Long
        get() = 2_000L
    override val dismissAnimDuration: Long
        get() = 150L

    override fun getQueueTag(): String? {
        return this.javaClass.simpleName
    }

    override fun onBeforeAddView(view: View) {

    }

    override fun onAfterAddView(view: View) {

    }

    override fun getLayoutParams(view: View): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.CENTER
        return layoutParams
    }

    override suspend fun playShowAnim(view: View) {
        playBounceAnimation(view)
    }

    override suspend fun playDismissAnim(view: View) {
        view.apply {
            val start = 1f
            val end = 0f
            ValueAnimator.ofFloat(start, end).apply {
                duration = dismissAnimDuration
                addUpdateListener { animation ->
                    val p = animation.animatedValue as Float
                    alpha = p
                }
                addListener(
                    onStart = {
                        alpha = start
                    }
                )
                start()
            }
        }
    }

    private fun playBounceAnimation(view: View) {
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(showAnimDuration)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(showAnimDuration)
                    .start()
            }
            .start()
    }
}