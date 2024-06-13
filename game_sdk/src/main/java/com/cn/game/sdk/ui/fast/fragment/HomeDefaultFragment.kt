package com.cn.game.sdk.ui.fast.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.bean.InPrizeBean
import com.cn.game.sdk.databinding.FragmentHomeDefaultBinding
import com.cn.game.sdk.enums.NOTES_ENUM
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.bean.ComputeDefault
import com.cn.game.sdk.tool.HomeXPopupDialog
import com.cn.game.sdk.tool.PromptSoundPlay
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.MoneyOKDeleteView
import com.cn.game.sdk.view.MoneyOKView
import com.xcjh.base_lib.utils.dp2px
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
    /**
     * 右角注区的控件确定
     */
    lateinit var showRightTopMoney: MoneyOKView

    /**
     * 左下角注区的控件确定
     */
    lateinit var showLeftBelowMoney: MoneyOKView

    /**
     * 右下角注区的控件确定
     */
    lateinit var showRightBelowMoney: MoneyOKView

    /**
     * 中间注区
     */
    lateinit var showCentreDateMoney: MoneyOKDeleteView

    private var animators: MutableList<ObjectAnimator> = mutableListOf()
    var lastClickTime = 0L
    lateinit var  homeXPopupDialog:HomeXPopupDialog

    fun   setHomeXPopupDialogDate(homeXPopupDialog:HomeXPopupDialog){
        this.homeXPopupDialog=homeXPopupDialog
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {

        arguments?.let {
            type = it.getInt("type")
        }



        //初始化左上角
        showLeftTopMoney=MoneyOKView(requireContext())
        showLeftTopMoney.tag = "showLeftTopMoney"
        showLeftTopMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {
                if(ComputeDefault.leftTop.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
                        mDatabind.rlHomeRoot.removeView(showLeftTopMoney)
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

        //初始化右上角
        showRightTopMoney=MoneyOKView(requireContext())
        showRightTopMoney.tag = "showRightTopMoney"
        showRightTopMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {
//                ComputeDefault.rightTop.moneyOkEmpty=20
                if(ComputeDefault.rightTop.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlClickRightTop.indexOfChild(showRightTopMoney) != -1) {
                        mDatabind.rlClickRightTop.removeView(showRightTopMoney)
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

        //左下
        showLeftBelowMoney=MoneyOKView(requireContext())
        showLeftBelowMoney.tag = "showLeftBelowMoney"
        showLeftBelowMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {
                if(ComputeDefault.leftBelow.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlClickLeftBelow.indexOfChild(showLeftBelowMoney) != -1) {
                        mDatabind.rlClickLeftBelow.removeView(showLeftBelowMoney)
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

        //右下
        showRightBelowMoney=MoneyOKView(requireContext())
        showRightBelowMoney.tag = "showRightBelowMoney"
        showRightBelowMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {
                if(ComputeDefault.rightBelow.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlClickRightBelow.indexOfChild(showRightBelowMoney) != -1) {
                        mDatabind.rlClickRightBelow.removeView(showRightBelowMoney)
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


        //中间
        showCentreDateMoney=MoneyOKDeleteView(requireContext())
        showCentreDateMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
                if(ComputeDefault.centreDate.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlClickCentre.indexOfChild(showCentreDateMoney) != -1) {
                        mDatabind.rlClickCentre.removeView(showCentreDateMoney)
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


        appGameViewModel.ceshEvent.observe(this){

        }
        //左上
        clickLeftTop()
        //右上
        clickRightTop()
        /**
         * 中间点击  第一步
         */
        mDatabind.rlClickCentre.setOnClickListener {
            //先判断余额是否够这次 并且扣取钱
            if( homeXPopupDialog.isCanBetting()&&MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                val location = IntArray(2)
                mDatabind.rlClickCentre.getLocationOnScreen(location)
                var selectNum=0
                for (i in 0 until   MyGameManager.noteList.size) {
                    if(MyGameManager.noteList[i].select){
                        selectNum=i
                        break
                    }
                }
                clickAnimationIsHidden(4)
                showCentreDateMoney.showTop()
                //计算钱
                ComputeDefault.centreDate.moneyTemporary= ComputeDefault.centreDate.moneyTemporary+MyGameManager.noteList[selectNum].money
                showCentreDateMoney.setShowMoney(ComputeDefault.centreDate.moneyTemporary+ ComputeDefault.centreDate.moneyOkEmpty)

                //动画位置
                if (mDatabind.rlClickCentre.indexOfChild(showCentreDateMoney) != -1) {
                    val location = IntArray(2)
                    showCentreDateMoney.getLocationOnScreen(location)
                    val xOnScreen = location[0]
                    val yOnScreen = location[1]
                    //通过显示的控件得到相对于屏幕的位置
                    var  rax=xOnScreen
                    var ray=yOnScreen+dp2px(context,47f)


                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showCentreDateMoney.ivShowBg )

                } else {
                    val viewTreeObserver = showCentreDateMoney.viewTreeObserver
                    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // 确保只监听一次
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                showCentreDateMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            } else {
                                showCentreDateMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }

                            // 获取视图在屏幕上的绝对位置
                            val location = IntArray(2)
                            showCentreDateMoney.getLocationOnScreen(location)
                            val xOnScreen = location[0]
                            val yOnScreen = location[1]


                            //通过显示的控件得到相对于屏幕的位置
                            var  rax=xOnScreen
                            var ray=yOnScreen+dp2px(context,47f)

                            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showCentreDateMoney.ivShowBg)
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeDefault.centreDate.viewXYTemporary[0]==0&& ComputeDefault.centreDate.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showCentreDateMoney.getLocationInWindow(location)

                                ComputeDefault.centreDate.viewXYTemporary[0]= showCentreDateMoney.left
                                ComputeDefault.centreDate.viewXYTemporary[1]=showCentreDateMoney.top
                            }
                        }
                    })
//                    if(ComputeDefault.centreDate.viewXYTemporary[0]!=0&&ComputeDefault.centreDate.viewXYTemporary[1]!=0){
//                        val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                        params.leftMargin= ComputeDefault.centreDate.viewXYTemporary[0]
//                        params.topMargin= ComputeDefault.centreDate.viewXYTemporary[1]
//                        showCentreDateMoney.layoutParams = params
//                        mDatabind.rlClickCentre.addView(showCentreDateMoney)
//                    }else{
//                        val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                        mDatabind.rlClickCentre.addView(showCentreDateMoney, params)
//                        // 将新按钮设置为居中
//                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//                        showCentreDateMoney.layoutParams = params
//                    }
                      val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        mDatabind.rlClickCentre.addView(showCentreDateMoney, params)
                        // 将新按钮设置为居中
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        showCentreDateMoney.layoutParams = params
                }

            }



        }

        /**
         * 左下点击  第一步
         */
        mDatabind.rlClickLeftBelow.setOnTouchListener { v, event ->
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
        }
        /**
         * 点击右下
         */
        mDatabind.rlClickRightBelowImage.setOnTouchListener { v, event ->
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
                            if(ComputeDefault.rightBelow.viewXYTemporary [0]==0&& ComputeDefault.rightBelow.viewXYTemporary[1]==0){
                                ComputeDefault.rightBelow.viewXYTemporary[0]=rax.toInt()
                                ComputeDefault.rightBelow.viewXYTemporary[1]=y.toInt()
                            }

                            var selectNum=0
                            for (i in 0 until   MyGameManager.noteList.size) {
                                if(MyGameManager.noteList[i].select){
                                    selectNum=i
                                    break
                                }
                            }
                            //计算钱
                            ComputeDefault.rightBelow.moneyTemporary= ComputeDefault.rightBelow.moneyTemporary+MyGameManager.noteList[selectNum].money
                            showRightBelowMoney.setShowMoney(ComputeDefault.rightBelow.moneyTemporary+ ComputeDefault.rightBelow.moneyOkEmpty)
                            //判断是否添加上去了这个viwe
                            if (mDatabind.rlHomeRoot.indexOfChild(showRightBelowMoney) != -1) {

                                val location = IntArray(2)
                                showRightBelowMoney.getLocationOnScreen(location)
                                val xOnScreen = location[0]
                                val yOnScreen = location[1]
                                //通过显示的控件得到相对于屏幕的位置
                                var  rax=xOnScreen
                                var ray=yOnScreen+requireContext().dp2px(52)

                                homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView =showRightBelowMoney.ivShowBg )
                            } else {

                                val viewTreeObserver = showRightBelowMoney.viewTreeObserver
                                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        // 确保只监听一次
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            showRightBelowMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                        } else {
                                            showRightBelowMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        }
                                        // 获取视图在屏幕上的绝对位置
                                        val location = IntArray(2)
                                        showRightBelowMoney.getLocationOnScreen(location)
                                        val xOnScreen = location[0]
                                        val yOnScreen = location[1]
                                        //通过显示的控件得到相对于屏幕的位置
                                        var  rax=xOnScreen
                                        var ray=yOnScreen+requireContext().dp2px(52)

                                        homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(),animationView =showRightBelowMoney.ivShowBg)
                                    }
                                })


                                // 动态添加的视图未成功添加到布局中
                                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                mDatabind.rlHomeRoot.addView(showRightBelowMoney, params)
                                showRightBelowMoney.translationX =  ComputeDefault.rightBelow.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
                                showRightBelowMoney.translationY =  ComputeDefault.rightBelow.viewXYTemporary[1].toFloat()+requireContext().dp2px(35)

                            }
//                            (context as GameHomeActivity).startAnimation(ComputeDefault.leftBelow.screenXYTemporary[0].toFloat(),ComputeDefault.leftBelow.screenXYTemporary[1].toFloat(),true)

                            //当前点击的这个注区头部显示出来
                            showRightBelowMoney.showTop()
                            //点击每个模块的动画，隐藏没点击的所有的头部
                            clickAnimationIsHidden(3)

                        }

                    }


                }
            }
            false // 返回 true 表示事件已经被处理
        }


        /**
         * 如果有数据就添加进去，
         */
        setNoFinish()
    }

    /**
     * 点击左上角
     */
    @SuppressLint("ClickableViewAccessibility")
    fun  clickLeftTop(){
       mDatabind.rlClick.setOnTouchListener { v, event ->
           Log.i("BTBTBTBTB","999==========="+event.action)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i("FFFFF","44444444444444")
                    val currentTime = System.currentTimeMillis()
                    //先判断余额是否够这次   并且是否可以点击

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
                            // 点击不在边缘
                            handleNonEdgeClick(x,y,rax,ray)
                            //当前点击的这个注区头部显示出来
                            showLeftTopMoney.showTop()
                            //点击每个模块的动画，隐藏没点击的所有的头部
                            clickAnimationIsHidden(0)

                        }

                }
                    lastClickTime = currentTime

                }
                MotionEvent.ACTION_UP -> {
                    Log.i("FFFFF","555555")

                }
            }
            false // 返回 true 表示事件已经被处理
        }



    }

    /**
     * 点击右上角
     */
    @SuppressLint("ClickableViewAccessibility", "SuspiciousIndentation")
    fun  clickRightTop(){
        mDatabind.rlClickIamge.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i("VVVVVVV","=============按下")
                    //先判断余额是否够这次
                    //先判断余额是否够这次
                    if(homeXPopupDialog.isCanBetting()&&MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
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
                            if(ComputeDefault.rightTop.viewXYTemporary [0]==0&& ComputeDefault.rightTop.viewXYTemporary[1]==0){
                                ComputeDefault.rightTop.viewXYTemporary[0]=rax.toInt()
                                ComputeDefault.rightTop.viewXYTemporary[1]=y.toInt()
                            }

                            var selectNum=0
                            for (i in 0 until   MyGameManager.noteList.size) {
                                if(MyGameManager.noteList[i].select){
                                    selectNum=i
                                    break
                                }
                            }
                            //计算钱
                            ComputeDefault.rightTop.moneyTemporary= ComputeDefault.rightTop.moneyTemporary+MyGameManager.noteList[selectNum].money
                            showRightTopMoney.setShowMoney(ComputeDefault.rightTop.moneyTemporary+ ComputeDefault.rightTop.moneyOkEmpty)
                            //判断是否添加上去了这个viwe
                            if (mDatabind.rlHomeRoot.indexOfChild(showRightTopMoney) != -1) {

                                val location = IntArray(2)
                                showRightTopMoney.getLocationOnScreen(location)
                                val xOnScreen = location[0]
                                val yOnScreen = location[1]
                                //通过显示的控件得到相对于屏幕的位置
                                var  rax=xOnScreen
                                var ray=yOnScreen+requireContext().dp2px(52)

                                homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView = showRightTopMoney.ivShowBg)

                            } else {
                                val viewTreeObserver = showRightTopMoney.viewTreeObserver
                                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        // 确保只监听一次
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            showRightTopMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
                                        } else {
                                            showRightTopMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                        }
                                        // 获取视图在屏幕上的绝对位置
                                        val location = IntArray(2)
                                        showRightTopMoney.getLocationOnScreen(location)
                                        val xOnScreen = location[0]
                                        val yOnScreen = location[1]
                                        //通过显示的控件得到相对于屏幕的位置
                                        var  rax=xOnScreen
                                        var ray=yOnScreen+requireContext().dp2px(52)

                                        homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView = showRightTopMoney.ivShowBg)
                                     }
                                })


                                // 动态添加的视图未成功添加到布局中
                                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                mDatabind.rlHomeRoot.addView(showRightTopMoney, params)
                                showRightTopMoney.translationX =  ComputeDefault.rightTop.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
                                showRightTopMoney.translationY =  ComputeDefault.rightTop.viewXYTemporary[1].toFloat()-requireContext().dp2px(52)

                            }
//                            (context as GameHomeActivity).startAnimation(ComputeDefault.rightTop.screenXYTemporary[0].toFloat(),ComputeDefault.rightTop.screenXYTemporary[1].toFloat(),true)

                            //当前点击的这个注区头部显示出来
                            showRightTopMoney.showTop()
                            //点击每个模块的动画，隐藏没点击的所有的头部
                            clickAnimationIsHidden(1)

                        }

                    }


                }
                MotionEvent.ACTION_MOVE -> {
                    Log.i("VVVVVVV","=============移动")
                }

            }
            false // 返回 true 表示事件已经被处理
        }

    }


    private fun handleEdgeClick(
        isOnLeftEdge: Boolean,
        isOnTopEdge: Boolean,
        isOnRightEdge: Boolean,
        isOnBottomEdge: Boolean) {
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
        if(ComputeDefault.leftTop.viewXYTemporary [0]==0&& ComputeDefault.leftTop.viewXYTemporary[1]==0){
            ComputeDefault.leftTop.viewXYTemporary[0]=x.toInt()
            ComputeDefault.leftTop.viewXYTemporary[1]=y.toInt()
        }

        var selectNum=0
        for (i in 0 until   MyGameManager.noteList.size) {
            if(MyGameManager.noteList[i].select){
                selectNum=i
                break
            }
        }
        //计算钱
        ComputeDefault.leftTop.moneyTemporary= ComputeDefault.leftTop.moneyTemporary+MyGameManager.noteList[selectNum].money
        showLeftTopMoney.setShowMoney(ComputeDefault.leftTop.moneyTemporary+ ComputeDefault.leftTop.moneyOkEmpty)
        //判断是否添加上去了这个viwe
        if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {


            val location = IntArray(2)
            showLeftTopMoney.getLocationOnScreen(location)
            val xOnScreen = location[0]
            val yOnScreen = location[1]
            //通过显示的控件得到相对于屏幕的位置
            var  rax=xOnScreen
            var ray=yOnScreen+dp2px(context,47f)
            homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView = showLeftTopMoney.ivShowBg)

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
                    //通过显示的控件得到相对于屏幕的位置
                  var  rax=xOnScreen
                   var ray=yOnScreen+dp2px(context,47f)

                    homeXPopupDialog.startAnimation(rax.toFloat(),ray.toFloat(), animationView = showLeftTopMoney.ivShowBg)

                }
            })


            // 动态添加的视图未成功添加到布局中
            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlHomeRoot.addView(showLeftTopMoney, params)
            showLeftTopMoney.translationX =  ComputeDefault.leftTop.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
            showLeftTopMoney.translationY =  ComputeDefault.leftTop.viewXYTemporary[1].toFloat()-requireContext().dp2px(52)

        }

   }


    /**
     * 点击每个模块的动画，隐藏没点击的所有的头部
     * 0是左上  1是右上   2是左下  3是右下  4是中间   等于-1的话就把所有的头部取消
     */
    private fun clickAnimationIsHidden(num:Int ){
        if(num!=0){
            showLeftTopMoney.hiddenTop()
        }
        if(num!=1){
            showRightTopMoney.hiddenTop()
        }

        if(num!=2){
            showLeftBelowMoney.hiddenTop()
        }
        if(num!=3){
            showRightBelowMoney.hiddenTop()
        }
        if(num!=4){
            showCentreDateMoney.hiddenTop()
        }



    }

    /**
     * 删除投注 1111 就保留上一次的确定的钱,isEmpty是否清空临时的钱，如果是点击的叉叉就要清空~~如果是勾勾就不用清除
     */
    fun deleteBet(isEmpty:Boolean=true){
        //删除临时钱
        if(isEmpty){
            ComputeDefault.leftTop.moneyTemporary=0
            ComputeDefault.rightTop.moneyTemporary=0
            ComputeDefault.leftBelow.moneyTemporary=0
            ComputeDefault.rightBelow.moneyTemporary=0
            ComputeDefault.centreDate.moneyTemporary=0
        }
        //删除没有确定钱的坐标
        if(ComputeDefault.leftTop.moneyOkEmpty<=0){
            ComputeDefault.leftTop.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeDefault.rightTop.moneyOkEmpty<=0){
            ComputeDefault.rightTop.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeDefault.leftBelow.moneyOkEmpty<=0){
            ComputeDefault.leftBelow.viewXYTemporary=intArrayOf(0, 0)
        }
        if(ComputeDefault.rightBelow.moneyOkEmpty<=0){
            ComputeDefault.rightBelow.viewXYTemporary=intArrayOf(0, 0)
        }

        //左上角确定的钱没有
           if(ComputeDefault.leftTop.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
                    mDatabind.rlHomeRoot.removeView(showLeftTopMoney)
                }
            }else{
                showLeftTopMoney.hiddenTop()
            }


            //右上角确定的钱没有
            if(ComputeDefault.rightTop.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlHomeRoot.indexOfChild(showRightTopMoney) != -1) {
                    mDatabind.rlHomeRoot.removeView(showRightTopMoney)
                }
            }else{
                showRightTopMoney.hiddenTop()
            }



        //左下角确定的钱没有
            if(ComputeDefault.leftBelow.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlHomeRoot.indexOfChild(showLeftBelowMoney) != -1) {
                    mDatabind.rlHomeRoot.removeView(showLeftBelowMoney)
                }
            }else{
                showLeftBelowMoney.hiddenTop()
            }

        //右下角确定的钱没有
            if(ComputeDefault.rightBelow.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlHomeRoot.indexOfChild(showRightBelowMoney) != -1) {
                    mDatabind.rlHomeRoot.removeView(showRightBelowMoney)
                }
            }else{
                showRightBelowMoney.hiddenTop()
            }


        //中间
            if(ComputeDefault.centreDate.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlClickCentre.indexOfChild(showCentreDateMoney) != -1) {
                    mDatabind.rlClickCentre.removeView(showCentreDateMoney)
                }
            }else{
                showCentreDateMoney.hiddenTop()
            }

    }

    /**
     * 从新设置一下每个注区显示投注的钱
     */
    fun setAllShowViewMoney(){
        showLeftTopMoney.setShowMoney(ComputeDefault.leftTop.moneyOkEmpty)
        showRightTopMoney.setShowMoney(ComputeDefault.rightTop.moneyOkEmpty)
        showLeftBelowMoney.setShowMoney(ComputeDefault.leftBelow.moneyOkEmpty)
        showRightBelowMoney.setShowMoney(ComputeDefault.rightBelow.moneyOkEmpty)
        showCentreDateMoney.setShowMoney(ComputeDefault.centreDate.moneyOkEmpty)
    }


    /**
     * 打开游戏界面的时候，如果没有结束当前游戏的时候就要 把确定的钱显示在相对于注区的位置
     */
    fun setNoFinish(){

        clickAnimationIsHidden(-1)
        //左上
        if(ComputeDefault.leftTop.moneyOkEmpty>0){
            //临时的动画位置也要赋值，不然点击的时候会有问题
            ComputeDefault.leftTop.viewXYTemporary[0]= ComputeDefault.leftTop.viewXYLast[0]
            ComputeDefault.leftTop.viewXYTemporary[1]= ComputeDefault.leftTop.viewXYLast[1]
            //计算钱
            showLeftTopMoney.setShowMoney(ComputeDefault.leftTop.moneyTemporary+ ComputeDefault.leftTop.moneyOkEmpty)
            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlHomeRoot.addView(showLeftTopMoney, params)
            showLeftTopMoney.translationX =  ComputeDefault.leftTop.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
            showLeftTopMoney.translationY =  ComputeDefault.leftTop.viewXYTemporary[1].toFloat()-requireContext().dp2px(52)
        }
        //右上
        if(ComputeDefault.rightTop.moneyOkEmpty>0){
            //临时的动画位置也要赋值，不然点击的时候会有问题
            ComputeDefault.rightTop.viewXYTemporary[0]= ComputeDefault.rightTop.viewXYLast[0]
            ComputeDefault.rightTop.viewXYTemporary[1]= ComputeDefault.rightTop.viewXYLast[1]
            //计算钱
            showRightTopMoney.setShowMoney(ComputeDefault.rightTop.moneyTemporary+ ComputeDefault.rightTop.moneyOkEmpty)

            // 动态添加的视图未成功添加到布局中
            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlHomeRoot.addView(showRightTopMoney, params)
            showRightTopMoney.translationX =  ComputeDefault.rightTop.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
            showRightTopMoney.translationY =  ComputeDefault.rightTop.viewXYTemporary[1].toFloat()-requireContext().dp2px(52)

        }

        //左下
        if(ComputeDefault.leftBelow.moneyOkEmpty>0){
            //临时的动画位置也要赋值，不然点击的时候会有问题
            ComputeDefault.leftBelow.viewXYTemporary[0]= ComputeDefault.leftBelow.viewXYLast[0]
            ComputeDefault.leftBelow.viewXYTemporary[1]= ComputeDefault.leftBelow.viewXYLast[1]
            //计算钱
            showLeftBelowMoney.setShowMoney(ComputeDefault.leftBelow.moneyTemporary+ ComputeDefault.leftBelow.moneyOkEmpty)

            // 动态添加的视图未成功添加到布局中
            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlHomeRoot.addView(showLeftBelowMoney, params)
            showLeftBelowMoney.translationX =  ComputeDefault.leftBelow.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
            showLeftBelowMoney.translationY =  ComputeDefault.leftBelow.viewXYTemporary[1].toFloat()+requireContext().dp2px(35)

        }
        //右下
        if(ComputeDefault.rightBelow.moneyOkEmpty>0){
            //临时的动画位置也要赋值，不然点击的时候会有问题
            ComputeDefault.rightBelow.viewXYTemporary[0]= ComputeDefault.rightBelow.viewXYLast[0]
            ComputeDefault.rightBelow.viewXYTemporary[1]= ComputeDefault.rightBelow.viewXYLast[1]
            //计算钱
            showRightBelowMoney.setShowMoney(ComputeDefault.rightBelow.moneyTemporary+ ComputeDefault.rightBelow.moneyOkEmpty)
            // 动态添加的视图未成功添加到布局中
            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlHomeRoot.addView(showRightBelowMoney, params)
            showRightBelowMoney.translationX =  ComputeDefault.rightBelow.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
            showRightBelowMoney.translationY =  ComputeDefault.rightBelow.viewXYTemporary[1].toFloat()+requireContext().dp2px(35)
        }

        //中间
        if(ComputeDefault.centreDate.moneyOkEmpty>0){
            //临时的动画位置也要赋值，不然点击的时候会有问题
            ComputeDefault.centreDate.viewXYTemporary[0]= ComputeDefault.centreDate.viewXYLast[0]
            ComputeDefault.centreDate.viewXYTemporary[1]= ComputeDefault.centreDate.viewXYLast[1]
            //计算钱
            showCentreDateMoney.setShowMoney(ComputeDefault.centreDate.moneyTemporary+ ComputeDefault.centreDate.moneyOkEmpty)


            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlClickCentre.addView(showCentreDateMoney, params)
            // 将新按钮设置为居中
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            showCentreDateMoney.layoutParams = params

        }

    }

    /**
     * inPrizeList   中奖的区域
     * pressureIn    压中的区域
     */
    fun flicker(inPrizeList:ArrayList<InPrizeBean>,pressureIn:ArrayList<InPrizeBean>){
        closeBetting()
        //中奖区域
        var inPrizeBean=InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTDefaultSmall.num
        inPrizeList.add(inPrizeBean)
          inPrizeBean=InPrizeBean()
        inPrizeBean.inPrizType= NOTES_ENUM.QTDefaultTriple.num
        inPrizeList.add(inPrizeBean)
        fadeOut(true,inPrizeList)
        //压中
        for (i in 0 until  inPrizeList.size) {
            if(inPrizeList[i].inPrizType==NOTES_ENUM.QTDefaultSmall.num&& ComputeDefault.leftTop.moneyOkEmpty>0){
                var pressure=InPrizeBean()
                pressure.inPrizType=NOTES_ENUM.QTDefaultSmall.num
                pressure.money=(ComputeDefault.leftTop.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else  if(inPrizeList[i].inPrizType==NOTES_ENUM.QTDefaultBig.num&& ComputeDefault.rightTop.moneyOkEmpty>0){


                var pressure=InPrizeBean()
                pressure.inPrizType=NOTES_ENUM.QTDefaultBig.num
                pressure.money=(ComputeDefault.rightTop.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else  if(inPrizeList[i].inPrizType==NOTES_ENUM.QTDefaultSingle.num&& ComputeDefault.leftBelow.moneyOkEmpty>0){
                var pressure=InPrizeBean()
                pressure.inPrizType=NOTES_ENUM.QTDefaultSingle.num
                pressure.money=(ComputeDefault.leftBelow.moneyOkEmpty*2)
                pressureIn.add(pressure)

            } else  if(inPrizeList[i].inPrizType==NOTES_ENUM.QTDefaultDouble.num&& ComputeDefault.rightBelow.moneyOkEmpty>0){

                var pressure=InPrizeBean()
                pressure.inPrizType=NOTES_ENUM.QTDefaultDouble.num
                pressure.money=(ComputeDefault.rightBelow.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }else  if(inPrizeList[i].inPrizType==NOTES_ENUM.QTDefaultTriple.num&& ComputeDefault.centreDate.moneyOkEmpty>0){
                var pressure=InPrizeBean()
                pressure.inPrizType=NOTES_ENUM.QTDefaultTriple.num
                pressure.money=(ComputeDefault.centreDate.moneyOkEmpty*2)
                pressureIn.add(pressure)

            }

        }

        //显示数据
        for (i in 0 until  pressureIn.size) {
            if(pressureIn[i].inPrizType==NOTES_ENUM.QTDefaultSmall.num){
                //计算钱
                showLeftTopMoney.setShowMoney(pressureIn[i].money)
                showLeftTopMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showLeftTopMoney, params)
                showLeftTopMoney.translationX =  ComputeDefault.leftTop.viewXYLast[0].toFloat()-requireContext().dp2px(30)
                showLeftTopMoney.translationY =  ComputeDefault.leftTop.viewXYLast[1].toFloat()-requireContext().dp2px(52)

            }else  if(pressureIn[i].inPrizType==NOTES_ENUM.QTDefaultBig.num){
                //计算钱
                showRightTopMoney.setShowMoney(pressureIn[i].money)
                showRightTopMoney.hiddenTop()
                // 动态添加的视图未成功添加到布局中
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showRightTopMoney, params)
                showRightTopMoney.translationX =  ComputeDefault.rightTop.viewXYLast[0].toFloat()-requireContext().dp2px(30)
                showRightTopMoney.translationY =  ComputeDefault.rightTop.viewXYLast[1].toFloat()-requireContext().dp2px(52)

            }else  if(pressureIn[i].inPrizType==NOTES_ENUM.QTDefaultSingle.num){
                //计算钱
                showRightBelowMoney.setShowMoney(pressureIn[i].money)
                showRightBelowMoney.hiddenTop()
                // 动态添加的视图未成功添加到布局中
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showRightBelowMoney, params)
                showRightBelowMoney.translationX =  ComputeDefault.rightBelow.viewXYLast[0].toFloat()-requireContext().dp2px(30)
                showRightBelowMoney.translationY =  ComputeDefault.rightBelow.viewXYLast[1].toFloat()+requireContext().dp2px(35)

            }else  if(pressureIn[i].inPrizType==NOTES_ENUM.QTDefaultDouble.num){
                //计算钱
                showRightBelowMoney.setShowMoney(pressureIn[i].money)
                showRightBelowMoney.hiddenTop()
                // 动态添加的视图未成功添加到布局中
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlHomeRoot.addView(showRightBelowMoney, params)
                showRightBelowMoney.translationX =  ComputeDefault.rightBelow.viewXYLast[0].toFloat()-requireContext().dp2px(30)
                showRightBelowMoney.translationY =  ComputeDefault.rightBelow.viewXYLast[1].toFloat()+requireContext().dp2px(35)

            }else  if(pressureIn[i].inPrizType==NOTES_ENUM.QTDefaultTriple.num){
                //计算钱
                showCentreDateMoney.setShowMoney(pressureIn[i].money)
                showCentreDateMoney.hiddenTop()
                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDatabind.rlClickCentre.addView(showCentreDateMoney, params)
                // 将新按钮设置为居中
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                showCentreDateMoney.layoutParams = params
            }


        }

        deletePreviousRound()


    }

    /**
     * 得到开奖信息后删除所有的在界面上的注码
     */
    fun closeBetting(){
        if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showLeftTopMoney)
        }
        if (mDatabind.rlHomeRoot.indexOfChild(showRightTopMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showRightTopMoney)
        }

        if (mDatabind.rlHomeRoot.indexOfChild(showLeftBelowMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showLeftBelowMoney)
        }

        if (mDatabind.rlHomeRoot.indexOfChild(showRightBelowMoney) != -1) {
            mDatabind.rlHomeRoot.removeView(showRightBelowMoney)
        }
        if (mDatabind.rlClickCentre.indexOfChild(showCentreDateMoney) != -1) {
            mDatabind.rlClickCentre.removeView(showCentreDateMoney)
        }


    }

    /**
     * 删除上一轮的数据
     */
    fun  deletePreviousRound(){
        ComputeDefault.leftTop.viewXYTemporary= intArrayOf(0, 0)
        ComputeDefault.leftTop.viewXYLast= intArrayOf(0, 0)
        ComputeDefault.leftTop.moneyTemporary= 0
        ComputeDefault.leftTop.moneyOkEmpty= 0

        ComputeDefault.rightTop.viewXYTemporary= intArrayOf(0, 0)
        ComputeDefault.rightTop.viewXYLast= intArrayOf(0, 0)
        ComputeDefault.rightTop.moneyTemporary= 0
        ComputeDefault.rightTop.moneyOkEmpty= 0


        ComputeDefault.leftBelow.viewXYTemporary= intArrayOf(0, 0)
        ComputeDefault.leftBelow.viewXYLast= intArrayOf(0, 0)
        ComputeDefault.leftBelow.moneyTemporary= 0
        ComputeDefault.leftBelow.moneyOkEmpty= 0


        ComputeDefault.rightBelow.viewXYTemporary= intArrayOf(0, 0)
        ComputeDefault.rightBelow.viewXYLast= intArrayOf(0, 0)
        ComputeDefault.rightBelow.moneyTemporary= 0
        ComputeDefault.rightBelow.moneyOkEmpty= 0

        ComputeDefault.centreDate.viewXYTemporary= intArrayOf(0, 0)
        ComputeDefault.centreDate.viewXYLast= intArrayOf(0, 0)
        ComputeDefault.centreDate.moneyTemporary= 0
        ComputeDefault.centreDate.moneyOkEmpty= 0

    }

    /**
     * 中奖区域闪烁的动画
     *isAnimation true  是执行动画   flase是取消动画
     */
    fun fadeOut(isAnimation:Boolean,inPrizeList:ArrayList<InPrizeBean> =ArrayList<InPrizeBean>()){
        if(isAnimation){
            inPrizeList.forEach {
                if(it.inPrizType==NOTES_ENUM.QTDefaultSmall.num){
                    mDatabind.ivFlickerLeftTop.visibility=View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivFlickerLeftTop, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDefaultBig.num){
                    mDatabind.ivFlickerRightTop.visibility=View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivFlickerRightTop, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDefaultSingle.num){
                    mDatabind.ivFlickerLeftBelow.visibility=View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivFlickerLeftBelow, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDefaultDouble.num){
                    mDatabind.ivFlickerRightBelow.visibility=View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivFlickerRightBelow, "alpha", 1f, 0f, 1f).apply {
                        duration = 400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })

                }else  if(it.inPrizType==NOTES_ENUM.QTDefaultTriple.num){
                    mDatabind.ivFlickerCenter.visibility=View.VISIBLE
                    animators.add(ObjectAnimator.ofFloat(mDatabind.ivFlickerCenter, "alpha", 1f, 0f, 1f).apply {
                        duration =400 // 设置动画持续时间
                        repeatCount = ObjectAnimator.INFINITE // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    })
                }
            }

            // 开始动画
            animators.forEach { it.start() }

        }else{
            mDatabind.ivFlickerLeftTop.visibility=View.GONE
            mDatabind.ivFlickerRightTop.visibility=View.GONE
            mDatabind.ivFlickerLeftBelow.visibility=View.GONE
            mDatabind.ivFlickerRightBelow.visibility=View.GONE
            mDatabind.ivFlickerCenter.visibility=View.GONE
            animators.forEach { it.cancel() }
            animators.clear()
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
