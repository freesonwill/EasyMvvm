package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.ViewBettingOkBinding
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.xcjh.base_lib.utils.StringFormatUtil
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 选择钱以后点击确定
 */
class MoneyOKView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    //总和没有添加到gameAreaView中，保存下被添加的父Layout用于续压
    var parentView: ViewGroup? = null
    /**
     * 是否显示取消或者确定
     */
    var llShowTop: LinearLayout

    /**
     * 取消
     */
    var ivOff: ImageView

    /**
     * 确定
     */
    var ivOk: ImageView

    /**
     * 显示的钱
     */
    var ivShowMoney: AppCompatTextView

    /**
     * 显示的背景钱
     */
    var ivShowBg: AppCompatImageView

    var binding: ViewBettingOkBinding? = null


    // 声明一个变量来保存回调接口
    private var onMoneyOKClickListener: OnMoneyOKClickListener? = null

    fun setMoneyOKClickListener(listener: OnMoneyOKClickListener) {
        onMoneyOKClickListener = listener
    }

    /**
     * 点击事件
     */
    interface OnMoneyOKClickListener {
        /**
         * 关闭
         */
        fun onDelete()

        /**
         * 确定
         */
        fun onConfirm()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_betting_ok, this).apply {
            binding = ViewBettingOkBinding.bind(this)
            llShowTop = binding!!.llShowTop
            ivShowMoney = binding!!.ivShowMoney
            ivShowBg = binding!!.ivShowBg
            ivOff = binding!!.ivOff
            ivOk = binding!!.ivOk
        }

        ivOff.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia(context)
            onMoneyOKClickListener?.onDelete()
        }

        ivOk.clickNoRepeat {
            PromptSoundPlay.playAudio(context)
            onMoneyOKClickListener?.onConfirm()
        }
    }

    /**
     * 修改显示的钱
     */
    fun setShowMoney(money: Int) {
        ivShowMoney.text = showMoneyFormat(money)
        ivShowBg.setImageDrawable(
            when {
                money < 5000 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_shi)
                money in 5000..9999 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_wushi)
                money in 10000..19999 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_yibai)
                money in 20000..49999 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_liangbai)
                money in 50000..99999 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_wubai)
                money in 100000..199999 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_qian)
                money in 200000..499999 -> ContextCompat.getDrawable(
                    context,
                    R.drawable.icon_ok_liangqian
                )

                money in 500000..999999 -> ContextCompat.getDrawable(context, R.drawable.icon_ok_wuqian)
                money in 1000000..1999999 -> ContextCompat.getDrawable(
                    context,
                    R.drawable.icon_ok_yiwan
                )

                money in 2000000..4999999 -> ContextCompat.getDrawable(
                    context,
                    R.drawable.icon_ok_liangwan
                )

                money in 5000000..9999999 -> ContextCompat.getDrawable(
                    context,
                    R.drawable.icon_ok_wuwan
                )

                else -> ContextCompat.getDrawable(context, R.drawable.icon_ok_shiwan)
            }
        )
    }

    /**
     * 隐藏头部的缺点和删除
     */
    fun hiddenTop() {
        llShowTop.visibility = View.INVISIBLE
    }

    /**
     * 显示头部的确定和删除
     */
    fun showTop() {
        llShowTop.visibility = View.VISIBLE
        bringToFront()
    }

    private fun showMoneyFormat(money: Int): String {
        if (money < 1000 * 100) {
            return money.formatRealMoney()
        } else if (money < 10000 * 100) {
            if ((money % 100000) == 0) {
                val resultNoDecimal = (money / 100000f).round(0)
                return resultNoDecimal.toString() + "k"

            } else if ((money % 10000) == 0) {
                val resultNoDecimal = (money / 100000f).round(1)
                return resultNoDecimal.toString() + "k"
            } else {
                val resultNoDecimal = (money / 100000f).round(2)
                return resultNoDecimal.toString() + "k"
            }
        } else {
            val tenThousand = money / 1000000
            val thousand = money / 100000 % 10
            val hundred = money / 10000 % 10
            val ten = money / 1000 % 10
            if (thousand > 0) { // 千位有值
                return if (hundred > 0) { // 百位有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}.${thousand}${hundred}W+")
                    } else { // 十位没有值
                        ("${tenThousand}.${thousand}${hundred}W")
                    }

                } else { // 百位没有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}.${thousand}W+")
                    } else { // 十位没有值
                        ("${tenThousand}.${thousand}W")
                    }
                }

            } else { // 千位没有值
                return if (hundred > 0) { // 百位有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}.0${hundred}W+")
                    } else { // 十位没有值
                        ("${tenThousand}.0${hundred}W")
                    }

                } else { // 百位没有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}W+")
                    } else { // 十位没有值
                        ("${tenThousand}W")
                    }
                }
            }
        }
    }

    /**
     * 保留几位小数并且是截取
     */
    private fun Float.round(decimalPlaces: Int): Float {
        if (decimalPlaces < 0) throw IllegalArgumentException()

        val bigDecimal = BigDecimal(this.toString())
        return bigDecimal.setScale(decimalPlaces, RoundingMode.DOWN).toFloat()
    }
}