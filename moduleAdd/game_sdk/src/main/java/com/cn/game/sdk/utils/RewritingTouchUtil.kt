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

        }else if(viewPager.currentItem==4) {//总和
            var  showSumSiMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumSiMoney")
            var  showSumWuMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumWuMoney")
            var  showSumLiuMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumLiuMoney")
            var  showSumQiMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumQiMoney")
            var  showSumBaMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumBaMoney")
            var  showSumJiuMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumJiuMoney")
            var  showSumShiMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiMoney")
            var  showSumShiYiMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiYiMoney")
            var  showSumShiErMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiErMoney")
            var  showSumShiSanMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiSanMoney")
            var  showSumShiSiMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiSiMoney")
            var  showSumShiWuMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiWuMoney")
            var  showSumShiLiuMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiLiuMoney")
            var  showSumShiQiMoney= sumTotalFragment.requireView().findViewWithTag<MoneyOKView>("showSumShiQiMoney")
            //用于判断父类是否是在点击范围
            var isTouchSumSi:Boolean=false
            var isTouchSumWu:Boolean=false
            var isTouchSumLiu:Boolean=false
            var isTouchSumQi :Boolean=false
            var isTouchSumBa :Boolean=false
            var isTouchSumJiu :Boolean=false
            var isTouchSumShi :Boolean=false
            var isTouchSumShiYi :Boolean=false
            var isTouchSumShiEr :Boolean=false
            var isTouchSumShiSan :Boolean=false
            var isTouchSumShiSi :Boolean=false
            var isTouchSumShiWu :Boolean=false
            var isTouchSumShiLiu :Boolean=false
            var isTouchSumShiQi :Boolean=false


            if(showSumSiMoney!=null){
                isTouchSumSi = isShowTouch(showSumSiMoney,x,y)
            }
            if(showSumWuMoney!=null){
                isTouchSumWu = isShowTouch(showSumWuMoney,x,y)
            }

            if(showSumLiuMoney!=null){
                isTouchSumLiu = isShowTouch(showSumLiuMoney,x,y)
            }
            if(showSumQiMoney!=null){
                isTouchSumQi = isShowTouch(showSumQiMoney,x,y)
            }
            if(showSumBaMoney!=null){
                isTouchSumBa = isShowTouch(showSumBaMoney,x,y)
            }
            if(showSumJiuMoney!=null){
                isTouchSumJiu = isShowTouch(showSumJiuMoney,x,y)
            }
            if(showSumShiMoney!=null){
                isTouchSumShi = isShowTouch(showSumShiMoney,x,y)
            }

            if(showSumShiYiMoney!=null){
                isTouchSumShiYi = isShowTouch(showSumShiYiMoney,x,y)
            }
            if(showSumShiErMoney!=null){
                isTouchSumShiEr = isShowTouch(showSumShiErMoney,x,y)
            }

            if(showSumShiSanMoney!=null){
                isTouchSumShiSan= isShowTouch(showSumShiSanMoney,x,y)
            }

            if(showSumShiSiMoney!=null){
                isTouchSumShiSi= isShowTouch(showSumShiSiMoney,x,y)
            }
            if(showSumShiWuMoney!=null){
                isTouchSumShiWu= isShowTouch(showSumShiWuMoney,x,y)
            }
            if(showSumShiLiuMoney!=null){
                isTouchSumShiLiu= isShowTouch(showSumShiLiuMoney,x,y)
            }
            if(showSumShiQiMoney!=null){
                isTouchSumShiQi= isShowTouch(showSumShiQiMoney,x,y)
            }



            //判断放进去的控件是否为空，并且判断是不是在范围，是否隐藏了
            if(showSumSiMoney!=null&&isTouchSumSi&&showSumSiMoney.llShowTop.visibility== View.VISIBLE){
                //4
                showSumSiMoney.ivOff.getLocationOnScreen(locationOff)
                showSumSiMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumSiMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumSiMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumSiMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumSiMoney.ivOk.height)
                if(isTouchOnOff&& showSumSiMoney.ivOff.visibility== View.VISIBLE){
                    showSumSiMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumSiMoney.ivOk.visibility== View.VISIBLE){
                    showSumSiMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumWuMoney!=null&&isTouchSumWu&&showSumWuMoney.llShowTop.visibility== View.VISIBLE){
                //5
                showSumWuMoney.ivOff.getLocationOnScreen(locationOff)
                showSumWuMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumWuMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumWuMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumWuMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumWuMoney.ivOk.height)
                if(isTouchOnOff&& showSumWuMoney.ivOff.visibility== View.VISIBLE){
                    showSumWuMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumWuMoney.ivOk.visibility== View.VISIBLE){
                    showSumWuMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumLiuMoney!=null&&isTouchSumLiu&&showSumLiuMoney.llShowTop.visibility== View.VISIBLE){
                //6
                showSumLiuMoney.ivOff.getLocationOnScreen(locationOff)
                showSumLiuMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumLiuMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumLiuMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumLiuMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumLiuMoney.ivOk.height)
                if(isTouchOnOff&& showSumLiuMoney.ivOff.visibility== View.VISIBLE){
                    showSumLiuMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumLiuMoney.ivOk.visibility== View.VISIBLE){
                    showSumLiuMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumQiMoney!=null&&isTouchSumQi&&showSumQiMoney.llShowTop.visibility== View.VISIBLE){
                //7
                showSumQiMoney.ivOff.getLocationOnScreen(locationOff)
                showSumQiMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumQiMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumQiMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumQiMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumQiMoney.ivOk.height)
                if(isTouchOnOff&& showSumQiMoney.ivOff.visibility== View.VISIBLE){
                    showSumQiMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumQiMoney.ivOk.visibility== View.VISIBLE){
                    showSumQiMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumBaMoney!=null&&isTouchSumBa&&showSumBaMoney.llShowTop.visibility== View.VISIBLE){
                //8
                showSumBaMoney.ivOff.getLocationOnScreen(locationOff)
                showSumBaMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumBaMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumBaMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumBaMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumBaMoney.ivOk.height)
                if(isTouchOnOff&& showSumBaMoney.ivOff.visibility== View.VISIBLE){
                    showSumBaMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumBaMoney.ivOk.visibility== View.VISIBLE){
                    showSumBaMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumJiuMoney!=null&&isTouchSumJiu&&showSumJiuMoney.llShowTop.visibility== View.VISIBLE){
                //9
                showSumJiuMoney.ivOff.getLocationOnScreen(locationOff)
                showSumJiuMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumJiuMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumJiuMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumJiuMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumJiuMoney.ivOk.height)
                if(isTouchOnOff&& showSumJiuMoney.ivOff.visibility== View.VISIBLE){
                    showSumJiuMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumJiuMoney.ivOk.visibility== View.VISIBLE){
                    showSumJiuMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiMoney!=null&&isTouchSumShi&&showSumShiMoney.llShowTop.visibility== View.VISIBLE){
                //10
                showSumShiMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiYiMoney!=null&&isTouchSumShiYi&&showSumShiYiMoney.llShowTop.visibility== View.VISIBLE){
                //11
                showSumShiYiMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiYiMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiYiMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiYiMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiYiMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiYiMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiYiMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiYiMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiYiMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiYiMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiErMoney!=null&&isTouchSumShiEr&&showSumShiErMoney.llShowTop.visibility== View.VISIBLE){
                //12
                showSumShiErMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiErMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiErMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiErMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiErMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiErMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiErMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiErMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiErMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiErMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiSanMoney!=null&&isTouchSumShiSan&&showSumShiSanMoney.llShowTop.visibility== View.VISIBLE){
                //13
                showSumShiSanMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiSanMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiSanMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiSanMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiSanMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiSanMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiSanMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiSanMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiSanMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiSanMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiSiMoney!=null&&isTouchSumShiSi&&showSumShiSiMoney.llShowTop.visibility== View.VISIBLE){
                //14
                showSumShiSiMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiSiMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiSiMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiSiMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiSiMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiSiMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiSiMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiSiMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiSiMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiSiMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiWuMoney!=null&&isTouchSumShiWu&&showSumShiWuMoney.llShowTop.visibility== View.VISIBLE){
                //15
                showSumShiWuMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiWuMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiWuMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiWuMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiWuMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiWuMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiWuMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiWuMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiWuMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiWuMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiLiuMoney!=null&&isTouchSumShiLiu&&showSumShiLiuMoney.llShowTop.visibility== View.VISIBLE){
                //16
                showSumShiLiuMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiLiuMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiLiuMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiLiuMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiLiuMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiLiuMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiLiuMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiLiuMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiLiuMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiLiuMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }else  if(showSumShiQiMoney!=null&&isTouchSumShiQi&&showSumShiQiMoney.llShowTop.visibility== View.VISIBLE){
                //17
                showSumShiQiMoney.ivOff.getLocationOnScreen(locationOff)
                showSumShiQiMoney.ivOk.getLocationOnScreen(locationOk)
                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSumShiQiMoney.ivOff.width)
                        && y >= locationOff[1] && y <= (locationOff[1] + showSumShiQiMoney.ivOff.height)
                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSumShiQiMoney.ivOk.width)
                        && y >= locationOk[1] && y <= (locationOk[1] + showSumShiQiMoney.ivOk.height)
                if(isTouchOnOff&& showSumShiQiMoney.ivOff.visibility== View.VISIBLE){
                    showSumShiQiMoney.ivOff.performClick()
                    return@OnTouchListener true
                } else  if(isTouchOnOk&& showSumShiQiMoney.ivOk.visibility== View.VISIBLE){
                    showSumShiQiMoney.ivOk.performClick()
                    return@OnTouchListener true
                }else {
                    return@OnTouchListener false
                }

            }

        }




        return@OnTouchListener false
    })

}


//else  if(viewPager.currentItem==1) {//第二
//    when (event.action) {
//        MotionEvent.ACTION_DOWN -> {
//            var  showSingleYiMoney= singleDiceFragment.requireView().findViewWithTag<MoneyOKView>("showSingleYiMoney")
//            var  showSingleErMoney= singleDiceFragment.requireView().findViewWithTag<MoneyOKView>("showSingleErMoney")
//            var  showSingleSanMoney= singleDiceFragment.requireView().findViewWithTag<MoneyOKView>("showSingleSanMoney")
//            var  showSingleSiMoney= singleDiceFragment.requireView().findViewWithTag<MoneyOKView>("showSingleSiMoney")
//            var  showSingleWuMoney= singleDiceFragment.requireView().findViewWithTag<MoneyOKView>("showSingleWuMoney")
//            var  showSingleLiuMoney= singleDiceFragment.requireView().findViewWithTag<MoneyOKView>("showSingleLiuMoney")
//            //用于判断父类是否是在点击范围
//            var isTouchSingleYi:Boolean=false
//            var isTouchSingleEr:Boolean=false
//            var isTouchSingleSan:Boolean=false
//            var isTouchSingleSi:Boolean=false
//            var isTouchSingleWu:Boolean=false
//            var isTouchLiuMoney:Boolean=false
//
//            if(showSingleYiMoney!=null){
//                isTouchSingleYi = isShowTouch(showSingleYiMoney,x,y)
//            }
//            if(showSingleErMoney!=null){
//                isTouchSingleEr = isShowTouch(showSingleErMoney,x,y)
//            }
//            if(showSingleSanMoney!=null){
//                isTouchSingleSan = isShowTouch(showSingleSanMoney,x,y)
//            }
//            if(showSingleSiMoney!=null){
//                isTouchSingleSi = isShowTouch(showSingleSiMoney,x,y)
//            }
//            if(showSingleWuMoney!=null){
//                isTouchSingleWu = isShowTouch(showSingleWuMoney,x,y)
//            }
//            if(showSingleLiuMoney!=null){
//                isTouchLiuMoney = isShowTouch(showSingleLiuMoney,x,y)
//            }
//            //判断放进去的控件是否为空，并且判断是不是在范围，是否隐藏了
//            if(showSingleYiMoney!=null&&isTouchSingleYi&&showSingleYiMoney.llShowTop.visibility== View.VISIBLE){
//                //左上
//                showSingleYiMoney.ivOff.getLocationOnScreen(locationOff)
//                showSingleYiMoney.ivOk.getLocationOnScreen(locationOk)
//                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSingleYiMoney.ivOff.width)
//                        && y >= locationOff[1] && y <= (locationOff[1] + showSingleYiMoney.ivOff.height)
//                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSingleYiMoney.ivOk.width)
//                        && y >= locationOk[1] && y <= (locationOk[1] + showSingleYiMoney.ivOk.height)
//                if(isTouchOnOff&& showSingleYiMoney.ivOff.visibility== View.VISIBLE){
//                    showSingleYiMoney.ivOff.performClick()
//                    return@OnTouchListener true
//                } else  if(isTouchOnOk&& showSingleYiMoney.ivOk.visibility== View.VISIBLE){
//                    showSingleYiMoney.ivOk.performClick()
//                    return@OnTouchListener true
//                }else {
//                    return@OnTouchListener false
//                }
//
//            }else  if(showSingleErMoney!=null&&isTouchSingleEr&&showSingleErMoney.llShowTop.visibility== View.VISIBLE){
//
//                showSingleErMoney.ivOff.getLocationOnScreen(locationOff)
//                showSingleErMoney.ivOk.getLocationOnScreen(locationOk)
//                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSingleErMoney.ivOff.width)
//                        && y >= locationOff[1] && y <= (locationOff[1] + showSingleErMoney.ivOff.height)
//                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSingleErMoney.ivOk.width)
//                        && y >= locationOk[1] && y <= (locationOk[1] + showSingleErMoney.ivOk.height)
//                if(isTouchOnOff&& showSingleErMoney.ivOff.visibility== View.VISIBLE){
//                    showSingleErMoney.ivOff.performClick()
//                    return@OnTouchListener true
//                } else  if(isTouchOnOk&& showSingleErMoney.ivOk.visibility== View.VISIBLE){
//                    showSingleErMoney.ivOk.performClick()
//                    return@OnTouchListener true
//                }else {
//                    return@OnTouchListener false
//                }
//
//            }else  if(showSingleSanMoney!=null&&isTouchSingleSan&&showSingleSanMoney.llShowTop.visibility== View.VISIBLE){
//                //左上
//                showSingleSanMoney.ivOff.getLocationOnScreen(locationOff)
//                showSingleSanMoney.ivOk.getLocationOnScreen(locationOk)
//                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSingleSanMoney.ivOff.width)
//                        && y >= locationOff[1] && y <= (locationOff[1] + showSingleSanMoney.ivOff.height)
//                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSingleSanMoney.ivOk.width)
//                        && y >= locationOk[1] && y <= (locationOk[1] + showSingleSanMoney.ivOk.height)
//                if(isTouchOnOff&& showSingleSanMoney.ivOff.visibility== View.VISIBLE){
//                    showSingleSanMoney.ivOff.performClick()
//                    return@OnTouchListener true
//                } else  if(isTouchOnOk&& showSingleSanMoney.ivOk.visibility== View.VISIBLE){
//                    showSingleSanMoney.ivOk.performClick()
//                    return@OnTouchListener true
//                }else {
//                    return@OnTouchListener false
//                }
//
//            }else  if(showSingleSiMoney!=null&&isTouchSingleSi&&showSingleSiMoney.llShowTop.visibility== View.VISIBLE){
//
//                showSingleSiMoney.ivOff.getLocationOnScreen(locationOff)
//                showSingleSiMoney.ivOk.getLocationOnScreen(locationOk)
//                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSingleSiMoney.ivOff.width)
//                        && y >= locationOff[1] && y <= (locationOff[1] + showSingleSiMoney.ivOff.height)
//                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSingleSiMoney.ivOk.width)
//                        && y >= locationOk[1] && y <= (locationOk[1] + showSingleSiMoney.ivOk.height)
//                if(isTouchOnOff&& showSingleSiMoney.ivOff.visibility== View.VISIBLE){
//                    showSingleSiMoney.ivOff.performClick()
//                    return@OnTouchListener true
//                } else  if(isTouchOnOk&& showSingleSiMoney.ivOk.visibility== View.VISIBLE){
//                    showSingleSiMoney.ivOk.performClick()
//                    return@OnTouchListener true
//                }else {
//                    return@OnTouchListener false
//                }
//
//            }else if(showSingleWuMoney!=null&&isTouchSingleWu&&showSingleWuMoney.llShowTop.visibility== View.VISIBLE){
//
//                showSingleWuMoney.ivOff.getLocationOnScreen(locationOff)
//                showSingleWuMoney.ivOk.getLocationOnScreen(locationOk)
//                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSingleWuMoney.ivOff.width)
//                        && y >= locationOff[1] && y <= (locationOff[1] + showSingleWuMoney.ivOff.height)
//                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSingleWuMoney.ivOk.width)
//                        && y >= locationOk[1] && y <= (locationOk[1] + showSingleWuMoney.ivOk.height)
//                if(isTouchOnOff&& showSingleWuMoney.ivOff.visibility== View.VISIBLE){
//                    showSingleWuMoney.ivOff.performClick()
//                    return@OnTouchListener true
//                } else  if(isTouchOnOk&& showSingleWuMoney.ivOk.visibility== View.VISIBLE){
//                    showSingleWuMoney.ivOk.performClick()
//                    return@OnTouchListener true
//                }else {
//                    return@OnTouchListener false
//                }
//
//            }else if(showSingleLiuMoney!=null&&isTouchLiuMoney&&showSingleLiuMoney.llShowTop.visibility== View.VISIBLE){
//
//                showSingleLiuMoney.ivOff.getLocationOnScreen(locationOff)
//                showSingleLiuMoney.ivOk.getLocationOnScreen(locationOk)
//                val isTouchOnOff = x >= locationOff[0] && x <= (locationOff[0] + showSingleLiuMoney.ivOff.width)
//                        && y >= locationOff[1] && y <= (locationOff[1] + showSingleLiuMoney.ivOff.height)
//                val isTouchOnOk = x >= locationOk[0] && x <= (locationOk[0] + showSingleLiuMoney.ivOk.width)
//                        && y >= locationOk[1] && y <= (locationOk[1] + showSingleLiuMoney.ivOk.height)
//                if(isTouchOnOff&& showSingleLiuMoney.ivOff.visibility== View.VISIBLE){
//                    showSingleLiuMoney.ivOff.performClick()
//                    return@OnTouchListener true
//                } else  if(isTouchOnOk&& showSingleLiuMoney.ivOk.visibility== View.VISIBLE){
//                    showSingleLiuMoney.ivOk.performClick()
//                    return@OnTouchListener true
//                }else {
//                    return@OnTouchListener false
//                }
//
//            }
//
//
//
//        }
//
//    }
//
//
//
//}


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



fun Int.addCommas(): String {
    val numberString = this.toString()
    val length = numberString.length
    val stringBuilder = StringBuilder()

    for (i in 0 until length) {
        stringBuilder.append(numberString[i])
        val remainingDigits = length - i - 1
        if (remainingDigits > 0 && remainingDigits % 3 == 0) {
            stringBuilder.append(',')
        }
    }

    return stringBuilder.toString()
}


