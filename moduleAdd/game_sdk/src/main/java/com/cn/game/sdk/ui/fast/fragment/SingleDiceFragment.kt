package com.cn.game.sdk.ui.fast.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.bean.ComputeDefault
import com.cn.game.sdk.bean.ComputeSingle
import com.cn.game.sdk.bean.InPrizeBean
import com.cn.game.sdk.databinding.FragmentSingleDiceBinding
import com.cn.game.sdk.enums.NOTES_ENUM
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKDeleteView
import com.xcjh.base_lib.utils.dp2px
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * 单骰子
 */
class SingleDiceFragment : BaseGameFragment<SingleDiceVm, FragmentSingleDiceBinding>() {
    /**
     * 第一个
     */
    lateinit var showSingleYiMoney: MoneyOKDeleteView

    /**
     * 第二个
     */
    lateinit var showSingleErMoney: MoneyOKDeleteView
    /**
     * 第三个
     */
    lateinit var showSingleSanMoney: MoneyOKDeleteView
    /**
     * 第四个
     */
    lateinit var showSingleSiMoney: MoneyOKDeleteView
    /**
     * 第五个
     */
    lateinit var showSingleWuMoney: MoneyOKDeleteView

    /**
     * 第六个
     */
    lateinit var showSingleLiuMoney: MoneyOKDeleteView


    private var animators: MutableList<ObjectAnimator> = mutableListOf()


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {

        showSingleYiMoney=MoneyOKDeleteView(requireContext())
        showSingleYiMoney.tag = "showSingleYiMoney"
        showSingleYiMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvDiceClickOne.indexOfChild(showSingleYiMoney) != -1) {
                        mDatabind.rvDiceClickOne.removeView(showSingleYiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSingleErMoney=MoneyOKDeleteView(requireContext())
        showSingleErMoney.tag = "showSingleErMoney"
        showSingleErMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleEr.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvDiceClickTwo.indexOfChild(showSingleErMoney) != -1) {
                        mDatabind.rvDiceClickTwo.removeView(showSingleErMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })

        showSingleSanMoney=MoneyOKDeleteView(requireContext())
        showSingleSanMoney.tag = "showSingleSanMoney"
        showSingleSanMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleSan.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvDiceClickThree.indexOfChild(showSingleSanMoney) != -1) {
                        mDatabind.rvDiceClickThree.removeView(showSingleSanMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })
        showSingleSiMoney=MoneyOKDeleteView(requireContext())
        showSingleSiMoney.tag = "showSingleSiMoney"
        showSingleSiMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleSi.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvDiceClickFour.indexOfChild(showSingleSiMoney) != -1) {
                        mDatabind.rvDiceClickFour.removeView(showSingleSiMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })
        showSingleWuMoney=MoneyOKDeleteView(requireContext())
        showSingleWuMoney.tag = "showSingleWuMoney"
        showSingleWuMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleWu.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvDiceClickFive.indexOfChild(showSingleWuMoney) != -1) {
                        mDatabind.rvDiceClickFive.removeView(showSingleWuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })
        showSingleLiuMoney=MoneyOKDeleteView(requireContext())
        showSingleLiuMoney.tag = "showSingleLiuMoney"
        showSingleLiuMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleLiu.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvDiceClickSix.indexOfChild(showSingleLiuMoney) != -1) {
                        mDatabind.rvDiceClickSix.removeView(showSingleLiuMoney)
                    }
                }
                (context as GameHomeActivity).clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                (context as GameHomeActivity).clickOKBet()
            }

        })


     //点击一
     mDatabind.rvDiceClickOne. setOnClickListener {
         //先判断余额是否够这次 并且扣取钱
         if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
             val location = IntArray(2)
             mDatabind.rvDiceClickOne.getLocationOnScreen(location)
             var selectNum=0
             for (i in 0 until   MyGameManager.noteList.size) {
                 if(MyGameManager.noteList[i].select){
                     selectNum=i
                     break
                 }
             }
             clickAnimationIsHidden(1)
             showSingleYiMoney.showTop()
             //计算钱
             ComputeSingle.singleYi .moneyTemporary= ComputeSingle.singleYi.moneyTemporary+MyGameManager.noteList[selectNum].money
             showSingleYiMoney.setShowMoney(ComputeSingle.singleYi.moneyTemporary+ ComputeSingle.singleYi.moneyOkEmpty)

             //动画位置
             if (mDatabind.rvDiceClickOne.indexOfChild(showSingleYiMoney) != -1) {
                 val location = IntArray(2)
                 showSingleYiMoney.getLocationOnScreen(location)
                 val xOnScreen = location[0]
                 val yOnScreen = location[1]
                 //通过显示的控件得到相对于屏幕的位置
                 var  rax=xOnScreen
                 var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                 (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSingleYiMoney.ivShowBg )

             } else {
                 val viewTreeObserver = showSingleYiMoney.viewTreeObserver
                 viewTreeObserver.addOnGlobalLayoutListener(object :
                     ViewTreeObserver.OnGlobalLayoutListener {
                     override fun onGlobalLayout() {
                         // 确保只监听一次
                         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                             showSingleYiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                         } else {
                             showSingleYiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                         }

                         // 获取视图在屏幕上的绝对位置
                         val location = IntArray(2)
                         showSingleYiMoney.getLocationOnScreen(location)
                         val xOnScreen = location[0]
                         val yOnScreen = location[1]


                         //通过显示的控件得到相对于屏幕的位置
                         var  rax=xOnScreen
                         var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                         (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSingleYiMoney.ivShowBg)
                         //显示点击在Fragment的位置用于动画结束后显示
                         if(ComputeSingle.singleYi.viewXYTemporary[0]==0&& ComputeSingle.singleYi.viewXYTemporary[1]==0){
                             val location = IntArray(2)
                             showSingleYiMoney.getLocationInWindow(location)

                             ComputeSingle.singleYi.viewXYTemporary[0]= showSingleYiMoney.left
                             ComputeSingle.singleYi.viewXYTemporary[1]=showSingleYiMoney.top
                         }
                     }
                 })

                 val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                 mDatabind.rvDiceClickOne.addView(showSingleYiMoney, params)
                 // 将新按钮设置为居中
                 params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                 showSingleYiMoney.layoutParams = params
             }

         }



     }
     //点击二
     mDatabind.rvDiceClickTwo. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvDiceClickTwo.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showSingleErMoney.showTop()
                //计算钱
                ComputeSingle.singleEr .moneyTemporary= ComputeSingle.singleEr.moneyTemporary+MyGameManager.noteList[selectNum].money
                showSingleErMoney.setShowMoney(ComputeSingle.singleEr.moneyTemporary+ ComputeSingle.singleEr.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvDiceClickTwo.indexOfChild(showSingleErMoney) != -1) {
                    val location = IntArray(2)
                    showSingleErMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSingleErMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showSingleErMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showSingleErMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showSingleErMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showSingleErMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSingleErMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeSingle.singleEr.viewXYTemporary[0]==0&& ComputeSingle.singleEr.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showSingleErMoney.getLocationInWindow(location)

                                ComputeSingle.singleEr.viewXYTemporary[0]= showSingleErMoney.left
                                ComputeSingle.singleEr.viewXYTemporary[1]=showSingleErMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvDiceClickTwo.addView(showSingleErMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showSingleErMoney.layoutParams = params
                }

            }



        }

      //点击三
      mDatabind.rvDiceClickThree. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvDiceClickThree.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showSingleSanMoney.showTop()
                //计算钱
                ComputeSingle.singleSan .moneyTemporary= ComputeSingle.singleSan.moneyTemporary+MyGameManager.noteList[selectNum].money
                showSingleSanMoney.setShowMoney(ComputeSingle.singleSan.moneyTemporary+ ComputeSingle.singleSan.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvDiceClickThree.indexOfChild(showSingleSanMoney) != -1) {
                    val location = IntArray(2)
                    showSingleSanMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSingleSanMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showSingleSanMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showSingleSanMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showSingleSanMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showSingleSanMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSingleSanMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeSingle.singleSan.viewXYTemporary[0]==0&& ComputeSingle.singleSan.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showSingleSanMoney.getLocationInWindow(location)

                                ComputeSingle.singleSan.viewXYTemporary[0]= showSingleSanMoney.left
                                ComputeSingle.singleSan.viewXYTemporary[1]=showSingleSanMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvDiceClickThree.addView(showSingleSanMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showSingleSanMoney.layoutParams = params
                }

            }



        }

        //点击4
     mDatabind.rvDiceClickFour. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvDiceClickFour.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showSingleSiMoney.showTop()
                //计算钱
                ComputeSingle.singleSi .moneyTemporary= ComputeSingle.singleSi.moneyTemporary+MyGameManager.noteList[selectNum].money
                showSingleSiMoney.setShowMoney(ComputeSingle.singleSi.moneyTemporary+ ComputeSingle.singleSi.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvDiceClickFour.indexOfChild(showSingleSiMoney) != -1) {
                    val location = IntArray(2)
                    showSingleSiMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSingleSiMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showSingleSiMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showSingleSiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showSingleSiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showSingleSiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSingleSiMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeSingle.singleSi.viewXYTemporary[0]==0&& ComputeSingle.singleSi.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showSingleSiMoney.getLocationInWindow(location)

                                ComputeSingle.singleSi.viewXYTemporary[0]= showSingleSiMoney.left
                                ComputeSingle.singleSi.viewXYTemporary[1]=showSingleSiMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvDiceClickFour.addView(showSingleSiMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showSingleSiMoney.layoutParams = params
                }

            }



        }

        //点击5
       mDatabind.rvDiceClickFive. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvDiceClickFive.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showSingleWuMoney.showTop()
                //计算钱
                ComputeSingle.singleWu .moneyTemporary= ComputeSingle.singleWu.moneyTemporary+MyGameManager.noteList[selectNum].money
                showSingleWuMoney.setShowMoney(ComputeSingle.singleWu.moneyTemporary+ ComputeSingle.singleWu.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvDiceClickFive.indexOfChild(showSingleWuMoney) != -1) {
                    val location = IntArray(2)
                    showSingleWuMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSingleWuMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showSingleWuMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showSingleWuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showSingleWuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showSingleWuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSingleWuMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeSingle.singleWu.viewXYTemporary[0]==0&& ComputeSingle.singleWu.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showSingleWuMoney.getLocationInWindow(location)

                                ComputeSingle.singleWu.viewXYTemporary[0]= showSingleWuMoney.left
                                ComputeSingle.singleWu.viewXYTemporary[1]=showSingleWuMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvDiceClickFive.addView(showSingleWuMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showSingleWuMoney.layoutParams = params
                }

            }



        }

        //点击6
       mDatabind.rvDiceClickSix. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&&MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvDiceClickSix.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showSingleLiuMoney.showTop()
                //计算钱
                ComputeSingle.singleLiu .moneyTemporary= ComputeSingle.singleLiu.moneyTemporary+MyGameManager.noteList[selectNum].money
                showSingleLiuMoney.setShowMoney(ComputeSingle.singleLiu.moneyTemporary+ ComputeSingle.singleLiu.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvDiceClickSix.indexOfChild(showSingleLiuMoney) != -1) {
                    val location = IntArray(2)
                    showSingleLiuMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showSingleLiuMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showSingleLiuMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showSingleLiuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showSingleLiuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showSingleLiuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showSingleLiuMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeSingle.singleLiu.viewXYTemporary[0]==0&& ComputeSingle.singleLiu.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showSingleLiuMoney.getLocationInWindow(location)

                                ComputeSingle.singleLiu.viewXYTemporary[0]= showSingleLiuMoney.left
                                ComputeSingle.singleLiu.viewXYTemporary[1]=showSingleLiuMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvDiceClickSix.addView(showSingleLiuMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showSingleLiuMoney.layoutParams = params
                }

            }



        }
    }









    /**
     * 点击每个模块的动画，隐藏没点击的所有的头部
     * 1  2 3  4   5  6   0
     */
    private fun clickAnimationIsHidden(num:Int ){
        if(num!=1){
            showSingleYiMoney.hiddenTop()
        }
        if(num!=2){
            showSingleErMoney.hiddenTop()
        }

        if(num!=3){
            showSingleSanMoney.hiddenTop()
        }
        if(num!=4){
            showSingleSiMoney.hiddenTop()
        }
        if(num!=5){
            showSingleWuMoney.hiddenTop()
        }
        if(num!=6){
            showSingleLiuMoney.hiddenTop()
        }


    }



    /**
     * 删除投注 1111 就保留上一次的确定的钱,isEmpty是否清空临时的钱，如果是点击的叉叉就要清空~~如果是勾勾就不用清除
     */
    fun deleteBet(isEmpty:Boolean=true){
        //删除临时钱
        if(isEmpty){
            ComputeSingle.singleYi.moneyTemporary=0
            ComputeSingle.singleEr.moneyTemporary=0
            ComputeSingle.singleSan.moneyTemporary=0
            ComputeSingle.singleSi.moneyTemporary=0
            ComputeSingle.singleWu.moneyTemporary=0
            ComputeSingle.singleLiu.moneyTemporary=0
        }
        //删除没有确定钱的坐标
        if(ComputeSingle.singleYi.moneyOkEmpty<=0){
            ComputeSingle.singleYi.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeSingle.singleEr.moneyOkEmpty<=0){
            ComputeSingle.singleEr.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeSingle.singleSan.moneyOkEmpty<=0){
            ComputeSingle.singleSan.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeSingle.singleSi.moneyOkEmpty<=0){
            ComputeSingle.singleSi.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeSingle.singleWu.moneyOkEmpty<=0){
            ComputeSingle.singleWu.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeSingle.singleLiu.moneyOkEmpty<=0){
            ComputeSingle.singleLiu.viewXYTemporary=intArrayOf(0, 0)
        }

        //
        if(ComputeSingle.singleYi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvDiceClickOne.indexOfChild(showSingleYiMoney) != -1) {
                mDatabind.rvDiceClickOne.removeView(showSingleYiMoney)
            }
        }else{
            showSingleYiMoney.hiddenTop()
        }

        if(ComputeSingle.singleEr.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvDiceClickTwo.indexOfChild(showSingleErMoney) != -1) {
                mDatabind.rvDiceClickTwo.removeView(showSingleErMoney)
            }
        }else{
            showSingleErMoney.hiddenTop()
        }

        if(ComputeSingle.singleSan.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvDiceClickThree.indexOfChild(showSingleSanMoney) != -1) {
                mDatabind.rvDiceClickThree.removeView(showSingleSanMoney)
            }
        }else{
            showSingleSanMoney.hiddenTop()
        }


        if(ComputeSingle.singleSi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvDiceClickFour.indexOfChild(showSingleSiMoney) != -1) {
                mDatabind.rvDiceClickFour.removeView(showSingleSiMoney)
            }
        }else{
            showSingleSiMoney.hiddenTop()
        }


        if(ComputeSingle.singleWu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvDiceClickFive.indexOfChild(showSingleWuMoney) != -1) {
                mDatabind.rvDiceClickFive.removeView(showSingleWuMoney)
            }
        }else{
            showSingleWuMoney.hiddenTop()
        }

        if(ComputeSingle.singleLiu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvDiceClickSix.indexOfChild(showSingleLiuMoney) != -1) {
                mDatabind.rvDiceClickSix.removeView(showSingleLiuMoney)
            }
        }else{
            showSingleLiuMoney.hiddenTop()
        }

    }


    /**
     * 从新设置一下每个注区显示投注的钱
     */
    fun setAllShowViewMoney(){
        showSingleYiMoney.setShowMoney(ComputeSingle.singleYi.moneyOkEmpty)
        showSingleErMoney.setShowMoney(ComputeSingle.singleEr.moneyOkEmpty)
        showSingleSanMoney.setShowMoney(ComputeSingle.singleSan.moneyOkEmpty)
        showSingleSiMoney.setShowMoney(ComputeSingle.singleSi.moneyOkEmpty)
        showSingleWuMoney.setShowMoney(ComputeSingle.singleWu.moneyOkEmpty)
        showSingleLiuMoney.setShowMoney(ComputeSingle.singleLiu.moneyOkEmpty)
    }

    /**
     * inPrizeList   中奖的区域
     * pressureIn    压中的区域
     */
    fun flicker(inPrizeList:ArrayList<InPrizeBean>, pressureIn:ArrayList<InPrizeBean>){
        closeBetting()
        //中奖区域
        var inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTSingle2.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTSingle6.num
        inPrizeList.add(inPrizeBean)
        fadeOut(true,inPrizeList)
        //压中
        for (i in 0 until  inPrizeList.size) {
            if(inPrizeList[i].inPrizType== NOTES_ENUM.QTSingle1.num&& ComputeSingle.singleYi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTSingle1.num
                pressure.money=(ComputeSingle.singleYi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTSingle2.num&& ComputeSingle.singleEr .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTSingle2.num
                pressure.money=(ComputeSingle.singleEr.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTSingle3.num&& ComputeSingle.singleSan .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTSingle3.num
                pressure.money=(ComputeSingle.singleSan.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTSingle4.num&& ComputeSingle.singleSi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTSingle4.num
                pressure.money=(ComputeSingle.singleSi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTSingle5.num&& ComputeSingle.singleWu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTSingle5.num
                pressure.money=(ComputeSingle.singleWu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTSingle6.num&& ComputeSingle.singleLiu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTSingle6.num
                pressure.money=(ComputeSingle.singleLiu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }

        }

        //显示数据
        for (i in 0 until  pressureIn.size) {
            if(pressureIn[i].inPrizType== NOTES_ENUM.QTSingle1.num){
                //计算钱
                showSingleYiMoney.setShowMoney(pressureIn[i].money)
                showSingleYiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvDiceClickOne.addView(showSingleYiMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showSingleYiMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTSingle2.num){
                //计算钱
                showSingleErMoney.setShowMoney(pressureIn[i].money)
                showSingleErMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvDiceClickTwo.addView(showSingleErMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showSingleErMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTSingle3.num){
                //计算钱
                showSingleSanMoney.setShowMoney(pressureIn[i].money)
                showSingleSanMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvDiceClickThree.addView(showSingleSanMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showSingleSanMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTSingle4.num){
                //计算钱
                showSingleSiMoney.setShowMoney(pressureIn[i].money)
                showSingleSiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvDiceClickFour.addView(showSingleSiMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showSingleSiMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTSingle5.num){
                //计算钱
                showSingleWuMoney.setShowMoney(pressureIn[i].money)
                showSingleWuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvDiceClickFive.addView(showSingleWuMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showSingleWuMoney.layoutParams = params
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTSingle6.num){
                //计算钱
                showSingleLiuMoney.setShowMoney(pressureIn[i].money)
                showSingleLiuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvDiceClickSix.addView(showSingleLiuMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showSingleLiuMoney.layoutParams = params
            }


            deletePreviousRound()


        }

    }


    /**
     * 得到开奖信息后删除所有的在界面上的注码
     */
    fun closeBetting(){
        if (mDatabind.rvDiceClickOne.indexOfChild(showSingleYiMoney) != -1) {
            mDatabind.rvDiceClickOne.removeView(showSingleYiMoney)
        }
        if (mDatabind.rvDiceClickTwo.indexOfChild(showSingleErMoney) != -1) {
            mDatabind.rvDiceClickTwo.removeView(showSingleErMoney)
        }

        if (mDatabind.rvDiceClickThree.indexOfChild(showSingleSanMoney) != -1) {
            mDatabind.rvDiceClickThree.removeView(showSingleSanMoney)
        }

        if (mDatabind.rvDiceClickFour.indexOfChild(showSingleSiMoney) != -1) {
            mDatabind.rvDiceClickFour.removeView(showSingleSiMoney)
        }
        if (mDatabind.rvDiceClickFive.indexOfChild(showSingleWuMoney) != -1) {
            mDatabind.rvDiceClickFive.removeView(showSingleWuMoney)
        }
        if (mDatabind.rvDiceClickSix.indexOfChild(showSingleLiuMoney) != -1) {
            mDatabind.rvDiceClickSix.removeView(showSingleLiuMoney)
        }

    }


    /**
     * 中奖区域闪烁的动画
     *isAnimation true  是执行动画   flase是取消动画
     */
    fun fadeOut(isAnimation:Boolean,inPrizeList:ArrayList<InPrizeBean> =ArrayList<InPrizeBean>()){
        if(isAnimation){
            inPrizeList.forEach {
                if(it.inPrizType==NOTES_ENUM.QTSingle1.num){
                    mDatabind.ivSingleOne.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSingleOne, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTSingle2.num){
                    mDatabind.ivSingleTwo.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSingleTwo, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTSingle3.num){
                    mDatabind.ivSingleThree.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSingleThree, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTSingle4.num){
                    mDatabind.ivSingleFour.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSingleFour, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTSingle5.num){
                    mDatabind.ivSingleFive.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSingleFive, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }else  if(it.inPrizType==NOTES_ENUM.QTSingle6.num){
                    mDatabind.ivSingleSix.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivSingleSix, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }
            }

            // 开始动画
            animators.forEach { it.start() }

        }else{
            mDatabind.ivSingleOne.visibility= View.GONE
            mDatabind.ivSingleTwo.visibility= View.GONE
            mDatabind.ivSingleThree.visibility= View.GONE
            mDatabind.ivSingleFour.visibility= View.GONE
            mDatabind.ivSingleFive.visibility= View.GONE
            mDatabind.ivSingleSix.visibility= View.GONE

            animators.forEach { it.cancel() }
            animators.clear()
        }


    }

    /**
     * 删除上一轮的数据
     */
    fun  deletePreviousRound(){
        ComputeSingle.singleYi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSingle.singleYi.viewXYLast= intArrayOf(0, 0)
        ComputeSingle.singleYi.moneyTemporary= 0
        ComputeSingle.singleYi.moneyOkEmpty= 0

        ComputeSingle.singleEr.viewXYTemporary= intArrayOf(0, 0)
        ComputeSingle.singleEr.viewXYLast= intArrayOf(0, 0)
        ComputeSingle.singleEr.moneyTemporary= 0
        ComputeSingle.singleEr.moneyOkEmpty= 0

        ComputeSingle.singleSan.viewXYTemporary= intArrayOf(0, 0)
        ComputeSingle.singleSan.viewXYLast= intArrayOf(0, 0)
        ComputeSingle.singleSan.moneyTemporary= 0
        ComputeSingle.singleSan.moneyOkEmpty= 0

        ComputeSingle.singleSi.viewXYTemporary= intArrayOf(0, 0)
        ComputeSingle.singleSi.viewXYLast= intArrayOf(0, 0)
        ComputeSingle.singleSi.moneyTemporary= 0
        ComputeSingle.singleSi.moneyOkEmpty= 0

        ComputeSingle.singleWu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSingle.singleWu.viewXYLast= intArrayOf(0, 0)
        ComputeSingle.singleWu.moneyTemporary= 0
        ComputeSingle.singleWu.moneyOkEmpty= 0

        ComputeSingle.singleLiu.viewXYTemporary= intArrayOf(0, 0)
        ComputeSingle.singleLiu.viewXYLast= intArrayOf(0, 0)
        ComputeSingle.singleLiu.moneyTemporary= 0
        ComputeSingle.singleLiu.moneyOkEmpty= 0


    }


    /**
     * 退出页面的时候要清空这些数据
     */
    fun closeActivity(){
        animators.forEach { it.cancel() }
        animators.clear()
    }

}