package com.cn.game.sdk.view

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.startActivity
import com.cn.game.sdk.R
import com.cn.game.sdk.ui.CeshiSdkActivity
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 快三开奖logoView
 */
public class FastLogoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, null, defStyleAttr) {
    lateinit var llFastClick: LinearLayout
    lateinit var txtTime: AppCompatTextView
    // 定义一个接口用于传递按钮点击事件
    interface OnFastLogoClickListener {
        fun onButtonClick()
    }

    // 声明一个变量来保存回调接口
   private var onFastLogoClickListener: OnFastLogoClickListener? = null


    fun setFastLogoClickListener(listener: OnFastLogoClickListener) {
        onFastLogoClickListener = listener
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_fast_three_logo, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastLogoViewString)
        llFastClick = findViewById(R.id.llFastClick)
        txtTime = findViewById(R.id.txtTime)

        val customText = typedArray.getString(R.styleable.FastLogoViewString_tv_fast_time)
        if (customText != null && customText.isNotEmpty()) {
            txtTime.text=customText

        }
        //点击事件
        llFastClick.clickNoRepeat {
            val options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_up, 0)
            var inagte= Intent(context, GameHomeActivity::class.java)
            startActivity(context,inagte,options.toBundle())
//            var inagte= Intent(context, CeshiSdkActivity::class.java)
//            context.startActivity(inagte)
//            onFastLogoClickListener?.onButtonClick()

        }
        // 使用完毕后记得回收 TypedArray
        typedArray.recycle()

    }


    fun setText(text: String) {
        txtTime.text = text
    }




}