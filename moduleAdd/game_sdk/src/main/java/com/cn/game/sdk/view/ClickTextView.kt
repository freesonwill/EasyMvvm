package com.cn.game.sdk.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

@SuppressLint("ClickableViewAccessibility")
class ClickTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {


    init {
    setOnTouchListener(View.OnTouchListener { v, event ->

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (v is CombinationOkView) {
                    Log.i("VVVVVVVVV","1111111111111")

                }else{
                    Log.i("VVVVVVVVV","22222222222222")
                }

            }

        }
                    return@OnTouchListener false
      })
    }


}