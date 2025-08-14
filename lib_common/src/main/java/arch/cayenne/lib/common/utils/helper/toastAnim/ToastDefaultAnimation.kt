package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener

open class ToastDefaultAnimation : ToastAnimation {

    override val animDuration: Long
        get() = 150L
    override val showDuration: Long
        get() = 2_000L

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
                duration = animDuration
                addUpdateListener { animation ->
                    val p = animation.animatedValue as Float
//                    scaleX = p
                    alpha = p
//                    scaleY = p
                }
                addListener(
                    onStart = {
//                        scaleX = start
                        alpha = start
//                        scaleY = start
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
            .setDuration(75)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(75)
                    .start()
            }
            .start()
    }
}