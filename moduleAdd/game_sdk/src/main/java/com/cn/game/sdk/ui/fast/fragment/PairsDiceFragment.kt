package com.cn.game.sdk.ui.fast.fragment

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.bean.ComputePairs
import com.cn.game.sdk.bean.ComputeSingle
import com.cn.game.sdk.bean.InPrizeBean
import com.cn.game.sdk.databinding.FragmentPairsDiceBinding
import com.cn.game.sdk.enums.NOTES_ENUM
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKDeleteView
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * 对子
 */
class PairsDiceFragment  : BaseGameFragment<PairsDiceVm, FragmentPairsDiceBinding>() {
    /**
     * 第一个
     */
    lateinit var showPairsYiMoney: MoneyOKDeleteView

    /**
     * 第二个
     */
    lateinit var showPairsErMoney: MoneyOKDeleteView
    /**
     * 第三个
     */
    lateinit var showPairsSanMoney: MoneyOKDeleteView
    /**
     * 第四个
     */
    lateinit var showPairsSiMoney: MoneyOKDeleteView
    /**
     * 第五个
     */
    lateinit var showPairsWuMoney: MoneyOKDeleteView

    /**
     * 第六个
     */
    lateinit var showPairsLiuMoney: MoneyOKDeleteView


    private var animators: MutableList<ObjectAnimator> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        showPairsYiMoney=MoneyOKDeleteView(requireContext())
        showPairsYiMoney.tag = "showPairsYiMoney"
        showPairsYiMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvPairsClickOne.indexOfChild(showPairsYiMoney) != -1) {
                        mDatabind.rvPairsClickOne.removeView(showPairsYiMoney)
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


        showPairsErMoney=MoneyOKDeleteView(requireContext())
        showPairsErMoney.tag = "showPairsErMoney"
        showPairsErMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvPairsClickTwo.indexOfChild(showPairsErMoney) != -1) {
                        mDatabind.rvPairsClickTwo.removeView(showPairsErMoney)
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
        showPairsSanMoney=MoneyOKDeleteView(requireContext())
        showPairsSanMoney.tag = "showPairsSanMoney"
        showPairsSanMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvPairsClickThree.indexOfChild(showPairsSanMoney) != -1) {
                        mDatabind.rvPairsClickThree.removeView(showPairsSanMoney)
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
        showPairsSiMoney=MoneyOKDeleteView(requireContext())
        showPairsSiMoney.tag = "showPairsSiMoney"
        showPairsSiMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvPairsClickFour.indexOfChild(showPairsSiMoney) != -1) {
                        mDatabind.rvPairsClickFour.removeView(showPairsSiMoney)
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
        showPairsWuMoney=MoneyOKDeleteView(requireContext())
        showPairsWuMoney.tag = "showSingleYiMoney"
        showPairsWuMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvPairsClickFive.indexOfChild(showPairsWuMoney) != -1) {
                        mDatabind.rvPairsClickFive.removeView(showPairsWuMoney)
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
        showPairsLiuMoney=MoneyOKDeleteView(requireContext())
        showPairsLiuMoney.tag = "showPairsLiuMoney"
        showPairsLiuMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvPairsClickSix.indexOfChild(showPairsLiuMoney) != -1) {
                        mDatabind.rvPairsClickSix.removeView(showPairsLiuMoney)
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
        mDatabind.rvPairsClickOne. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&& MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvPairsClickOne.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(1)
                showPairsYiMoney.showTop()
                //计算钱
                ComputePairs.pairsYi .moneyTemporary= ComputePairs.pairsYi.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showPairsYiMoney.setShowMoney(ComputePairs.pairsYi.moneyTemporary+ ComputePairs.pairsYi.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvPairsClickOne.indexOfChild(showPairsYiMoney) != -1) {
                    val location = IntArray(2)
                    showPairsYiMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showPairsYiMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showPairsYiMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showPairsYiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showPairsYiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showPairsYiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showPairsYiMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputePairs.pairsYi.viewXYTemporary[0]==0&& ComputePairs.pairsYi.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showPairsYiMoney.getLocationInWindow(location)

                                ComputePairs.pairsYi.viewXYTemporary[0]= showPairsYiMoney.left
                                ComputePairs.pairsYi.viewXYTemporary[1]=showPairsYiMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvPairsClickOne.addView(showPairsYiMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showPairsYiMoney.layoutParams = params
                }

            }



        }

        //点击二
        mDatabind.rvPairsClickTwo. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&& MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvPairsClickTwo.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showPairsErMoney.showTop()
                //计算钱
                ComputePairs.pairsEr .moneyTemporary= ComputePairs.pairsEr.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showPairsErMoney.setShowMoney(ComputePairs.pairsEr.moneyTemporary+ ComputePairs.pairsEr.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvPairsClickTwo.indexOfChild(showPairsErMoney) != -1) {
                    val location = IntArray(2)
                    showPairsErMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showPairsErMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showPairsErMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showPairsErMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showPairsErMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showPairsErMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showPairsErMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputePairs.pairsEr.viewXYTemporary[0]==0&& ComputePairs.pairsEr.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showPairsErMoney.getLocationInWindow(location)

                                ComputePairs.pairsEr.viewXYTemporary[0]= showPairsErMoney.left
                                ComputePairs.pairsEr.viewXYTemporary[1]=showPairsErMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvPairsClickTwo.addView(showPairsErMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showPairsErMoney.layoutParams = params
                }

            }



        }

        //点击三
        mDatabind.rvPairsClickThree. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&& MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvPairsClickThree.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(3)
                showPairsSanMoney.showTop()
                //计算钱
                ComputePairs.pairsSan .moneyTemporary= ComputePairs.pairsSan.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showPairsSanMoney.setShowMoney(ComputePairs.pairsSan.moneyTemporary+ ComputePairs.pairsSan.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvPairsClickThree.indexOfChild(showPairsSanMoney) != -1) {
                    val location = IntArray(2)
                    showPairsSanMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showPairsSanMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showPairsSanMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showPairsSanMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showPairsSanMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showPairsSanMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showPairsSanMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputePairs.pairsSan.viewXYTemporary[0]==0&& ComputePairs.pairsSan.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showPairsSanMoney.getLocationInWindow(location)

                                ComputePairs.pairsSan.viewXYTemporary[0]= showPairsSanMoney.left
                                ComputePairs.pairsSan.viewXYTemporary[1]=showPairsSanMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvPairsClickThree.addView(showPairsSanMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showPairsErMoney.layoutParams = params
                }

            }



        }

        //点击四
        mDatabind.rvPairsClickFour. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&& MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvPairsClickFour.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(4)
                showPairsSiMoney.showTop()
                //计算钱
                ComputePairs.pairsSi .moneyTemporary= ComputePairs.pairsSi.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showPairsSiMoney.setShowMoney(ComputePairs.pairsSi.moneyTemporary+ ComputePairs.pairsSi.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvPairsClickFour.indexOfChild(showPairsSiMoney) != -1) {
                    val location = IntArray(2)
                    showPairsSiMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showPairsSiMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showPairsSiMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showPairsSiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showPairsSiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showPairsSiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showPairsSiMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputePairs.pairsSi.viewXYTemporary[0]==0&& ComputePairs.pairsSi.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showPairsSiMoney.getLocationInWindow(location)

                                ComputePairs.pairsSi.viewXYTemporary[0]= showPairsSiMoney.left
                                ComputePairs.pairsSi.viewXYTemporary[1]=showPairsSiMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvPairsClickFour.addView(showPairsSiMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showPairsErMoney.layoutParams = params
                }

            }



        }

        //点击五
        mDatabind.rvPairsClickFive. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&& MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvPairsClickFive.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(5)
                showPairsWuMoney.showTop()
                //计算钱
                ComputePairs.pairsWu .moneyTemporary= ComputePairs.pairsWu.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showPairsWuMoney.setShowMoney(ComputePairs.pairsWu.moneyTemporary+ ComputePairs.pairsWu.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvPairsClickFive.indexOfChild(showPairsWuMoney) != -1) {
                    val location = IntArray(2)
                    showPairsWuMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showPairsWuMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showPairsWuMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showPairsWuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showPairsWuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showPairsWuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showPairsWuMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputePairs.pairsWu.viewXYTemporary[0]==0&& ComputePairs.pairsWu.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showPairsWuMoney.getLocationInWindow(location)

                                ComputePairs.pairsWu.viewXYTemporary[0]= showPairsWuMoney.left
                                ComputePairs.pairsWu.viewXYTemporary[1]=showPairsWuMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvPairsClickFive.addView(showPairsWuMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showPairsErMoney.layoutParams = params
                }

            }



        }
        //点击6
        mDatabind.rvPairsClickSix. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( (context as GameHomeActivity).isCanBetting()&& MyGameManager.isClickOperation){
                val location = IntArray(2)
                mDatabind.rvPairsClickSix.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(5)
                showPairsLiuMoney.showTop()
                //计算钱
                ComputePairs.pairsLiu .moneyTemporary= ComputePairs.pairsLiu.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showPairsLiuMoney.setShowMoney(ComputePairs.pairsLiu.moneyTemporary+ ComputePairs.pairsLiu.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvPairsClickSix.indexOfChild(showPairsLiuMoney) != -1) {
                    val location = IntArray(2)
                    showPairsLiuMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(), animationView =showPairsLiuMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showPairsLiuMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showPairsLiuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showPairsLiuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showPairsLiuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            (context as GameHomeActivity).startAnimation(rax.toFloat(),ray.toFloat(),animationView =showPairsLiuMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputePairs.pairsLiu.viewXYTemporary[0]==0&& ComputePairs.pairsLiu.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showPairsLiuMoney.getLocationInWindow(location)

                                ComputePairs.pairsLiu.viewXYTemporary[0]= showPairsLiuMoney.left
                                ComputePairs.pairsLiu.viewXYTemporary[1]=showPairsLiuMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvPairsClickSix.addView(showPairsLiuMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showPairsErMoney.layoutParams = params
                }

            }



        }
    }


    /**
     * 删除投注 1111 就保留上一次的确定的钱,isEmpty是否清空临时的钱，如果是点击的叉叉就要清空~~如果是勾勾就不用清除
     */
    fun deleteBet(isEmpty:Boolean=true){
        //删除临时钱
        if(isEmpty){
            ComputePairs.pairsYi.moneyTemporary=0
            ComputePairs.pairsEr.moneyTemporary=0
            ComputePairs.pairsSan.moneyTemporary=0
            ComputePairs.pairsSi.moneyTemporary=0
            ComputePairs.pairsWu.moneyTemporary=0
            ComputePairs.pairsLiu.moneyTemporary=0
        }
        //删除没有确定钱的坐标
        if(ComputePairs.pairsYi.moneyOkEmpty<=0){
            ComputePairs.pairsYi.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputePairs.pairsEr.moneyOkEmpty<=0){
            ComputePairs.pairsEr.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputePairs.pairsSan.moneyOkEmpty<=0){
            ComputePairs.pairsSan.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputePairs.pairsSi.moneyOkEmpty<=0){
            ComputePairs.pairsSi.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputePairs.pairsWu.moneyOkEmpty<=0){
            ComputePairs.pairsWu.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputePairs.pairsLiu.moneyOkEmpty<=0){
            ComputePairs.pairsLiu.viewXYTemporary=intArrayOf(0, 0)
        }

        //
        if(ComputePairs.pairsYi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvPairsClickOne.indexOfChild(showPairsYiMoney) != -1) {
                mDatabind.rvPairsClickOne.removeView(showPairsYiMoney)
            }
        }else{
            showPairsYiMoney.hiddenTop()
        }

        if(ComputePairs.pairsEr.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvPairsClickTwo.indexOfChild(showPairsErMoney) != -1) {
                mDatabind.rvPairsClickTwo.removeView(showPairsErMoney)
            }
        }else{
            showPairsErMoney.hiddenTop()
        }

        if(ComputePairs.pairsSan.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvPairsClickThree.indexOfChild(showPairsSanMoney) != -1) {
                mDatabind.rvPairsClickThree.removeView(showPairsSanMoney)
            }
        }else{
            showPairsSanMoney.hiddenTop()
        }


        if(ComputePairs.pairsSi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvPairsClickFour.indexOfChild(showPairsSiMoney) != -1) {
                mDatabind.rvPairsClickFour.removeView(showPairsSiMoney)
            }
        }else{
            showPairsSiMoney.hiddenTop()
        }


        if(ComputePairs.pairsWu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvPairsClickFive.indexOfChild(showPairsWuMoney) != -1) {
                mDatabind.rvPairsClickFive.removeView(showPairsWuMoney)
            }
        }else{
            showPairsWuMoney.hiddenTop()
        }

        if(ComputePairs.pairsLiu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvPairsClickSix.indexOfChild(showPairsLiuMoney) != -1) {
                mDatabind.rvPairsClickSix.removeView(showPairsLiuMoney)
            }
        }else{
            showPairsLiuMoney.hiddenTop()
        }

    }

    /**
     * 从新设置一下每个注区显示投注的钱
     */
    fun setAllShowViewMoney(){
        showPairsYiMoney.setShowMoney(ComputePairs.pairsYi.moneyOkEmpty)
        showPairsErMoney.setShowMoney(ComputePairs.pairsEr.moneyOkEmpty)
        showPairsSanMoney.setShowMoney(ComputePairs.pairsSan.moneyOkEmpty)
        showPairsSiMoney.setShowMoney(ComputePairs.pairsSi.moneyOkEmpty)
        showPairsWuMoney.setShowMoney(ComputePairs.pairsWu.moneyOkEmpty)
        showPairsLiuMoney.setShowMoney(ComputePairs.pairsLiu.moneyOkEmpty)
    }

    /**
     * inPrizeList   中奖的区域
     * pressureIn    压中的区域
     */
    fun flicker(inPrizeList:ArrayList<InPrizeBean>, pressureIn:ArrayList<InPrizeBean>){
        closeBetting()
        //中奖区域
        var inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTDouble3.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTDouble4.num
        inPrizeList.add(inPrizeBean)
        fadeOut(true,inPrizeList)
        //压中
        for (i in 0 until  inPrizeList.size) {
            if(inPrizeList[i].inPrizType== NOTES_ENUM.QTDouble1.num&& ComputePairs.pairsYi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTDouble1.num
                pressure.money=(ComputePairs.pairsYi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTDouble2.num&& ComputePairs.pairsEr .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTDouble2.num
                pressure.money=(ComputePairs.pairsEr.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTDouble3.num&& ComputePairs.pairsSan .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTDouble3.num
                pressure.money=(ComputePairs.pairsSan.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTDouble4.num&& ComputePairs.pairsSi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTDouble4.num
                pressure.money=(ComputePairs.pairsSi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTDouble5.num&& ComputePairs.pairsWu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTDouble5.num
                pressure.money=(ComputePairs.pairsWu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTDouble6.num&& ComputePairs.pairsLiu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTDouble6.num
                pressure.money=(ComputePairs.pairsLiu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }

        }

        //显示数据
        for (i in 0 until  pressureIn.size) {
            if(pressureIn[i].inPrizType== NOTES_ENUM.QTDouble1.num){
                //计算钱
                showPairsYiMoney.setShowMoney(pressureIn[i].money)
                showPairsYiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvPairsClickOne.addView(showPairsYiMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showPairsYiMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTDouble2.num){
                //计算钱
                showPairsErMoney.setShowMoney(pressureIn[i].money)
                showPairsErMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvPairsClickTwo.addView(showPairsErMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showPairsErMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTDouble3.num){
                //计算钱
                showPairsSanMoney.setShowMoney(pressureIn[i].money)
                showPairsSanMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvPairsClickThree.addView(showPairsSanMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showPairsSanMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTDouble4.num){
                //计算钱
                showPairsSiMoney.setShowMoney(pressureIn[i].money)
                showPairsSiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvPairsClickFour.addView(showPairsSiMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showPairsSiMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTDouble5.num){
                //计算钱
                showPairsWuMoney.setShowMoney(pressureIn[i].money)
                showPairsWuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvPairsClickFive.addView(showPairsWuMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showPairsWuMoney.layoutParams = params
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTDouble6.num){
                //计算钱
                showPairsLiuMoney.setShowMoney(pressureIn[i].money)
                showPairsLiuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvPairsClickSix.addView(showPairsLiuMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showPairsLiuMoney.layoutParams = params
            }


            deletePreviousRound()


        }

    }


    /**
     * 删除上一轮的数据
     */
    fun  deletePreviousRound(){
        ComputePairs.pairsYi.viewXYTemporary= intArrayOf(0, 0)
        ComputePairs.pairsYi.viewXYLast= intArrayOf(0, 0)
        ComputePairs.pairsYi.moneyTemporary= 0
        ComputePairs.pairsYi.moneyOkEmpty= 0

        ComputePairs.pairsEr.viewXYTemporary= intArrayOf(0, 0)
        ComputePairs.pairsEr.viewXYLast= intArrayOf(0, 0)
        ComputePairs.pairsEr.moneyTemporary= 0
        ComputePairs.pairsEr.moneyOkEmpty= 0

        ComputePairs.pairsSan.viewXYTemporary= intArrayOf(0, 0)
        ComputePairs.pairsSan.viewXYLast= intArrayOf(0, 0)
        ComputePairs.pairsSan.moneyTemporary= 0
        ComputePairs.pairsSan.moneyOkEmpty= 0

        ComputePairs.pairsSi.viewXYTemporary= intArrayOf(0, 0)
        ComputePairs.pairsSi.viewXYLast= intArrayOf(0, 0)
        ComputePairs.pairsSi.moneyTemporary= 0
        ComputePairs.pairsSi.moneyOkEmpty= 0

        ComputePairs.pairsWu.viewXYTemporary= intArrayOf(0, 0)
        ComputePairs.pairsWu.viewXYLast= intArrayOf(0, 0)
        ComputePairs.pairsWu.moneyTemporary= 0
        ComputePairs.pairsWu.moneyOkEmpty= 0

        ComputePairs.pairsLiu.viewXYTemporary= intArrayOf(0, 0)
        ComputePairs.pairsLiu.viewXYLast= intArrayOf(0, 0)
        ComputePairs.pairsLiu.moneyTemporary= 0
        ComputePairs.pairsLiu.moneyOkEmpty= 0


    }

    /**
     * 得到开奖信息后删除所有的在界面上的注码
     */
    fun closeBetting(){
        if (mDatabind.rvPairsClickOne.indexOfChild(showPairsYiMoney) != -1) {
            mDatabind.rvPairsClickOne.removeView(showPairsYiMoney)
        }
        if (mDatabind.rvPairsClickTwo.indexOfChild(showPairsErMoney) != -1) {
            mDatabind.rvPairsClickTwo.removeView(showPairsErMoney)
        }

        if (mDatabind.rvPairsClickThree.indexOfChild(showPairsSanMoney) != -1) {
            mDatabind.rvPairsClickThree.removeView(showPairsSanMoney)
        }

        if (mDatabind.rvPairsClickFour.indexOfChild(showPairsSiMoney) != -1) {
            mDatabind.rvPairsClickFour.removeView(showPairsSiMoney)
        }
        if (mDatabind.rvPairsClickFive.indexOfChild(showPairsWuMoney) != -1) {
            mDatabind.rvPairsClickFive.removeView(showPairsWuMoney)
        }
        if (mDatabind.rvPairsClickSix.indexOfChild(showPairsLiuMoney) != -1) {
            mDatabind.rvPairsClickSix.removeView(showPairsLiuMoney)
        }

    }

    /**
     * 中奖区域闪烁的动画
     *isAnimation true  是执行动画   flase是取消动画
     */
    fun fadeOut(isAnimation:Boolean,inPrizeList:ArrayList<InPrizeBean> =ArrayList<InPrizeBean>()){
        if(isAnimation){
            inPrizeList.forEach {
                if(it.inPrizType==NOTES_ENUM.QTDouble1.num){
                    mDatabind.ivPairsOne.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivPairsOne, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDouble2.num){
                    mDatabind.ivPairsTwo.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivPairsTwo, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDouble3.num){
                    mDatabind.ivPairsThree.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivPairsThree, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDouble4.num){
                    mDatabind.ivPairsFour.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivPairsFour, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDouble5.num){
                    mDatabind.ivPairsFive.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivPairsFive, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }else  if(it.inPrizType==NOTES_ENUM.QTDouble6.num){
                    mDatabind.ivPairsSix.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivPairsSix, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }
            }

            // 开始动画
            animators.forEach { it.start() }

        }else{
            mDatabind.ivPairsOne.visibility= View.GONE
            mDatabind.ivPairsTwo.visibility= View.GONE
            mDatabind.ivPairsThree.visibility= View.GONE
            mDatabind.ivPairsFour.visibility= View.GONE
            mDatabind.ivPairsFive.visibility= View.GONE
            mDatabind.ivPairsSix.visibility= View.GONE

            animators.forEach { it.cancel() }
            animators.clear()
        }


    }

    /**
     * 点击每个模块的动画，隐藏没点击的所有的头部
     * 1  2 3  4   5  6   0
     */
    private fun clickAnimationIsHidden(num:Int ){
        if(num!=1){
            showPairsYiMoney.hiddenTop()
        }
        if(num!=2){
            showPairsErMoney.hiddenTop()
        }

        if(num!=3){
            showPairsSanMoney.hiddenTop()
        }
        if(num!=4){
            showPairsSiMoney.hiddenTop()
        }
        if(num!=5){
            showPairsWuMoney.hiddenTop()
        }
        if(num!=6){
            showPairsLiuMoney.hiddenTop()
        }


    }


    /**
     * 退出页面的时候要清空这些数据
     */
    fun closeActivity(){
        animators.forEach { it.cancel() }
        animators.clear()
    }
}