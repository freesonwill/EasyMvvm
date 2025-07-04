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
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.entity.BetResultLiteBean
import arch.cayenne.lib.database.entity.ComboBetResultBean
import arch.cayenne.lib.database.entity.SingleBetResultBean

class BetResultToastView: LinearLayout {

    companion object {
        fun canShowToast(activity: FragmentActivity): Boolean {
            fun checkFragments(fragments: List<Fragment>): Boolean {
                for (fragment in fragments) {
                    if (fragment is Block && fragment.isResumed) return false
                    if (fragment.isAdded) {
                        if (!checkFragments(fragment.childFragmentManager.fragments)) return false
                    }
                }
                return true
            }

            return checkFragments(activity.supportFragmentManager.fragments)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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
        mBinding.tvMatch.text = data.selectionName
        mBinding.groupSuccess.isVisible = data.isSuccessful
        mBinding.tvSuccessCombo.isVisible = data.isSuccessful
        mBinding.groupFailure.isVisible = !data.isSuccessful
        mBinding.tvFailureCombo.isVisible = !data.isSuccessful
        if (data.isSuccessful) {
            mBinding.tvSuccessCombo.text = data.matchName
        } else {
            mBinding.tvFailureCombo.text = data.matchName
        }
    }

    private fun setComboResult(data: List<ComboBetResultBean>) {
        if (data.isEmpty() || data.first().matchName.isNotEmpty()) return
        mBinding.tvTitle.text = data.first().matchName.joinToString("、")
        val successfulData = data.filter { it.isSuccessful }
        val failureData = data.filter { !it.isSuccessful }
        mBinding.groupSuccess.isVisible = successfulData.isNotEmpty()
        mBinding.tvSuccessCombo.isVisible = successfulData.isNotEmpty()
        mBinding.groupFailure.isVisible = failureData.isNotEmpty()
        mBinding.tvFailureCombo.isVisible = failureData.isNotEmpty()
        val successfulTitle = successfulData
            .sortedWith(compareBy({ it.comboK }, { it.comboV }))
            .joinToString("、") { R.string.title_combo_bet.getString(it.comboK, it.comboV) }
        val failureTitle = failureData
            .sortedWith(compareBy({ it.comboK }, { it.comboV }))
            .joinToString("、") { R.string.title_combo_bet.getString(it.comboK, it.comboV) }
        mBinding.tvSuccessCombo.text = successfulTitle
        mBinding.tvFailureCombo.text = failureTitle
    }

    interface Block
}