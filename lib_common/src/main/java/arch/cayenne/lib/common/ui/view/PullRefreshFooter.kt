package arch.cayenne.lib.common.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ViewSportHeaderBinding
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

/**
 * @author: wenxi
 * @date: 30/5/25 16:56
 * @description:
 */
class PullRefreshFooter : SimpleComponent, RefreshFooter {

    private lateinit var binding: ViewSportHeaderBinding
    private var loadingAnim: ObjectAnimator? = null

    constructor(context: Context) : super(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        super.onInitialized(kernel, height, maxDragHeight)
        val inflater = LayoutInflater.from(context)
        binding = ViewSportHeaderBinding.inflate(inflater, this, true)
    }

    fun setLeagueMode() {
        binding.tvTitle.setTextColor(Color.WHITE)
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.PullUpToLoad -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.pull_load)
                loadingAnim?.cancel()
                loadingAnim = binding.ivProgress.startSafeObjectAnimator(
                    "rotation",  // 属性名称
                    0f, 360f, // 从 0 度旋转到 360 度
                    duration = 1000L, // 持续时间 1 秒
                    repeatCount = ObjectAnimator.INFINITE, // 无限循环
                    interpolator = LinearInterpolator(), // 匀速旋转
                    start = true
                )
            }
            RefreshState.Loading, RefreshState.RefreshReleased -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.loading)
            }
            RefreshState.None -> {
                loadingAnim?.cancel()
                loadingAnim = null
                binding.tvTitle.text = ContextCompat.getString(context, R.string.pull_load)
            }
            else -> Unit
        }
        binding.tvTitle.requestLayout()
    }
}