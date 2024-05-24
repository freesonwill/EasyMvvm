package com.cn.game.sdk.ui.fast.fragment

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.bean.ComputeLeopard
import com.cn.game.sdk.bean.ComputePairs
import com.cn.game.sdk.bean.ComputeSingle
import com.cn.game.sdk.bean.InPrizeBean
import com.cn.game.sdk.databinding.FragmentLeopardBinding
import com.cn.game.sdk.enums.NOTES_ENUM
import com.cn.game.sdk.tool.HomeXPopupDialog
import com.cn.game.sdk.tool.PromptSoundPlay
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKDeleteView
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * 豹子选择界面
 */
class LeopardFragment : BaseGameFragment<LeopardVm, FragmentLeopardBinding>() {
    /**
     * 第一个
     */
    lateinit var showLeopardYiMoney: MoneyOKDeleteView

    /**
     * 第二个
     */
    lateinit var showLeopardErMoney: MoneyOKDeleteView
    /**
     * 第三个
     */
    lateinit var showLeopardSanMoney: MoneyOKDeleteView
    /**
     * 第四个
     */
    lateinit var showLeopardSiMoney: MoneyOKDeleteView
    /**
     * 第五个
     */
    lateinit var showLeopardWuMoney: MoneyOKDeleteView

    /**
     * 第六个
     */
    lateinit var showLeopardLiuMoney: MoneyOKDeleteView

    private var animators: MutableList<ObjectAnimator> = mutableListOf()

    lateinit var  homeXPopupDialog: HomeXPopupDialog

    fun   setHomeXPopupDialogDate(homeXPopupDialog: HomeXPopupDialog){
        this.homeXPopupDialog=homeXPopupDialog
    }


    override fun initView(savedInstanceState: Bundle?) {
        showLeopardYiMoney=MoneyOKDeleteView(requireContext())
        showLeopardYiMoney.tag = "showLeopardYiMoney"
        showLeopardYiMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvLeopardClickOne.indexOfChild(showLeopardYiMoney) != -1) {
                        mDatabind.rvLeopardClickOne.removeView(showLeopardYiMoney)
                    }
                }
                homeXPopupDialog.clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                homeXPopupDialog.clickOKBet()
            }

        })
        showLeopardErMoney=MoneyOKDeleteView(requireContext())
        showLeopardErMoney.tag = "showLeopardErMoney"
        showLeopardErMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvLeopardClickTwo.indexOfChild(showLeopardErMoney) != -1) {
                        mDatabind.rvLeopardClickTwo.removeView(showLeopardErMoney)
                    }
                }
                homeXPopupDialog.clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                homeXPopupDialog.clickOKBet()
            }

        })
        showLeopardSanMoney=MoneyOKDeleteView(requireContext())
        showLeopardSanMoney.tag = "showLeopardSanMoney"
        showLeopardSanMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvLeopardClickThree.indexOfChild(showLeopardSanMoney) != -1) {
                        mDatabind.rvLeopardClickThree.removeView(showLeopardSanMoney)
                    }
                }
                homeXPopupDialog.clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                homeXPopupDialog.clickOKBet()
            }

        })
        showLeopardSiMoney=MoneyOKDeleteView(requireContext())
        showLeopardSiMoney.tag = "showLeopardSiMoney"
        showLeopardSiMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvLeopardClickFour.indexOfChild(showLeopardSiMoney) != -1) {
                        mDatabind.rvLeopardClickFour.removeView(showLeopardSiMoney)
                    }
                }
                homeXPopupDialog.clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                homeXPopupDialog.clickOKBet()
            }

        })
        showLeopardWuMoney=MoneyOKDeleteView(requireContext())
        showLeopardWuMoney.tag = "showPairsYiMoney"
        showLeopardWuMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvLeopardClickFive.indexOfChild(showLeopardWuMoney) != -1) {
                        mDatabind.rvLeopardClickFive.removeView(showLeopardWuMoney)
                    }
                }
                homeXPopupDialog.clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                homeXPopupDialog.clickOKBet()
            }

        })
        showLeopardLiuMoney=MoneyOKDeleteView(requireContext())
        showLeopardLiuMoney.tag = "showLeopardLiuMoney"
        showLeopardLiuMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeSingle.singleYi .moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rvLeopardClickSix.indexOfChild(showLeopardLiuMoney) != -1) {
                        mDatabind.rvLeopardClickSix.removeView(showLeopardLiuMoney)
                    }
                }
                homeXPopupDialog.clickDelete()
            }

            override fun onConfirm() {
                // 正确的逻辑是投注成功后再执行下面的代码，，现在是测试所以直接成功

                /**
                 * 点击确定，把所有的临时钱赋值给确定钱然后要把没有确定的删除掉,在Activity处理所有的
                 */
                homeXPopupDialog.clickOKBet()
            }

        })

        //点击一
        mDatabind.rvLeopardClickOne. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if(homeXPopupDialog.isCanBetting()&& MyGameManager.isClickOperation&& PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rvLeopardClickOne.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(1)
                showLeopardYiMoney.showTop()
                //计算钱
                ComputeLeopard.leopardYi .moneyTemporary= ComputeLeopard.leopardYi.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showLeopardYiMoney.setShowMoney(ComputeLeopard.leopardYi.moneyTemporary+ ComputeLeopard.leopardYi.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvLeopardClickOne.indexOfChild(showLeopardYiMoney) != -1) {
                    val location = IntArray(2)
                    showLeopardYiMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showLeopardYiMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showLeopardYiMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showLeopardYiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showLeopardYiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showLeopardYiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showLeopardYiMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeLeopard.leopardYi.viewXYTemporary[0]==0&& ComputeLeopard.leopardYi.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showLeopardYiMoney.getLocationInWindow(location)

                                ComputeLeopard.leopardYi.viewXYTemporary[0]= showLeopardYiMoney.left
                                ComputeLeopard.leopardYi.viewXYTemporary[1]=showLeopardYiMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvLeopardClickOne.addView(showLeopardYiMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showLeopardYiMoney.layoutParams = params
                }

            }



        }

        //点击二
        mDatabind.rvLeopardClickTwo. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if(homeXPopupDialog.isCanBetting()&& MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rvLeopardClickTwo.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(2)
                showLeopardErMoney.showTop()
                //计算钱
                ComputeLeopard.leopardEr .moneyTemporary= ComputeLeopard.leopardEr.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showLeopardErMoney.setShowMoney(ComputeLeopard.leopardEr.moneyTemporary+ ComputeLeopard.leopardEr.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvLeopardClickTwo.indexOfChild(showLeopardErMoney) != -1) {
                    val location = IntArray(2)
                    showLeopardErMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showLeopardErMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showLeopardErMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showLeopardErMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showLeopardErMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showLeopardErMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showLeopardErMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeLeopard.leopardEr.viewXYTemporary[0]==0&& ComputeLeopard.leopardEr.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showLeopardErMoney.getLocationInWindow(location)

                                ComputeLeopard.leopardEr.viewXYTemporary[0]= showLeopardErMoney.left
                                ComputeLeopard.leopardEr.viewXYTemporary[1]=showLeopardErMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvLeopardClickTwo.addView(showLeopardErMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showLeopardErMoney.layoutParams = params
                }

            }



        }

        //点击三
        mDatabind.rvLeopardClickThree. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( homeXPopupDialog.isCanBetting()&& MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rvLeopardClickThree.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(3)
                showLeopardSanMoney.showTop()
                //计算钱
                ComputeLeopard.leopardSan .moneyTemporary= ComputeLeopard.leopardSan.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showLeopardSanMoney.setShowMoney(ComputeLeopard.leopardSan.moneyTemporary+ ComputeLeopard.leopardSan.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvLeopardClickThree.indexOfChild(showLeopardSanMoney) != -1) {
                    val location = IntArray(2)
                    showLeopardSanMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showLeopardSanMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showLeopardSanMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showLeopardSanMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showLeopardSanMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showLeopardSanMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showLeopardSanMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeLeopard.leopardSan.viewXYTemporary[0]==0&& ComputeLeopard.leopardSan.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showLeopardSanMoney.getLocationInWindow(location)

                                ComputeLeopard.leopardSan.viewXYTemporary[0]= showLeopardSanMoney.left
                                ComputeLeopard.leopardSan.viewXYTemporary[1]=showLeopardSanMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvLeopardClickThree.addView(showLeopardSanMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showLeopardSanMoney.layoutParams = params
                }

            }



        }


        //点击四
        mDatabind.rvLeopardClickFour. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( homeXPopupDialog.isCanBetting()&& MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rvLeopardClickFour.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(4)
                showLeopardSiMoney.showTop()
                //计算钱
                ComputeLeopard.leopardSi .moneyTemporary= ComputeLeopard.leopardSi.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showLeopardSiMoney.setShowMoney(ComputeLeopard.leopardSi.moneyTemporary+ ComputeLeopard.leopardSi.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvLeopardClickFour.indexOfChild(showLeopardSiMoney) != -1) {
                    val location = IntArray(2)
                    showLeopardSiMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showLeopardSiMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showLeopardSiMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showLeopardSiMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showLeopardSiMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showLeopardSiMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showLeopardSiMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeLeopard.leopardSi.viewXYTemporary[0]==0&& ComputeLeopard.leopardSi.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showLeopardSiMoney.getLocationInWindow(location)

                                ComputeLeopard.leopardSi.viewXYTemporary[0]= showLeopardSiMoney.left
                                ComputeLeopard.leopardSi.viewXYTemporary[1]=showLeopardSiMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvLeopardClickFour.addView(showLeopardSiMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showLeopardSiMoney.layoutParams = params
                }

            }



        }

        //点击五
        mDatabind.rvLeopardClickFive. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( homeXPopupDialog.isCanBetting()&& MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rvLeopardClickFive.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(5)
                showLeopardWuMoney.showTop()
                //计算钱
                ComputeLeopard.leopardWu .moneyTemporary= ComputeLeopard.leopardWu.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showLeopardWuMoney.setShowMoney(ComputeLeopard.leopardWu.moneyTemporary+ ComputeLeopard.leopardWu.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvLeopardClickFive.indexOfChild(showLeopardWuMoney) != -1) {
                    val location = IntArray(2)
                    showLeopardWuMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showLeopardWuMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showLeopardWuMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showLeopardWuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showLeopardWuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showLeopardWuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showLeopardWuMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeLeopard.leopardWu.viewXYTemporary[0]==0&& ComputeLeopard.leopardWu.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showLeopardWuMoney.getLocationInWindow(location)

                                ComputeLeopard.leopardWu.viewXYTemporary[0]= showLeopardWuMoney.left
                                ComputeLeopard.leopardWu.viewXYTemporary[1]=showLeopardWuMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvLeopardClickFive.addView(showLeopardWuMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showLeopardWuMoney.layoutParams = params
                }

            }



        }


        //点击六
        mDatabind.rvLeopardClickSix. setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if(homeXPopupDialog.isCanBetting()&& MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rvLeopardClickFive.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(6)
                showLeopardLiuMoney.showTop()
                //计算钱
                ComputeLeopard.leopardLiu .moneyTemporary= ComputeLeopard.leopardLiu.moneyTemporary+ MyGameManager.noteList[selectNum].money
                showLeopardLiuMoney.setShowMoney(ComputeLeopard.leopardLiu.moneyTemporary+ ComputeLeopard.leopardLiu.moneyOkEmpty)

                //动画位置
                if (mDatabind.rvLeopardClickSix.indexOfChild(showLeopardLiuMoney) != -1) {
                    val location = IntArray(2)
                    showLeopardLiuMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showLeopardLiuMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showLeopardLiuMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showLeopardLiuMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showLeopardLiuMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showLeopardLiuMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+ AutoSizeUtils.dp2px(context, 47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showLeopardLiuMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeLeopard.leopardLiu.viewXYTemporary[0]==0&& ComputeLeopard.leopardLiu.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showLeopardLiuMoney.getLocationInWindow(location)

                                ComputeLeopard.leopardLiu.viewXYTemporary[0]= showLeopardLiuMoney.left
                                ComputeLeopard.leopardLiu.viewXYTemporary[1]=showLeopardLiuMoney.top
                            }
                        }
                    })

                    val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    mDatabind.rvLeopardClickSix.addView(showLeopardLiuMoney, params)
                    // 将新按钮设置为居中
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    showLeopardLiuMoney.layoutParams = params
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
            ComputeLeopard.leopardYi.moneyTemporary=0
            ComputeLeopard.leopardEr.moneyTemporary=0
            ComputeLeopard.leopardSan.moneyTemporary=0
            ComputeLeopard.leopardSi.moneyTemporary=0
            ComputeLeopard.leopardWu.moneyTemporary=0
            ComputeLeopard.leopardLiu.moneyTemporary=0
        }
        //删除没有确定钱的坐标
        if(ComputeLeopard.leopardYi.moneyOkEmpty<=0){
            ComputeLeopard.leopardYi.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeLeopard.leopardEr.moneyOkEmpty<=0){
            ComputeLeopard.leopardEr.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeLeopard.leopardSan.moneyOkEmpty<=0){
            ComputeLeopard.leopardSan.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeLeopard.leopardSi.moneyOkEmpty<=0){
            ComputeLeopard.leopardSi.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeLeopard.leopardWu.moneyOkEmpty<=0){
            ComputeLeopard.leopardWu.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeLeopard.leopardLiu.moneyOkEmpty<=0){
            ComputeLeopard.leopardLiu.viewXYTemporary=intArrayOf(0, 0)
        }

        //
        if(ComputeLeopard.leopardYi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvLeopardClickOne.indexOfChild(showLeopardYiMoney) != -1) {
                mDatabind.rvLeopardClickOne.removeView(showLeopardYiMoney)
            }
        }else{
            showLeopardYiMoney.hiddenTop()
        }

        if(ComputeLeopard.leopardEr.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvLeopardClickTwo.indexOfChild(showLeopardErMoney) != -1) {
                mDatabind.rvLeopardClickTwo.removeView(showLeopardErMoney)
            }
        }else{
            showLeopardErMoney.hiddenTop()
        }

        if(ComputeLeopard.leopardSan.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvLeopardClickThree.indexOfChild(showLeopardSanMoney) != -1) {
                mDatabind.rvLeopardClickThree.removeView(showLeopardSanMoney)
            }
        }else{
            showLeopardSanMoney.hiddenTop()
        }


        if(ComputeLeopard.leopardSi.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvLeopardClickFour.indexOfChild(showLeopardSiMoney) != -1) {
                mDatabind.rvLeopardClickFour.removeView(showLeopardSiMoney)
            }
        }else{
            showLeopardSiMoney.hiddenTop()
        }


        if(ComputeLeopard.leopardWu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvLeopardClickFive.indexOfChild(showLeopardWuMoney) != -1) {
                mDatabind.rvLeopardClickFive.removeView(showLeopardWuMoney)
            }
        }else{
            showLeopardWuMoney.hiddenTop()
        }

        if(ComputeLeopard.leopardLiu.moneyOkEmpty<=0){
            //判断控件是否加入了
            if (mDatabind.rvLeopardClickSix.indexOfChild(showLeopardLiuMoney) != -1) {
                mDatabind.rvLeopardClickSix.removeView(showLeopardLiuMoney)
            }
        }else{
            showLeopardLiuMoney.hiddenTop()
        }

    }
    /**
     * 从新设置一下每个注区显示投注的钱
     */
    fun setAllShowViewMoney(){
        showLeopardYiMoney.setShowMoney(ComputeLeopard.leopardYi.moneyOkEmpty)
        showLeopardErMoney.setShowMoney(ComputeLeopard.leopardEr.moneyOkEmpty)
        showLeopardSanMoney.setShowMoney(ComputeLeopard.leopardSan.moneyOkEmpty)
        showLeopardSiMoney.setShowMoney(ComputeLeopard.leopardSi.moneyOkEmpty)
        showLeopardWuMoney.setShowMoney(ComputeLeopard.leopardWu.moneyOkEmpty)
        showLeopardLiuMoney.setShowMoney(ComputeLeopard.leopardLiu.moneyOkEmpty)
    }
    /**
     * inPrizeList   中奖的区域
     * pressureIn    压中的区域
     */
    fun flicker(inPrizeList:ArrayList<InPrizeBean>, pressureIn:ArrayList<InPrizeBean>){
        closeBetting()
        //中奖区域
        var inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTTriple1.num
        inPrizeList.add(inPrizeBean)
        inPrizeBean= InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTTriple6.num
        inPrizeList.add(inPrizeBean)
        fadeOut(true,inPrizeList)
        //压中
        for (i in 0 until  inPrizeList.size) {
            if(inPrizeList[i].inPrizType== NOTES_ENUM.QTTriple1.num&& ComputeLeopard.leopardYi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTTriple1.num
                pressure.money=(ComputeLeopard.leopardYi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTTriple2.num&& ComputeLeopard.leopardEr .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTTriple2.num
                pressure.money=(ComputeLeopard.leopardEr.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTTriple3.num&& ComputeLeopard.leopardSan .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTTriple3.num
                pressure.money=(ComputeLeopard.leopardSan.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTTriple4.num&& ComputeLeopard.leopardSi .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTTriple4.num
                pressure.money=(ComputeLeopard.leopardSi.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTTriple5.num&& ComputeLeopard.leopardWu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTTriple5.num
                pressure.money=(ComputeLeopard.leopardWu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else   if(inPrizeList[i].inPrizType== NOTES_ENUM.QTTriple6.num&& ComputeLeopard.leopardLiu .moneyOkEmpty>0){
                var pressure= InPrizeBean()
                pressure.inPrizType= NOTES_ENUM.QTTriple6.num
                pressure.money=(ComputeLeopard.leopardLiu.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }

        }

        //显示数据
        for (i in 0 until  pressureIn.size) {
            if(pressureIn[i].inPrizType== NOTES_ENUM.QTTriple1.num){
                //计算钱
                showLeopardYiMoney.setShowMoney(pressureIn[i].money)
                showLeopardYiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvLeopardClickOne.addView(showLeopardYiMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeopardYiMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTTriple2.num){
                //计算钱
                showLeopardErMoney.setShowMoney(pressureIn[i].money)
                showLeopardErMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvLeopardClickTwo.addView(showLeopardErMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeopardErMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTTriple3.num){
                //计算钱
                showLeopardSanMoney.setShowMoney(pressureIn[i].money)
                showLeopardSanMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvLeopardClickThree.addView(showLeopardSanMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeopardSanMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTTriple4.num){
                //计算钱
                showLeopardSiMoney.setShowMoney(pressureIn[i].money)
                showLeopardSiMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvLeopardClickFour.addView(showLeopardSiMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeopardSiMoney.layoutParams = params

            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTTriple5.num){
                //计算钱
                showLeopardWuMoney.setShowMoney(pressureIn[i].money)
                showLeopardWuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvLeopardClickFive.addView(showLeopardWuMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeopardWuMoney.layoutParams = params
            }else  if(pressureIn[i].inPrizType== NOTES_ENUM.QTTriple6.num){
                //计算钱
                showLeopardLiuMoney.setShowMoney(pressureIn[i].money)
                showLeopardLiuMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rvLeopardClickSix.addView(showLeopardLiuMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showLeopardLiuMoney.layoutParams = params
            }



        }

        deletePreviousRound()

    }


    /**
     * 删除上一轮的数据
     */
    fun  deletePreviousRound(){
        ComputeLeopard.leopardYi.viewXYTemporary= intArrayOf(0, 0)
        ComputeLeopard.leopardYi.viewXYLast= intArrayOf(0, 0)
        ComputeLeopard.leopardYi.moneyTemporary= 0
        ComputeLeopard.leopardYi.moneyOkEmpty= 0

        ComputeLeopard.leopardEr.viewXYTemporary= intArrayOf(0, 0)
        ComputeLeopard.leopardEr.viewXYLast= intArrayOf(0, 0)
        ComputeLeopard.leopardEr.moneyTemporary= 0
        ComputeLeopard.leopardEr.moneyOkEmpty= 0

        ComputeLeopard.leopardSan.viewXYTemporary= intArrayOf(0, 0)
        ComputeLeopard.leopardSan.viewXYLast= intArrayOf(0, 0)
        ComputeLeopard.leopardSan.moneyTemporary= 0
        ComputeLeopard.leopardSan.moneyOkEmpty= 0

        ComputeLeopard.leopardSi.viewXYTemporary= intArrayOf(0, 0)
        ComputeLeopard.leopardSi.viewXYLast= intArrayOf(0, 0)
        ComputeLeopard.leopardSi.moneyTemporary= 0
        ComputeLeopard.leopardSi.moneyOkEmpty= 0

        ComputeLeopard.leopardWu.viewXYTemporary= intArrayOf(0, 0)
        ComputeLeopard.leopardWu.viewXYLast= intArrayOf(0, 0)
        ComputeLeopard.leopardWu.moneyTemporary= 0
        ComputeLeopard.leopardWu.moneyOkEmpty= 0

        ComputeLeopard.leopardLiu.viewXYTemporary= intArrayOf(0, 0)
        ComputeLeopard.leopardLiu.viewXYLast= intArrayOf(0, 0)
        ComputeLeopard.leopardLiu.moneyTemporary= 0
        ComputeLeopard.leopardLiu.moneyOkEmpty= 0


    }

    /**
     * 中奖区域闪烁的动画
     *isAnimation true  是执行动画   flase是取消动画
     */
    fun fadeOut(isAnimation:Boolean,inPrizeList:ArrayList<InPrizeBean> =ArrayList<InPrizeBean>()){
        if(isAnimation){
            inPrizeList.forEach {
                if(it.inPrizType== NOTES_ENUM.QTTriple1.num){
                    mDatabind.ivLeopardOne.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivLeopardOne, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.QTTriple2.num){
                    mDatabind.ivLeopardTwo.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivLeopardTwo, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.QTTriple3.num){
                    mDatabind.ivLeopardThree.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivLeopardThree, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.QTTriple4.num){
                    mDatabind.ivLeopardFour.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivLeopardFour, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType== NOTES_ENUM.QTTriple5.num){
                    mDatabind.ivLeopardFive.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivLeopardFive, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }else  if(it.inPrizType== NOTES_ENUM.QTTriple6.num){
                    mDatabind.ivLeopardSix.visibility= View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivLeopardSix, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }
            }

            // 开始动画
            animators.forEach { it.start() }

        }else{
            mDatabind.ivLeopardOne.visibility= View.GONE
            mDatabind.ivLeopardTwo.visibility= View.GONE
            mDatabind.ivLeopardThree.visibility= View.GONE
            mDatabind.ivLeopardFour.visibility= View.GONE
            mDatabind.ivLeopardFive.visibility= View.GONE
            mDatabind.ivLeopardSix.visibility= View.GONE

            animators.forEach { it.cancel() }
            animators.clear()
        }


    }


    /**
     * 得到开奖信息后删除所有的在界面上的注码
     */
    fun closeBetting(){
        if (mDatabind.rvLeopardClickOne.indexOfChild(showLeopardYiMoney) != -1) {
            mDatabind.rvLeopardClickOne.removeView(showLeopardYiMoney)
        }
        if (mDatabind.rvLeopardClickTwo.indexOfChild(showLeopardErMoney) != -1) {
            mDatabind.rvLeopardClickTwo.removeView(showLeopardErMoney)
        }

        if (mDatabind.rvLeopardClickThree.indexOfChild(showLeopardSanMoney) != -1) {
            mDatabind.rvLeopardClickThree.removeView(showLeopardSanMoney)
        }

        if (mDatabind.rvLeopardClickFour.indexOfChild(showLeopardSiMoney) != -1) {
            mDatabind.rvLeopardClickFour.removeView(showLeopardSiMoney)
        }
        if (mDatabind.rvLeopardClickFive.indexOfChild(showLeopardWuMoney) != -1) {
            mDatabind.rvLeopardClickFive.removeView(showLeopardWuMoney)
        }
        if (mDatabind.rvLeopardClickSix.indexOfChild(showLeopardLiuMoney) != -1) {
            mDatabind.rvLeopardClickSix.removeView(showLeopardLiuMoney)
        }

    }

    /**
     * 点击每个模块的动画，隐藏没点击的所有的头部
     * 1  2 3  4   5  6   0
     */
    private fun clickAnimationIsHidden(num:Int ){
        if(num!=1){
            showLeopardYiMoney.hiddenTop()
        }
        if(num!=2){
            showLeopardErMoney.hiddenTop()
        }

        if(num!=3){
            showLeopardSanMoney.hiddenTop()
        }
        if(num!=4){
            showLeopardSiMoney.hiddenTop()
        }
        if(num!=5){
            showLeopardWuMoney.hiddenTop()
        }
        if(num!=6){
            showLeopardLiuMoney.hiddenTop()
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