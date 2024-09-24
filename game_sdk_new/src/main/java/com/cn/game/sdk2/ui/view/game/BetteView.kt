package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.enums.ChipsEnum
import com.cn.game.sdk2.databinding.ViewBettingBinding

class BetteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var parentView: ViewGroup? = null

    var ivShowBg: AppCompatImageView

    var binding: ViewBettingBinding? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_betting, this).apply {
            binding = ViewBettingBinding.bind(this)
            ivShowBg = binding!!.ivShowBg
        }
    }

    fun updateBetteIcon(money: Int) {
        val res = ChipsEnum.getNoValueResByMoney(money)
        ivShowBg.setImageDrawable(
            ContextCompat.getDrawable(context, res)
        )
    }
}