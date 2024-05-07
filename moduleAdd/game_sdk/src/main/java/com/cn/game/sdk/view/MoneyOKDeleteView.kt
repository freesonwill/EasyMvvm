package com.cn.game.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.cn.game.sdk.R
import com.cn.game.sdk.tool.myToast

/**
 * 选择钱以后点击确定不超出父类的
 */
class MoneyOKDeleteView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, null, defStyleAttr) {
    /**
     * 是否显示取消或者确定
     */
    lateinit var llShowTop: LinearLayout

    /**
     * 取消
     */
    lateinit var ivOff: AppCompatImageView

    /**
     * 确定
     */
    lateinit var ivOk: AppCompatImageView
    /**
     * 显示的钱
     */
    lateinit var ivShowMoney: AppCompatTextView
    /**
     * 显示的背景钱
     */
    lateinit var ivShowBg: AppCompatImageView



    // 声明一个变量来保存回调接口
    private var onMoneyOKDeleteClickListener: MoneyOKDeleteClickListener? = null

    fun setMoneyOKClickListener(listener: MoneyOKDeleteClickListener) {
        onMoneyOKDeleteClickListener = listener
    }

    /**
     * 点击事件
     */
    interface MoneyOKDeleteClickListener {
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
        LayoutInflater.from(context).inflate(R.layout.view_betting_ok_delete, this, true)
        llShowTop = findViewById(R.id.llShowTop)
        ivOff = findViewById(R.id.ivOff)
        ivOk = findViewById(R.id.ivOk)
        ivShowMoney = findViewById(R.id.ivShowMoney)
        ivShowBg = findViewById(R.id.ivShowBg)


        //取消
        ivOff.setOnClickListener {


            myToast("Ssssssssssssssss")
            onMoneyOKDeleteClickListener?.onDelete()

        }
        //确定
        ivOk.setOnClickListener {
            myToast("AAA")
            onMoneyOKDeleteClickListener?.onConfirm()

        }

//        ivOk.clickNoRepeat {
//            onMoneyOKClickListener?.onConfirm()
//        }


    }

    /**
     * 修改显示的钱
     */
    fun setShowMoney(money:Int){
        llShowTop.visibility= View.VISIBLE
        ivShowMoney.text = money.toString()
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
     * 当已经确定钱不等于0的话就隐藏头部的
     */
    fun hiddenTop(){
        llShowTop.visibility= View.GONE
    }




}