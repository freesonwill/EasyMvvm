package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.LayoutBetResultToastBinding
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetResultLiteBean
import arch.cayenne.lib.database.entity.ComboBetResultBean
import arch.cayenne.lib.database.entity.SingleBetResultBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager

class BetResultToastView : LinearLayout {

    companion object {
        fun canShowToast(activity: FragmentActivity): Boolean {
            fun checkFragments(fragments: List<Fragment>): Boolean {
                for (fragment in fragments) {
                    if (fragment is Block && fragment.isResumed && fragment.view?.isVisible == true) {
                        return false
                    }
                    if (fragment.isAdded) {
                        if (!checkFragments(fragment.childFragmentManager.fragments)) {
                            return false
                        }
                    }
                }
                return true
            }

            return checkFragments(activity.supportFragmentManager.fragments)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mBinding: LayoutBetResultToastBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        mBinding = LayoutBetResultToastBinding.inflate(layoutInflater, this, true)
    }

    fun setResult(data: List<BetResultLiteBean>) {
        if (data.size == 1 && data.first() is SingleBetResultBean) {
            setSingleResult(data.first() as SingleBetResultBean)
        } else {
            val newData = data.filterIsInstance<ComboBetResultBean>()
            setComboResult(newData)
        }
    }

    private fun setSingleResult(data: SingleBetResultBean) {
        mBinding.ivResult.setImageDrawable(
            SkinnableResourceManager.getDrawable(
                context,
                if (data.isSuccessful) R.drawable.icon_bet_result_success else R.drawable.icon_bet_result_failure
            )
        )
        mBinding.tvTitle.text =
            context.getString(if (data.isSuccessful) R.string.title_result_success_bet else R.string.title_result_fail_bet)

        val resultStr =
            "${data.matchName} ${data.selectionName} ${data.currency}${data.money.getFormalMoney()} ${
                context.getString(if (data.isSuccessful) R.string.title_result_success_bet_done else R.string.title_result_fail_bet_done)
            }"
        mBinding.tvResult.text = resultStr
    }

    private fun setComboResult(data: List<ComboBetResultBean>) {
    }

    interface Block
}