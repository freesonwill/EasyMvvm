package com.walisport.module.topup.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.ViewPayMethodButtonBinding

/**
 * 支付方式按钮
 */

class PayMethodButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val CHAT_PAY = "WeChatPay"
        const val BANK_PAY = "BANK"
        const val RMB_PAY = "RMB"
        const val YUN_PAY = "YUN"
        const val EE_PAY = "EE"
    }

    private val mBinding: ViewPayMethodButtonBinding =
        ViewPayMethodButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayMethodButton)
        val type = typedArray.getString(R.styleable.PayMethodButton_type)
        val recommend = typedArray.getBoolean(R.styleable.PayMethodButton_recommend, false)
        typedArray.recycle()
        mBinding.ivPayIcon.background = getPayMethodIcon(type)
        mBinding.tvPayMethod.text = getPayMethodText(type)
        mBinding.tvPayRecommend.visibility = if (recommend) View.VISIBLE else View.INVISIBLE
    }

    private fun getPayMethodIcon(type: String?): Drawable {
        return when (type) {
            CHAT_PAY -> R.drawable.icon_pay_wechat.getDrawable()
            EE_PAY -> R.drawable.icon_pay_ee.getDrawable()
            BANK_PAY -> R.drawable.icon_pay_union.getDrawable()
            RMB_PAY -> R.drawable.icon_pay_rmb.getDrawable()
            YUN_PAY -> R.drawable.icon_pay_yun.getDrawable()
            else -> R.drawable.icon_pay_ali.getDrawable()
        }
    }

    private fun getPayMethodText(type: String?): String {
        return when (type) {
            CHAT_PAY -> R.string.pay_wechat.getString()
            EE_PAY -> R.string.pay_ee.getString()
            BANK_PAY -> R.string.pay_bank.getString()
            RMB_PAY -> R.string.pay_rmb.getString()
            YUN_PAY -> R.string.pay_yun.getString()
            else -> R.string.pay_ali.getString()
        }
    }

    fun setPayMethodSelected(isSelected: Boolean){
        mBinding.tvPayMethod.isSelected = isSelected
        mBinding.layButton.isSelected = isSelected
    }
}