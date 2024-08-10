package com.cn.game.sdk2.ui.view.game

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.cn.game.sdk2.R

/**
 * 快三开奖结果
 */
@Deprecated("")
class OpenResultView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, null, defStyleAttr) {
    /**
     * 日期
     */
    lateinit var txtOpenTime: AppCompatTextView

    /**
     * 第一个
     */
    lateinit var ivOpenOne: AppCompatImageView
    /**
     * 第二个
     */
    lateinit var ivOpenTwo: AppCompatImageView
    /**
     * 第三个
     */
    lateinit var ivOpenThree: AppCompatImageView


    init {
        LayoutInflater.from(context).inflate(R.layout.view_open_result, this, true)
        txtOpenTime=findViewById(R.id.txtOpenTime)
        ivOpenOne=findViewById(R.id.ivOpenOne)
        ivOpenTwo=findViewById(R.id.ivOpenTwo)
        ivOpenThree=findViewById(R.id.ivOpenThree)

    }

    fun  setDate(){

    }

}