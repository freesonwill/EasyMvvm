package com.cn.game.sdk.ui.fast.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.cn.game.sdk.R
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.databinding.FragmentHomeDefaultBinding
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.utils.ComputeDefault
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKView
import com.drake.brv.utils.models
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 默认
 */
class HomeDefaultFragment : BaseGameFragment<HomeDefaultVm, FragmentHomeDefaultBinding>() {
    //显示类型的
    var type:Int=0
    /**
     * 右上角注区的控件确定
     */
    lateinit var showRightTopMoney: MoneyOKView


    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt("type")
        }
        //初始化右上角
        showRightTopMoney=MoneyOKView(requireContext())
        showRightTopMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {

                ComputeDefault.offRightTopTemporarily()
                if(ComputeDefault.rightTopOk<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlHomeRoot.indexOfChild(showRightTopMoney) != -1) {
                        mDatabind.rlHomeRoot.removeView(showRightTopMoney)
                    }
                }else{
                    showRightTopMoney.hiddenTop()
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
                    val rax= event.rawX
                    val ray= event.rawY
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
                        handleNonEdgeClick(x,y,rax,ray)
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
        if(ComputeDefault.rightTop[0]==0&&ComputeDefault.rightTop[1]==0){
            ComputeDefault.rightTop[0]=x.toInt()
            ComputeDefault.rightTop[1]=y.toInt()
            ComputeDefault.rightTopLast[0]=x.toInt()
            ComputeDefault.rightTopLast[1]=y.toInt()
        }
        //显示在屏幕的绝对位置,动画的位置
        if(ComputeDefault.rightTopAnimation[0]==0&&ComputeDefault.rightTopAnimation[1]==0){
            ComputeDefault.rightTopAnimation[0]=rax.toInt()
            ComputeDefault. rightTopAnimation[1]=ray.toInt()
            ComputeDefault.rightTopAnimationLast[0]=rax.toInt()
            ComputeDefault. rightTopAnimationLast[1]=ray.toInt()
        }
        var selectNum=0
        for (i in 0 until   MyGameManager.noteList.size) {
            if(MyGameManager.noteList[i].select){
                selectNum=i
                break
            }
        }
        ComputeDefault.clickRightTopMoney(MyGameManager.noteList[selectNum].money)
        showRightTopMoney.setShowMoney(ComputeDefault.getRightTopOwn())



        //判断控件是否加入了
        if (mDatabind.rlHomeRoot.indexOfChild(showRightTopMoney) != -1) {

        } else {
            if(ComputeDefault.rightTop[0]!=0&&ComputeDefault.rightTop[1]!=0){
                // 动态添加的视图未成功添加到布局中
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showRightTopMoney, params)
                showRightTopMoney.translationX =  ComputeDefault.rightTop[0].toFloat()
                showRightTopMoney.translationY =  ComputeDefault.rightTop[1].toFloat()-requireContext().dp2px(52)
            }else{
                // 动态添加的视图未成功添加到布局中
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showRightTopMoney, params)
                showRightTopMoney.translationX = x
                showRightTopMoney.translationY =y-requireContext().dp2px(52)
            }

        }

        (context as GameHomeActivity).startRightTopAnimation(ComputeDefault.rightTopAnimation[0].toFloat(),ComputeDefault.rightTopAnimation[1].toFloat())

             }

    }
