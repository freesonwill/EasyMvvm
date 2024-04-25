package com.cn.game.sdk.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.cn.game.sdk.R

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


    init{
        LayoutInflater.from(context).inflate(R.layout.view_betting_ok, this, true)
        llShowTop = findViewById(R.id.llShowTop)
        ivOff = findViewById(R.id.ivOff)
        ivOk = findViewById(R.id.ivOk)
        ivShowMoney = findViewById(R.id.ivShowMoney)
        ivShowBg = findViewById(R.id.ivShowBg)
    }

    /**
     * 修改显示的钱
     */
    fun setShowMoney(money:Int){
         if(ivShowMoney.text.isNotEmpty()){
             ivShowMoney.text=(ivShowMoney.text.toString().toInt()+money).toString()
         }else{
             ivShowMoney.text = money.toString()
         }




    }

    /**
     * 显示图片
     */
    fun setShowBg(drawable: Drawable){
        ivShowBg.setImageDrawable(drawable)
    }


}