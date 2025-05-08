package arch.cayenne.module.home.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import arch.cayenne.module.home.databinding.ViewSportHeaderBinding
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent


class SportHeader : SimpleComponent, RefreshHeader {
    private lateinit var binding: ViewSportHeaderBinding

    private lateinit var progressDrawable: ProgressDrawable

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
        binding.tvTitle.text = "下拉刷新..."
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        super.onStateChanged(refreshLayout, oldState, newState)
        when (newState) {
            RefreshState.PullDownToRefresh -> { progressDrawable.start() }
            RefreshState.Refreshing, RefreshState.RefreshReleased -> { binding.tvTitle.text = "刷新中..." }
            RefreshState.None -> {
                progressDrawable.stop()
                binding.tvTitle.text = "下拉刷新..."
            }
            else -> Unit
        }
    }
}