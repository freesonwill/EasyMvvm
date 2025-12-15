package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import arch.cayenne.lib.common.databinding.ViewBalanceBinding
import arch.cayenne.lib.common.ui.fragment.CurrencyDialogFragment
import arch.cayenne.lib.common.ui.viewmodel.BalanceViewModel
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class BalanceView : FrameLayout {
    private val mBinding: ViewBalanceBinding
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }
    var onAddClickListener: (() -> Unit)? = null
    var viewModel: BalanceViewModel? = null
    init {
        mBinding = ViewBalanceBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun init(
        childFragmentManager: FragmentManager
    ) {
        mBinding.root.clickNoRepeat {
            rotateArrow(true)

            val location = IntArray(2)
            mBinding.root.getLocationInWindow(location)

            val offset = if(isPortrait()) {
                val h = ViewUtils.getStatusBarHeight(mBinding.root.context)
                location.last() - h + mBinding.root.measuredHeight + 7.dp2px
            } else {
                location.first() + mBinding.root.measuredWidth + 15.dp2px
            }
            val f = CurrencyDialogFragment.newInstance(isPortrait(), offset)
            f.setOnDismissListener {
                rotateArrow(false)
            }
            f.setonItemClickListener {
                setMoney(it.amount)
            }
            f.show(childFragmentManager)
        }

        mBinding.ivAdd.apply {
            addScaleOnTouchAnimation(mBinding.ivAdd)
        }.clickNoRepeat{
            onAddClickListener?.invoke()
        }
    }

    private fun rotateArrow(isExpend: Boolean) {
        mBinding.ivArrow.animate()
            .rotation(if(isExpend) 180f else 0f)
            .setDuration(200)
            .start()
    }


    private fun isPortrait(): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
    fun setMoney(money: String){
        mBinding.tvWalletBalance.text = money
    }

    fun setBalanceViewModel(viewModel: BalanceViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        viewModel.onBalanceChange.observe(lifecycleOwner) {
            setMoney(it)
        }
    }
}