package com.cn.game.sdk.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.cn.game.sdk.ui.fast.fragment.HomeDefaultFragment
import com.cn.game.sdk.ui.fast.fragment.LeopardFragment
import com.cn.game.sdk.ui.fast.fragment.PairsDiceFragment
import com.cn.game.sdk.ui.fast.fragment.SingleDiceFragment
import com.cn.game.sdk.ui.fast.fragment.SumTotalFragment
import com.cn.game.sdk.view.MoneyOKView

/**
 * 首页超过父类的触摸事件
 */
@SuppressLint("ClickableViewAccessibility")
fun rewritingTouch(tempTouth: RelativeLayout, viewPager: ViewPager,
                   homeDefaultFragment :HomeDefaultFragment,
                   singleDiceFragment :SingleDiceFragment,
                   sumTotalFragment : SumTotalFragment,
                   pairsDiceFragment : PairsDiceFragment,
                   leopardFragment :LeopardFragment){
     tempTouth.setOnTouchListener(View.OnTouchListener { v, event ->
        // 获取触摸事件的坐标
        val x = event.rawX
        val y = event.rawY
        var locationOff = IntArray(2)
        var locationOk = IntArray(2)
        if(viewPager.currentItem==0){//第一个
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var  leftTop= homeDefaultFragment.requireView().findViewWithTag<MoneyOKView>("showLeftTopMoney")
                    var  rightTop= homeDefaultFragment.requireView().findViewWithTag<MoneyOKView>("showRightTopMoney")
                    var  leftBelow= homeDefaultFragment.requireView().findViewWithTag<MoneyOKView>("showLeftBelowMoney")
                    var  rightBelow= homeDefaultFragment.requireView().findViewWithTag<MoneyOKView>("showRightBelowMoney")
                    //用于判断父类是否是在点击范围
                    var isTouchLeftTop:Boolean=false
                    var isTouchRightTop:Boolean=false
                    var isTouchLeftBelow:Boolean=false
                    var isTouchRightBelow:Boolean=false
                    if(leftTop!=null){
                        isTouchLeftTop = isShowTouch(leftTop,x,y)
                    }
                    if(rightTop!=null){
                        isTouchRightTop = isShowTouch(rightTop,x,y)
                    }

                    if(leftBelow!=null){
                        isTouchLeftBelow = isShowTouch(leftBelow,x,y)
                    }
                    if(rightBelow!=null){
                        isTouchRightBelow = isShowTouch(rightBelow,x,y)
                    }

                    //判断放进去的控件是否为空，并且判断是不是在范围，是否隐藏了
                    if(leftTop!=null&&isTouchLeftTop&&leftTop.llShowTop.visibility== View.VISIBLE){
                        //左上
                        leftTop.ivOff.getLocationOnScreen(locationOff)
                        leftTop.ivOk.getLocationOnScreen(locationOk)
                        val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + leftTop.ivOff.width)
                                && y >= locationOff[1] && y <= (locationOff[1] + leftTop.ivOff.height)
                        val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + leftTop.ivOk.width)
                                && y >= locationOk[1] && y <= (locationOk[1] + leftTop.ivOk.height)
                        if(isTouchOnOff&& leftTop.ivOff.visibility== View.VISIBLE){
                            leftTop.ivOff.performClick()
                            return@OnTouchListener true
                        } else  if(isTouchOnOk&& leftTop.ivOk.visibility== View.VISIBLE){
                            leftTop.ivOk.performClick()
                            return@OnTouchListener true
                        }else {
                            return@OnTouchListener false
                        }

                    } else  if(rightTop!=null&&isTouchRightTop&&rightTop.llShowTop.visibility== View.VISIBLE){
                        //右上
                        rightTop.ivOff.getLocationOnScreen(locationOff)
                        rightTop.ivOk.getLocationOnScreen(locationOk)
                        val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + rightTop.ivOff.width)
                                && y >= locationOff[1] && y <= (locationOff[1] + rightTop.ivOff.height)
                        val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + rightTop.ivOk.width)
                                && y >= locationOk[1] && y <= (locationOk[1] + rightTop.ivOk.height)
                        if(isTouchOnOff&& rightTop.ivOff.visibility== View.VISIBLE){
                            rightTop.ivOff.performClick()
                            return@OnTouchListener true
                        } else  if(isTouchOnOk&& rightTop.ivOk.visibility== View.VISIBLE){
                            rightTop.ivOk.performClick()
                            return@OnTouchListener true
                        }else {
                            return@OnTouchListener false
                        }

                    }else  if(leftBelow!=null&&isTouchLeftBelow&&leftBelow.llShowTop.visibility== View.VISIBLE){
                        //左下
                        leftBelow.ivOff.getLocationOnScreen(locationOff)
                        leftBelow.ivOk.getLocationOnScreen(locationOk)
                        val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + leftBelow.ivOff.width)
                                && y >= locationOff[1] && y <= (locationOff[1] + leftBelow.ivOff.height)
                        val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + leftBelow.ivOk.width)
                                && y >= locationOk[1] && y <= (locationOk[1] + leftBelow.ivOk.height)
                        if(isTouchOnOff&& leftBelow.ivOff.visibility== View.VISIBLE){
                            leftBelow.ivOff.performClick()
                            return@OnTouchListener true
                        } else  if(isTouchOnOk&& leftBelow.ivOk.visibility== View.VISIBLE){
                            leftBelow.ivOk.performClick()
                            return@OnTouchListener true
                        }else {
                            return@OnTouchListener false
                        }

                    }else  if(rightBelow!=null&&isTouchRightBelow&&rightBelow.llShowTop.visibility== View.VISIBLE){
                        //右下
                        rightBelow.ivOff.getLocationOnScreen(locationOff)
                        rightBelow.ivOk.getLocationOnScreen(locationOk)
                        val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + rightBelow.ivOff.width)
                                && y >= locationOff[1] && y <= (locationOff[1] + rightBelow.ivOff.height)
                        val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + rightBelow.ivOk.width)
                                && y >= locationOk[1] && y <= (locationOk[1] + rightBelow.ivOk.height)
                        if(isTouchOnOff&& rightBelow.ivOff.visibility== View.VISIBLE){
                            rightBelow.ivOff.performClick()
                            return@OnTouchListener true
                        } else  if(isTouchOnOk&& rightBelow.ivOk.visibility== View.VISIBLE){
                            rightBelow.ivOk.performClick()
                            return@OnTouchListener true
                        }else {
                            return@OnTouchListener false
                        }

                    }

                    return@OnTouchListener false
                }


            }

        }




        return@OnTouchListener false
    })

}


/**
 * 判断父类的触摸
 */
fun  isShowTouch(topView:View, x: Float,y: Float):Boolean{
    var  isTouch:Boolean=false

    val locationView= IntArray(2)
    topView.getLocationOnScreen(locationView)
    isTouch = x >= locationView[0] && x <= (locationView[0] + topView.width)
            && y >= locationView[1] && y <= (locationView[1] + topView.height)
    return   isTouch
}


