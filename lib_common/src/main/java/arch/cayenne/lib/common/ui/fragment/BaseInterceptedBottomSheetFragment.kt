package arch.cayenne.lib.common.ui.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheerFragment
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.gesture.TikTokGesture
import arch.cayenne.lib.common.ui.view.InterceptedConstraintLayout

abstract class BaseInterceptedBottomSheetFragment<VM : BaseViewModel, VB : ViewBinding> :
    BasePreLoadBottomSheerFragment<VM, VB>() {

    override fun onStart() {
        super.onStart()
        setGesture()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setGesture() {
        val v = mBinding.root
        if (v is InterceptedConstraintLayout) {
            val tikTokGesture = TikTokGesture(v)
            tikTokGesture.setListener(object : TikTokGesture.TikTokGestureListener {
                override fun onHorizontalFling() {
                }

                override fun onHorizontalScroll(offsetX: Float) {
                    Log.d("abcd", "+++++ $offsetX")
                    sheetContainer?.translationY = offsetX
                }

                override fun onActionUp() {
                    resetSheetTranslation()
                }

            })
        }
    }

    private fun resetSheetTranslation() {
        sheetContainer?.let {
            ValueAnimator.ofFloat(it.translationY, 0f).apply {
                duration = 100
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    it.translationY = value
                }
                start()
            }
        }

    }
}