package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.walisport.module.live.databinding.ViewProgressStatisticsBinding

/**
 * 赛况页技术统计布局控件中的红蓝双方数据对比控件
 */

class TechProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewProgressStatisticsBinding =
        ViewProgressStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(techType: String, left: Int, right: Int) {
        mBinding.tvProgressName.text = techType
        mBinding.tvProgressLeft.text = left.toString()
        mBinding.tvProgressRight.text = right.toString()
        mBinding.proLeft.progress = left
        mBinding.proRight.progress = right
    }
}