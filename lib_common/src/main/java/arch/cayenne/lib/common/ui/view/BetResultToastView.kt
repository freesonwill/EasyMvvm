package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import arch.cayenne.lib.common.databinding.LayoutBetResultToastBinding
import arch.cayenne.lib.database.entity.BetResultLiteBean

class BetResultToastView: LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mBinding: LayoutBetResultToastBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        mBinding = LayoutBetResultToastBinding.inflate(layoutInflater, this, true)
    }

    fun setResult(data: List<BetResultLiteBean>) {
        if (data.size == 1) {
            setSingleResult(data.first())
        } else {
            setComboResult(data)
        }
    }

    private fun setSingleResult(data: BetResultLiteBean) {
        mBinding.tvTitle.isVisible = false
        mBinding.groupSuccess.isVisible = data.isSuccessful
        mBinding.groupFailure.isVisible = !data.isSuccessful
        if (data.isSuccessful) {
            mBinding.tvSuccessTitle.text = data.matchName
        } else {
            mBinding.tvFailureCombo.text = data.matchName
        }
    }

    private fun setComboResult(data: List<BetResultLiteBean>) {
        mBinding.tvTitle.text = data.map { it.matchName }.joinToString { ", " }
        val successfulData = data.filter { it.isSuccessful }
        val failureData = data.filter { !it.isSuccessful }
        mBinding.groupSuccess.isVisible = successfulData.isNotEmpty()
        mBinding.groupFailure.isVisible = failureData.isNotEmpty()

    }
}