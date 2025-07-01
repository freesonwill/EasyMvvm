package com.walisport.module.live.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.live.R
import com.walisport.module.live.databinding.ViewRoundHexagonBinding

/**
 * 六边形数据展示控件
 */

class RoundHexagonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mBinding: ViewRoundHexagonBinding =
        ViewRoundHexagonBinding.inflate(LayoutInflater.from(context), this, true)

    private var hexType: Int = 1

    companion object {
        const val RATE_0 = 0
        const val RATE_1 = 1
        const val RATE_2 = 2
        const val RATE_3 = 3
        const val RATE_4 = 4
        const val RATE_5 = 5
        const val RATE_6 = 6
        const val RATE_7 = 7
        const val RATE_8 = 8
        const val RATE_9 = 9
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundHexagonView)
        hexType = typedArray.getInt(R.styleable.RoundHexagonView_viewType, 1)
        typedArray.recycle()
    }

    fun setData(home: Int, away: Int) {
        val rate = if (home == 0 && away == 0) {
            RATE_5
        } else {
            val temp = (home.toFloat() / (home.toFloat() + away.toFloat())) * 100f
            if (temp == 50f) {
                RATE_5
            } else if (temp > 50 && temp <= 60) {
                RATE_6
            } else {
                (temp / 10f).toInt()
            }
        }
        mBinding.ivBack.background = getHexagonBack(rate)
        if (hexType == RATE_1) {
            mBinding.ivArrow.background = getAttackArrow(rate)
        } else if (hexType == RATE_2) {
            mBinding.ivArrow.background = getDangerArrow(rate)
        } else if (hexType == RATE_3) {
            mBinding.ivArrow.background = getControlArrow(rate)
        }
    }

    private fun getHexagonBack(rate: Int): Drawable? {
        return when (rate) {
            RATE_0 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_0
            )

            RATE_1 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_1
            )

            RATE_2 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_2
            )

            RATE_3 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_3
            )

            RATE_4 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_4
            )

            RATE_5 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_5
            )

            RATE_6 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_6
            )

            RATE_7 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_7
            )

            RATE_8 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_8
            )

            RATE_9 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_9
            )

            else -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.bg_hexagon_10
            )
        }
    }

    private fun getAttackArrow(rate: Int): Drawable? {
        return if (rate < RATE_5) {
            SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_arrow_left
            )
        } else if (rate == RATE_5) {
            SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_arrow_middle
            )
        } else {
            SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_arrow_right
            )
        }
    }

    private fun getDangerArrow(rate: Int): Drawable? {
        return when (rate) {
            in RATE_0..RATE_1 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_0
            )

            RATE_2 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_2
            )

            RATE_3 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_3
            )

            RATE_4 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_4
            )

            RATE_5 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_5
            )

            RATE_6 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_6
            )

            RATE_7 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_7
            )

            else -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_danger_8
            )
        }
    }

    private fun getControlArrow(rate: Int): Drawable? {
        return when (rate) {
            in RATE_0..RATE_3 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_control_0
            )

            RATE_4 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_control_4
            )

            RATE_5 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_control_5
            )

            RATE_6 -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_control_6
            )

            else -> SkinnableResourceManager.getDrawable(
                mBinding.root.context,
                R.drawable.icon_control_7
            )
        }
    }
}