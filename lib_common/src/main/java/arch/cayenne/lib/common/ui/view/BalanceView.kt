package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import arch.cayenne.lib.common.databinding.ViewBalanceBinding
import arch.cayenne.lib.common.ui.fragment.CurrencyDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class BalanceView : FrameLayout {
    private val mBinding: ViewBalanceBinding
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    init {
        mBinding = ViewBalanceBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun init(
        childFragmentManager: FragmentManager
    ) {
        mBinding.root.clickNoRepeat {
            val h = ViewUtils.getStatusBarHeight(mBinding.root.context)
            val location = IntArray(2)
            mBinding.root.getLocationInWindow(location)
            val positionY = location.last() - h + mBinding.root.measuredHeight + 4
            CurrencyDialogFragment.newInstance(positionY).show(childFragmentManager)
        }
    }
}