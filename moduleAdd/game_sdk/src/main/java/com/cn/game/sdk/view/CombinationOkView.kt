package com.cn.game.sdk.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.cn.game.sdk.R
import com.cn.game.sdk.game.GameData
import com.cn.game.sdk.game.GameServiceBack
import com.xcjh.base_lib.utils.logi


@SuppressLint("ClickableViewAccessibility")
class CombinationOkView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var activity: Activity? = null
    /**
     * 取消
     */
    lateinit var ivOff: AppCompatImageView

    /**
     * 确定
     */
    lateinit var ivOk: AppCompatImageView
    lateinit var rlClickOff: RelativeLayout
    lateinit var rlClickOk: RelativeLayout

    /**
     * 是否显示出来
     */
   private var isShow:Boolean=false

   fun getIvOffView():AppCompatImageView{
       return ivOff
   }
    fun getIvOkView():AppCompatImageView{
        return ivOk
    }

    init{
        val inflater = LayoutInflater.from(context)
      var rootView=  inflater.inflate(R.layout.combination_ok_view, this)

        ivOff =rootView.findViewById(R.id.ivOff)
        ivOk = rootView.findViewById(R.id.ivOk)
        rlClickOff = rootView.findViewById(R.id.rlClickOff)
        rlClickOk = rootView.findViewById(R.id.rlClickOk)
        activity = context as Activity

//        GameData.getInstance().rootView?.setOnTouchListener(OnTouchListener { v, event ->
//            when (event.action) {
//
//                MotionEvent.ACTION_DOWN -> {
//                    // 判断隐藏显示
//                    Log.i("SSSSSSSSSSSSSssssss","[==========="+isShow)
//                    if (!isShow){
//                        return@OnTouchListener false
//                    }
//                    val locationOff = IntArray(2)
//                    val locationOk = IntArray(2)
//                    rlClickOff.getLocationOnScreen(locationOff)
//                    rlClickOk.getLocationOnScreen(locationOk)
//                    val x = event.rawX
//                    val y = event.rawY
//                    // 判断触摸位置是否在 ivOff 区域内
//                    val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + ivOff.width)
//                            && y >= locationOff[1] && y <= (locationOff[1] + ivOff.height)
//
//                    // 判断触摸位置是否在 ivOk 区域内
//                    val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + ivOk.width)
//                            && y >= locationOk[1] && y <= (locationOk[1] + ivOk.height)
//
//                    if (isTouchOnOff) {
//                        Log.i("DDDDDDDDDDDDdd", "触摸在 ivOff 区域内")
//                        onCombinationOkClickListener?.onDelete()
//                        // 处理 ivOff 的点击逻辑
//                        return@OnTouchListener true
//                    } else if (isTouchOnOk) {
//                        Log.i("DDDDDDDDDDDDdd", "触摸在 ivOk 区域内")
//                        onCombinationOkClickListener?.onConfirm()
//                        // 处理 ivOk 的点击逻辑
//                        return@OnTouchListener true
//                    }
//
//
//                }
//            }
//            false
//        })
//        ivOff.setOnClickListener {
//            Log.i("AAAAAAA","============")
//        }




    }




    // 声明一个变量来保存回调接口
    private var onCombinationOkClickListener: CombinationOkClickListener? = null

    fun setMoneyOKClickListener(listener: CombinationOkClickListener) {
        onCombinationOkClickListener = listener
    }

    fun setShowView(show:Boolean){
        isShow=show
    }

    /**
     * 点击事件
     */
    interface CombinationOkClickListener {
        /**
         * 关闭
         */
        fun onDelete()

        /**
         * 确定
         */
        fun onConfirm()
    }




}