package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.cn.game.sdk2.R
import com.xcjh.base_lib2.utils.view.clickNoRepeat

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

            /*var inagte= Intent(context, GameHomeActivity::class.java)
            context.startActivity(inagte)
            llFastClick.visibility= View.GONE
            onFastLogoClickListener?.onButtonClick()*/

        }
        // 使用完毕后记得回收 TypedArray
        typedArray.recycle()

    }


    fun setText(text: String) {
        txtTime.text = text
    }




}