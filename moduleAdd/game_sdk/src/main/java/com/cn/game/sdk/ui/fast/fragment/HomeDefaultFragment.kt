package com.cn.game.sdk.ui.fast.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.databinding.FragmentHomeDefaultBinding
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.utils.ComputeDefault
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKView
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jessyan.autosize.utils.AutoSizeUtils.dp2px


/**
 * 默认
 */
class HomeDefaultFragment : BaseGameFragment<HomeDefaultVm, FragmentHomeDefaultBinding>() {
    //显示类型的
    var type:Int=0
    /**
     * 左上角注区的控件确定
     */
    lateinit var showLeftTopMoney: MoneyOKView


    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt("type")
        }
        //初始化左上角
        showLeftTopMoney=MoneyOKView(requireContext())
        showLeftTopMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                ComputeDefault.offRightTopTemporarily()
                if(ComputeDefault.rightTopOk<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
                        mDatabind.rlHomeRoot.removeView(showLeftTopMoney)
                    }
                }else{
                    showLeftTopMoney.hiddenTop()
                }


            }

            override fun onConfirm() {

            }

        })

        mDatabind.rlClick.setOnClickListener {
            Log.i("SSSSSSss","======"+it.x)
        }




        appGameViewModel.ceshEvent.observe(this){

        }
        //点击右上角
        mDatabind.rlClick.clickNoRepeat {
                Log.i("BBBBBb","==="+it.x)

        }
        clickRightTop()
    }

    /**
     * 点击右上角
     */
    @SuppressLint("ClickableViewAccessibility")
    fun  clickRightTop(){
        mDatabind.rlClick.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    // 获取点击位置的坐标
                    val x = event.x
                    val y = event.y
//                    //屏幕的坐标
//                    val rax= event.rawX
//                    val ray= event.rawY

                    val location = IntArray(2)
                    mDatabind.rlClick.getLocationOnScreen(location)

                    // 获取点击位置的坐标
                    val xShow =location[0]+(mDatabind.rlClick.width / 2)
                    val yShow = location[1]+(mDatabind.rlClick.height / 2)
                    //屏幕的坐标
                    val rax= event.rawX+mDatabind.rlClick.width/2
                    val ray= event.rawY+mDatabind.rlClick.height/2

                    //========

                    // 获取 View 的边界
                    val left = v.left.toFloat()
                    val top = v.top.toFloat()
                    val right = v.right.toFloat()
                    val bottom = v.bottom.toFloat()
                    // 定义边缘阈值，可根据实际情况调整
//                    val edgeThreshold = requireContext().dp2px(20) // 像素
//                    val rihtThreshold = requireContext().dp2px(40) // 像素
                    val edgeThreshold = requireContext().dp2px(10) // 像素
                    val rihtThreshold = requireContext().dp2px(10) // 像素
                    // 判断点击位置是否在 View 的上下左右边缘
                    val isOnLeftEdge = x <= left + edgeThreshold
                    val isOnTopEdge = y <= top + edgeThreshold
                    val isOnRightEdge = x >= right - rihtThreshold
                    val isOnBottomEdge = y >= bottom - rihtThreshold
                    // 处理点击在边缘的逻辑
                    if (isOnLeftEdge || isOnTopEdge || isOnRightEdge || isOnBottomEdge) {
                        handleEdgeClick(isOnLeftEdge, isOnTopEdge, isOnRightEdge, isOnBottomEdge)
                    } else {
                        // 点击不在边缘
                        handleNonEdgeClick(xShow.toFloat(),yShow.toFloat(),rax,ray)
                    }
                }
            }
            false // 返回 true 表示事件已经被处理
        }



    }

    private fun handleEdgeClick(
        isOnLeftEdge: Boolean,
        isOnTopEdge: Boolean,
        isOnRightEdge: Boolean,
        isOnBottomEdge: Boolean
    ) {
        // 处理点击在边缘的逻辑
        if (isOnLeftEdge) {
            // 点击在左边缘
            Log.i("边缘","点击在左边缘")
        }
        if (isOnTopEdge) {
            // 点击在上边缘
            Log.i("边缘","点击在上边缘")
        }
        if (isOnRightEdge) {
            // 点击在右边缘
            Log.i("边缘","点击在右边缘")
        }
        if (isOnBottomEdge) {
            // 点击在下边缘
            Log.i("边缘","点击在下边缘")
        }
    }

    private fun handleNonEdgeClick(x:Float,y:Float,rax:Float,ray:Float) {
        //显示点击在Fragment的位置用于动画结束后显示
//        if(ComputeDefault.rightTop[0]==0&&ComputeDefault.rightTop[1]==0){
//            ComputeDefault.rightTop[0]=x.toInt()
//            ComputeDefault.rightTop[1]=y.toInt()
//        }
//        //显示在屏幕的绝对位置,动画的位置
//        if(ComputeDefault.rightTopAnimation[0]==0&&ComputeDefault.rightTopAnimation[1]==0){
//            ComputeDefault.rightTopAnimation[0]=rax.toInt()
//            ComputeDefault. rightTopAnimation[1]=ray.toInt()
//        }
        var selectNum=0
        for (i in 0 until   MyGameManager.noteList.size) {
            if(MyGameManager.noteList[i].select){
                selectNum=i
                break
            }
        }
        ComputeDefault.clickRightTopMoney(MyGameManager.noteList[selectNum].money)
        showLeftTopMoney.setShowMoney(ComputeDefault.getRightTopOwn())



        //判断控件是否加入了 if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
        //已经添加了
        if (mDatabind.rlClick.indexOfChild(showLeftTopMoney) != -1) {
            //显示在屏幕的绝对位置,动画的位置
            if(ComputeDefault.rightTopAnimation[0]!=0&&ComputeDefault.rightTopAnimation[1]!=0){
                (context as GameHomeActivity).startRightTopAnimation(ComputeDefault.rightTopAnimation[0].toFloat(),ComputeDefault.rightTopAnimation[1].toFloat())
            }
//            mDatabind.rlClick.removeView(showLeftTopMoney)
//            GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
//                delay(2000L) // 延迟1秒（1000毫秒）
//                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                params.leftMargin= ComputeDefault.rightTop[0]
//                params.topMargin= ComputeDefault.rightTop[1]
//                showLeftTopMoney.layoutParams = params
//                mDatabind.rlClick.addView(showLeftTopMoney)
//            }



        } else {

            val viewTreeObserver = showLeftTopMoney.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // 确保只监听一次
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        showLeftTopMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    } else {
                        showLeftTopMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                    // 获取视图在屏幕上的绝对位置
                    val location = IntArray(2)
                    showLeftTopMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    ComputeDefault.rightTopAnimation[0]=xOnScreen
                    ComputeDefault. rightTopAnimation[1]=yOnScreen+dp2px(context,47f)
                    // 打印位置信息
                    Log.d("Position", "xOnScreen: $xOnScreen, yOnScreen: $yOnScreen")
                    (context as GameHomeActivity).startRightTopAnimation(ComputeDefault.rightTopAnimation[0].toFloat(),ComputeDefault.rightTopAnimation[1].toFloat())
                    //显示点击在Fragment的位置用于动画结束后显示
                    if(ComputeDefault.rightTop[0]==0&&ComputeDefault.rightTop[1]==0){
                        val location = IntArray(2)
                        showLeftTopMoney.getLocationInWindow(location)
                        val layoutParams = showLeftTopMoney.layoutParams  as  (RelativeLayout.LayoutParams)
//                        ComputeDefault.rightTop[0]=  layoutParams.leftMargin
//                        ComputeDefault.rightTop[1]=layoutParams.topMargin
                        ComputeDefault.rightTop[0]= showLeftTopMoney.left
                        ComputeDefault.rightTop[1]=showLeftTopMoney.top
                    }
                }
            })



            if(ComputeDefault.rightTop[0]!=0&&ComputeDefault.rightTop[1]!=0){
                // 动态添加的视图未成功添加到布局中
//                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                mDatabind.rlHomeRoot.addView(showLeftTopMoney, params)
//                showLeftTopMoney.translationX =  ComputeDefault.rightTop[0].toFloat()
//                showLeftTopMoney.translationY =  ComputeDefault.rightTop[1].toFloat()-requireContext().dp2px(52)
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlClick.addView(showLeftTopMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeftTopMoney.layoutParams = params

            }else{
                // 动态添加的视图未成功添加到布局中
//                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                mDatabind.rlHomeRoot.addView(showLeftTopMoney, params)
//                showLeftTopMoney.translationX = x
//                showLeftTopMoney.translationY =y-requireContext().dp2px(52)



                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlClick.addView(showLeftTopMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeftTopMoney.layoutParams = params
            }





        }




//        (context as GameHomeActivity).startRightTopAnimation(ComputeDefault.rightTopAnimation[0].toFloat(),ComputeDefault.rightTopAnimation[1].toFloat())




         }

    }
