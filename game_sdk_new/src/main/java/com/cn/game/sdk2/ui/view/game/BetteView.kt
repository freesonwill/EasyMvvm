package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cn.game.sdk2.R
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
        ivShowBg.setImageDrawable(
            when {
                money < 5000 -> ContextCompat.getDrawable(context, R.mipmap.game_sdk_icon_ok_shi)
                money in 5000..9999 -> ContextCompat.getDrawable(context, R.mipmap.game_sdk_icon_ok_wushi)
                money in 10000..19999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_yibai
                )

                money in 20000..49999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_liangbai
                )

                money in 50000..99999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_wubai
                )

                money in 100000..199999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_qian
                )

                money in 200000..499999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_liangqian
                )

                money in 500000..999999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_wuqian
                )

                money in 1000000..1999999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_yiwan
                )

                money in 2000000..4999999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_liangwan
                )

                money in 5000000..9999999 -> ContextCompat.getDrawable(
                    context,
                    R.mipmap.game_sdk_icon_ok_wuwan
                )

                else -> ContextCompat.getDrawable(context, R.mipmap.game_sdk_icon_ok_shiwan)
            }
        )
    }
}