package com.cn.game.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.cn.game.sdk.R
import com.cn.game.sdk.tool.myToast

class BeyoundGoView  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, null, defStyleAttr) {
    /**
     * 取消
     */
      var ivOff: AppCompatImageView

    /**
     * 确定
     */
      var ivOk: AppCompatImageView


    init {
        val inflater = LayoutInflater.from(context)

        inflater.inflate(R.layout.view_go_beyond, this, true)
        ivOff = findViewById(R.id.ivOff)
        ivOk = findViewById(R.id.ivOk)
        ivOff.setOnClickListener {
            myToast("22222222222222")
        }

    }

}