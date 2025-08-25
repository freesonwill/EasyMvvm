package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ViewSportHeaderBinding
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

class PullRefreshHeader : SimpleComponent, RefreshHeader {

    private lateinit var binding: ViewSportHeaderBinding

    constructor(context: Context) : super(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        super.onInitialized(kernel, height, maxDragHeight)
        if (childCount > 0) {
            removeAllViews()
        }
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
            RefreshState.PullDownToRefresh -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.pull_to_refresh)
            }

            RefreshState.ReleaseToRefresh -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.release_to_refresh)
            }

            RefreshState.Refreshing, RefreshState.RefreshReleased -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.refresh_data)
            }

            RefreshState.RefreshFinish, RefreshState.None -> {
                binding.tvTitle.text = ContextCompat.getString(context, R.string.pull_to_refresh)
            }

            else -> Unit
        }
        binding.tvTitle.requestLayout()
    }
}