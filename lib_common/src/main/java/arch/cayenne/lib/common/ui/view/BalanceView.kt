package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import arch.cayenne.lib.common.databinding.ViewBalanceBinding

class BalanceView : FrameLayout {
    val mBinding: ViewBalanceBinding
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    init {
        mBinding = ViewBalanceBinding.inflate(LayoutInflater.from(context), this, true)

    }
}