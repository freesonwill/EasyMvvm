package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ViewSportHeaderBinding
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
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

    private lateinit var progressDrawable: ProgressDrawable

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
        progressDrawable = ProgressDrawable()
        binding.ivProgress.setImageDrawable(progressDrawable)

    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        super.onStateChanged(refreshLayout, oldState, newState)
        when (newState) {
            RefreshState.PullUpToLoad -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.pull_load)
                progressDrawable.start()
            }

            RefreshState.Loading, RefreshState.RefreshReleased -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.loading)
            }

            RefreshState.None -> {
                progressDrawable.stop()
                binding.tvTitle.text = ContextCompat.getString(context, R.string.pull_load)
            }

            else -> Unit
        }
    }
}