package com.cn.game.sdk2.ui.fast3

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.data.enums.NOTES_ENUM
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.Fast3ViewModel
import com.cn.game.sdk2.utils.tool.PromptSoundPlay


/**
 * 默认
 */
class DXDSFragment2(private val delegate:Fast3ViewModel) : BaseGameFragment<Fast3ViewModel, FragDxdsBinding>() {
    private var animators: MutableList<ObjectAnimator> = mutableListOf()

    private var areaViewList:MutableList<GameAreaView> = mutableListOf();

    override fun createViewModel(): Fast3ViewModel {
        return delegate
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.bigView.areaCode = NOTES_ENUM.QTDefaultBig.num
        mDatabind.smallView.areaCode = NOTES_ENUM.QTDefaultSmall.num
        mDatabind.singleView.areaCode = NOTES_ENUM.QTDefaultSingle.num
        mDatabind.doubleView.areaCode = NOTES_ENUM.QTDefaultDouble.num
        mDatabind.leopardView.areaCode = NOTES_ENUM.QTDefaultTriple.num
        areaViewList.add(mDatabind.bigView)
        areaViewList.add(mDatabind.smallView)
        areaViewList.add(mDatabind.singleView)
        areaViewList.add(mDatabind.doubleView)
        areaViewList.add(mDatabind.leopardView)

        for(areaView in areaViewList){
            areaView.moneyView.setMoneyOKClickListener(object : MoneyOKView.OnMoneyOKClickListener{
                override fun onConfirm() {
                    mViewModel.betOkClick()
                }

                override fun onDelete() {
                    mViewModel.betDeleteClick()
                }
            })
            areaView.setOnClickListener{
                //先判断余额是否够这次 并且扣取钱
                //if( homeXPopupDialog.isCanBetting()&&MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                if(mViewModel.isClickOperation.value == true && PromptSoundPlay.handleClick()){}
            }
        }

        /**
         * 左下点击  第一步
         */
      /*  mDatabind.singleView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //先判断余额是否够这次
                    if( homeXPopupDialog.isCanBetting()&&MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                        // 获取点击位置的坐标控件位置
                        val x = event.x
                        val y = event.y
                        //屏幕的坐标
                        val rax= event.rawX
                        val ray= event.rawY
                        // 获取 View 的边界
                        val left = v.left.toFloat()
                        val top = v.top.toFloat()
                        val right = v.right.toFloat()
                        val bottom = v.bottom.toFloat()
                        // 定义边缘阈值，可根据实际情况调整
                        val edgeThreshold = requireContext().dp2px(20) // 像素
                        val rihtThreshold = requireContext().dp2px(20) // 像素
                        // 判断点击位置是否在 View 的上下左右边缘
                        val isOnLeftEdge = x <= left + edgeThreshold
                        val isOnTopEdge = y <= top + edgeThreshold
                        val isOnRightEdge = x >= right - rihtThreshold
                        val isOnBottomEdge = y >= bottom - rihtThreshold

                        // 处理点击在边缘的逻辑
                        if (isOnLeftEdge || isOnTopEdge || isOnRightEdge || isOnBottomEdge) {

                            handleEdgeClick(isOnLeftEdge, isOnTopEdge, isOnRightEdge, isOnBottomEdge)
                        } else {

                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeDefault.leftBelow.viewXYTemporary [0]==0&& ComputeDefault.leftBelow.viewXYTemporary[1]==0){
                                ComputeDefault.leftBelow.viewXYTemporary[0]=x.toInt()
                                ComputeDefault.leftBelow.viewXYTemporary[1]=y.toInt()
                            }

                            var selectNum=0
                            for (i in 0 until   MyGameManager.noteList.size) {
                                if(MyGameManager.noteList[i].select){
                                    selectNum=i
                                    break
                                }
                            }
                            //计算钱
                            ComputeDefault.leftBelow.moneyTemporary= ComputeDefault.leftBelow.moneyTemporary+MyGameManager.noteList[selectNum].money
                            showLeftBelowMoney.setShowMoney(ComputeDefault.leftBelow.moneyTemporary+ ComputeDefault.leftBelow.moneyOkEmpty)
                            //判断是否添加上去了这个viwe
                            if (mDatabind.rlHomeRoot.indexOfChild(showLeftBelowMoney) != -1) {

                                val location = IntArray(2)
                                showLeftBelowMoney.getLocationOnScreen(location)
                                val xOnScreen = location[0]
                                val yOnScreen = location[1]
                                //通过显示的控件得到相对于屏幕的位置
                                var  rax=xOnScreen
                                var ray=yOnScreen+requireContext().dp2px(52)

                                homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),  animationView=showLeftBelowMoney.ivShowBg)
                            } else {

                                val viewTreeObserver = showLeftBelowMoney.viewTreeObserver
                                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        // 确保只监听一次
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            showLeftBelowMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                        } else {
                                            showLeftBelowMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        }
                                        // 获取视图在屏幕上的绝对位置
                                        val location = IntArray(2)
                                        showLeftBelowMoney.getLocationOnScreen(location)
                                        val xOnScreen = location[0]
                                        val yOnScreen = location[1]
                                        //通过显示的控件得到相对于屏幕的位置
                                        var  rax=xOnScreen
                                        var ray=yOnScreen+requireContext().dp2px(52)

                                        homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView=showLeftBelowMoney.ivShowBg)
                                    }
                                })


                                // 动态添加的视图未成功添加到布局中
                                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                mDatabind.rlHomeRoot.addView(showLeftBelowMoney, params)
                                showLeftBelowMoney.translationX =  ComputeDefault.leftBelow.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
                                showLeftBelowMoney.translationY =  ComputeDefault.leftBelow.viewXYTemporary[1].toFloat()+requireContext().dp2px(35)

                            }
//                            (context as GameHomeActivity).startAnimation(ComputeDefault.leftBelow.screenXYTemporary[0].toFloat(),ComputeDefault.leftBelow.screenXYTemporary[1].toFloat(),true)

                            //当前点击的这个注区头部显示出来
                            showLeftBelowMoney.showTop()
                            //点击每个模块的动画，隐藏没点击的所有的头部
                            clickAnimationIsHidden(2)

                        }

                    }


                }


            }
            false // 返回 true 表示事件已经被处理
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animators.forEach { it.cancel() }
        animators.clear()
    }
}
