package com.cn.game.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.cn.game.sdk.R
import com.cn.game.sdk.view.CombinationOkView
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 选择钱以后点击确定
 */
class MoneyOKView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, null, defStyleAttr) {
    /**
     * 是否显示取消或者确定
     */
    lateinit var llShowTop: LinearLayout

    /**
     * 取消
     */
    lateinit var ivOff: OutImageView

    /**
     * 确定
     */
    lateinit var ivOk: OutImageView
    /**
     * 显示的钱
     */
    lateinit var ivShowMoney: AppCompatTextView
    /**
     * 显示的背景钱
     */
    lateinit var ivShowBg: AppCompatImageView
    lateinit var coOkView: CombinationOkView


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

    init{
      var rootView=  LayoutInflater.from(context).inflate(R.layout.view_betting_ok, this)
        llShowTop = rootView. findViewById(R.id.llShowTop)
        ivShowMoney =rootView. findViewById(R.id.ivShowMoney)
        ivShowBg = rootView. findViewById(R.id.ivShowBg)
        coOkView=CombinationOkView(context)
        llShowTop.addView(coOkView)
        coOkView.setMoneyOKClickListener(object :CombinationOkView.CombinationOkClickListener{
            override fun onDelete() {
                onMoneyOKClickListener?.onDelete()
            }
            override fun onConfirm() {
                onMoneyOKClickListener?.onConfirm()
            }

        })





//        ivOff.setOnClickListener(object : OutImageView.OnClickListener  {
//            override fun click() {
//                Log.i("SSSSSSSSSSSSSSSs","1111111111111")
//                myToast("关闭")
//
//            }
//        })
//        ivOk.setOnClickListener(object : OutImageView.OnClickListener  {
//            override fun click() {
//                Log.i("SSSSSSSSSSSSSSSs","22222222222")
//                myToast("确定")
//
//            }
//        })





//        ivOk.clickNoRepeat {
//            onMoneyOKClickListener?.onConfirm()
//        }


    }

    /**
     * 修改显示的钱
     */
    fun setShowMoney(money:Int){
        ivShowMoney.text = showMoneyFormat(money)
        if(money<=10){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_shi))
        }else if(money<=50){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_wushi))
        }else if(money<=100){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_yibai))
        }else if(money<=200){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_liangbai))
        }else if(money<=500){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_wubai))
        }else if(money<=1000){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_qian))
        }else if(money<=2000){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_liangqian))
        }else if(money<=5000){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_wuqian))
        }else if(money<=10000){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_yiwan))
        }else if(money<=20000){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_liangwan))
        }else if(money<=50000){
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_wuwan))
        }else{
            ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_ok_shiwan))
        }

    }

    /**
     * 隐藏头部的缺点和删除
     */
    fun hiddenTop(){
        llShowTop.visibility= View.INVISIBLE
    }

    /**
     * 显示头部的确定和删除
     */
    fun showTop(){
        llShowTop.visibility= View.VISIBLE
    }

    fun showMoneyFormat(money:Int):String{
        if(money<1000){
            ivShowMoney.text = money.toString()
        }else  if (money < 10000) {
            if ((money % 1000) == 0) {
                val resultNoDecimal = (money / 1000).toFloat().round(0)
                return resultNoDecimal.toString()+"k"

            } else if ((money % 100) == 0) {
                val resultNoDecimal = (money / 1000).toFloat().round(1)
                return   resultNoDecimal.toString()+"k"
            }else{
                val resultNoDecimal = (money / 1000).toFloat().round(2)
                return   resultNoDecimal.toString()+"k"
            }

        }else{
            val tenThousand = money / 10000 % 10
            val thousand = money / 1000 % 10
            val hundred = money / 100 % 10
            val ten = money / 10 % 10
            if (thousand > 0) { // 千位有值
                if (hundred > 0) { // 百位有值
                    if (ten > 0) { // 十位有值
                        return  ("${tenThousand}.${thousand}${hundred}W+")
                    } else { // 十位没有值
                        return  ("${tenThousand}.${thousand}${hundred}W")
                    }

                } else { // 百位没有值
                    if (ten > 0) { // 十位有值
                        return  ("${tenThousand}.${thousand}W+")
                    } else { // 十位没有值
                        return  ("${tenThousand}.${thousand}W")
                    }
                }

            }else{ // 千位没有值

                if (hundred > 0) { // 百位有值
                    if (ten > 0) { // 十位有值
                        return  ("${tenThousand}.0${hundred}W+")
                    } else { // 十位没有值
                        return  ("${tenThousand}.0${hundred}W")

                    }

                }else { // 百位没有值
                    if (ten > 0) { // 十位有值

                        return  ("${tenThousand}W+")

                    } else { // 十位没有值
                        return  ("${tenThousand}W")
                    }
                }

            }


        }
        return "${money}"
    }

    /**
     * 保留几位小数并且是截取
     */
    fun Float.round(decimalPlaces: Int): Float {
        if (decimalPlaces < 0) throw IllegalArgumentException()

        val bigDecimal = BigDecimal(this.toString())
        return bigDecimal.setScale(decimalPlaces, RoundingMode.DOWN).toFloat()
    }
}