package arch.cayenne.lib.common.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import arch.cayenne.lib.common.databinding.ViewLoadingBinding
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator

class LoadingView : LinearLayout {
    private lateinit var binding: ViewLoadingBinding
    private var loadingAnim: ObjectAnimator? = null

    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        val inflater = LayoutInflater.from(context)
        binding = ViewLoadingBinding.inflate(inflater, this)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == View.VISIBLE) {
            loadingAnim?.cancel()
            loadingAnim = binding.ivProgress.startSafeObjectAnimator(
                "rotation",  // 属性名称
                0f, 360f, // 从 0 度旋转到 360 度
                duration = 1000L, // 持续时间 1 秒
                repeatCount = ObjectAnimator.INFINITE, // 无限循环
                interpolator = LinearInterpolator(), // 匀速旋转
                start = true
            )
        } else {
            loadingAnim?.cancel()
            loadingAnim = null
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onDetachedFromWindow() {
        loadingAnim?.cancel()
        loadingAnim = null
        super.onDetachedFromWindow()
    }
}