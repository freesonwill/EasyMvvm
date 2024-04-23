package com.cn.game.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.cn.game.sdk.R
import com.xcjh.base_lib.utils.view.clickNoRepeat

public class FastLogoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, null, defStyleAttr) {
    lateinit var llFastClick: LinearLayout
    lateinit var txtTime: AppCompatTextView
    // 定义一个接口用于传递按钮点击事件
    interface OnFastLogoClickListener {
        fun onButtonClick()
    }

    // 声明一个变量来保存回调接口
    private var onFastLogoClickListener: OnFastLogoClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_fast_three_logo, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastLogoViewString)
        llFastClick = findViewById(R.id.llFastClick)
        txtTime = findViewById(R.id.txtTime)

        val customText = typedArray.getString(R.styleable.FastLogoViewString_tv_fast_time)
        if(customText!!.isNotEmpty()){
            txtTime.text=customText

        }
        //点击事件
        llFastClick.clickNoRepeat {
            onFastLogoClickListener?.onButtonClick()

        }
        // 使用完毕后记得回收 TypedArray
        typedArray.recycle()

    }


    fun setText(text: String) {
        txtTime.text = text
    }



}