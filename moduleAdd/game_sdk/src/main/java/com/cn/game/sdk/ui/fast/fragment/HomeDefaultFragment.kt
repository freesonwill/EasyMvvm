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
import com.cn.game.sdk.view.MoneyOKDeleteView
import com.cn.game.sdk.view.MoneyOKView
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
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
    lateinit var showRightTopMoney: MoneyOKDeleteView

    /**
     * 左下角注区的控件确定
     */
    lateinit var showLeftBelowMoney: MoneyOKDeleteView

    /**
     * 右下角注区的控件确定
     */
    lateinit var showRightBelowMoney: MoneyOKDeleteView

    /**
     * 中间注区
     */
    lateinit var showCentreDateMoney: MoneyOKDeleteView



    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt("type")
        }
        //初始化左上角
        showLeftTopMoney=MoneyOKView(requireContext())
        showLeftTopMoney.setMoneyOKClickListener(object :MoneyOKView.OnMoneyOKClickListener{
            override fun onDelete() {
                if(ComputeDefault.leftTop.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
                        mDatabind.rlHomeRoot.removeView(showLeftTopMoney)
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

        //初始化右上角
        showRightTopMoney=MoneyOKDeleteView(requireContext())
        showRightTopMoney.setMoneyOKClickListener(object :MoneyOKDeleteView.MoneyOKDeleteClickListener{
            override fun onDelete() {
//                ComputeDefault.rightTop.moneyOkEmpty=20
                if(ComputeDefault.rightTop.moneyOkEmpty<=0){
                    //判断控件是否加入了
                    if (mDatabind.rlClickRightTop.indexOfChild(showRightTopMoney) != -1) {
                        mDatabind.rlClickRightTop.removeView(showRightTopMoney)
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

        //左下
        showLeftBelowMoney=MoneyOKDeleteView(requireContext())




        //右下
        showRightBelowMoney=MoneyOKDeleteView(requireContext())


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
            if( (context as GameHomeActivity).isCanBetting()){
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
                showCentreDateMoney.setShowMoney(ComputeDefault.centreDate.moneyTemporary+ComputeDefault.centreDate.moneyOkEmpty)

                //动画位置
                if (mDatabind.rlClickCentre.indexOfChild(showCentreDateMoney) != -1) {
                    //显示在屏幕的绝对位置,动画的位置
                    if(ComputeDefault.centreDate.screenXYTemporary[0]!=0&&ComputeDefault.centreDate.screenXYTemporary[1]!=0){
                        (context as GameHomeActivity).startAnimation(ComputeDefault.centreDate.screenXYTemporary[0].toFloat(),ComputeDefault.centreDate.screenXYTemporary[1].toFloat())
                    }

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

                            ComputeDefault.centreDate.screenXYTemporary[0]=xOnScreen
                            ComputeDefault. centreDate.screenXYTemporary[1]=yOnScreen+dp2px(context,47f)

                            (context as GameHomeActivity).startAnimation(ComputeDefault.centreDate.screenXYTemporary[0].toFloat(),ComputeDefault.centreDate.screenXYTemporary[1].toFloat())
                            //显示点击在Fragment的位置用于动画结束后显示
                            if(ComputeDefault.centreDate.viewXYTemporary[0]==0&&ComputeDefault.centreDate.viewXYTemporary[1]==0){
                                val location = IntArray(2)
                                showCentreDateMoney.getLocationInWindow(location)

                                ComputeDefault.centreDate.viewXYTemporary[0]= showCentreDateMoney.left
                                ComputeDefault.centreDate.viewXYTemporary[1]=showCentreDateMoney.top
                            }
                        }
                    })
                    if(ComputeDefault.centreDate.viewXYTemporary[0]!=0&&ComputeDefault.centreDate.viewXYTemporary[1]!=0){
                        val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        params.leftMargin= ComputeDefault.centreDate.viewXYTemporary[0]
                        params.topMargin= ComputeDefault.centreDate.viewXYTemporary[1]
                        showCentreDateMoney.layoutParams = params
                        mDatabind.rlClickCentre.addView(showCentreDateMoney)
                    }else{
                        val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        mDatabind.rlClickCentre.addView(showCentreDateMoney, params)
                        // 将新按钮设置为居中
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        showCentreDateMoney.layoutParams = params
                    }

                }

            }



        }

    }

    /**
     * 点击左上角
     */
    @SuppressLint("ClickableViewAccessibility")
    fun  clickLeftTop(){
        mDatabind.rlClick.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //先判断余额是否够这次
                    if( (context as GameHomeActivity).isCanBetting()){
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
                            //当前点击的这个注区头部显示出来
                            showLeftTopMoney.showTop()
                            //点击每个模块的动画，隐藏没点击的所有的头部
                            clickAnimationIsHidden(0)

                        }

                }



                }
            }
            false // 返回 true 表示事件已经被处理
        }



    }

    /**
     * 点击右上角
     */
    @SuppressLint("ClickableViewAccessibility")
    fun  clickRightTop(){
        mDatabind.rlClickRightTop.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //先判断余额是否够这次
                    if( (context as GameHomeActivity).isCanBetting()){
                        //这样也要设置续压

                        val location = IntArray(2)
                        mDatabind.rlClickRightTop.getLocationOnScreen(location)
                        //========
                        var selectNum=0
                        for (i in 0 until   MyGameManager.noteList.size) {
                            if(MyGameManager.noteList[i].select){
                                selectNum=i
                                break
                            }
                        }
                        //计算钱以及显示的东西
                        clickAnimationIsHidden(1)
                        showRightTopMoney.showTop()
                        ComputeDefault.rightTop.moneyTemporary= ComputeDefault.rightTop.moneyTemporary+MyGameManager.noteList[selectNum].money
                        showRightTopMoney.setShowMoney(ComputeDefault.rightTop.moneyTemporary+ComputeDefault.rightTop.moneyOkEmpty)


                        if (mDatabind.rlClickRightTop.indexOfChild(showRightTopMoney) != -1) {
                            //显示在屏幕的绝对位置,动画的位置
                            if(ComputeDefault.rightTop.screenXYTemporary[0]!=0&&ComputeDefault.rightTop.screenXYTemporary[1]!=0){
                                (context as GameHomeActivity).startAnimation(ComputeDefault.rightTop.screenXYTemporary[0].toFloat(),ComputeDefault.rightTop.screenXYTemporary[1].toFloat())
                            }

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

                                    ComputeDefault.rightTop.screenXYTemporary[0]=xOnScreen
                                    ComputeDefault. rightTop.screenXYTemporary[1]=yOnScreen+dp2px(context,47f)


                                    (context as GameHomeActivity).startAnimation(ComputeDefault.rightTop.screenXYTemporary[0].toFloat(),ComputeDefault.rightTop.screenXYTemporary[1].toFloat())
                                    //显示点击在Fragment的位置用于动画结束后显示
                                    if(ComputeDefault.rightTop.viewXYTemporary[0]==0&&ComputeDefault.rightTop.viewXYTemporary[1]==0){
                                        val location = IntArray(2)
                                        showRightTopMoney.getLocationInWindow(location)

                                        ComputeDefault.rightTop.viewXYTemporary[0]= showRightTopMoney.left
                                        ComputeDefault.rightTop.viewXYTemporary[1]=showRightTopMoney.top
                                    }
                                }
                            })
                            if(ComputeDefault.rightTop.viewXYTemporary[0]!=0&&ComputeDefault.rightTop.viewXYTemporary[1]!=0){
                                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                params.leftMargin= ComputeDefault.rightTop.viewXYTemporary[0]
                                params.topMargin= ComputeDefault.rightTop.viewXYTemporary[1]
                                showRightTopMoney.layoutParams = params
                                mDatabind.rlClickRightTop.addView(showRightTopMoney)
                            }else{
                                val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                mDatabind.rlClickRightTop.addView(showRightTopMoney, params)
                                // 将新按钮设置为居中
                                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                                showRightTopMoney.layoutParams = params
                            }

                        }

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
        if(ComputeDefault.leftTop.viewXYTemporary [0]==0&&ComputeDefault.leftTop.viewXYTemporary[1]==0){
            ComputeDefault.leftTop.viewXYTemporary[0]=x.toInt()
            ComputeDefault.leftTop.viewXYTemporary[1]=y.toInt()
        }
        //显示在屏幕的绝对位置,动画的位置
        if(ComputeDefault.leftTop.screenXYTemporary[0]==0&&ComputeDefault.leftTop.screenXYTemporary[1]==0){
            ComputeDefault.leftTop.screenXYTemporary[0]=rax.toInt()
            ComputeDefault. leftTop.screenXYTemporary[1]=ray.toInt()
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
        showLeftTopMoney.setShowMoney(ComputeDefault.leftTop.moneyTemporary+ComputeDefault.leftTop.moneyOkEmpty)
        //判断是否添加上去了这个viwe
        if (mDatabind.rlHomeRoot.indexOfChild(showLeftTopMoney) != -1) {
        } else {
            // 动态添加的视图未成功添加到布局中
            val params = RelativeLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDatabind.rlHomeRoot.addView(showLeftTopMoney, params)
            showLeftTopMoney.translationX =  ComputeDefault.leftTop.viewXYTemporary[0].toFloat()-requireContext().dp2px(30)
            showLeftTopMoney.translationY =  ComputeDefault.leftTop.viewXYTemporary[1].toFloat()-requireContext().dp2px(52)

        }
        (context as GameHomeActivity).startAnimation(ComputeDefault.leftTop.screenXYTemporary[0].toFloat(),ComputeDefault.leftTop.screenXYTemporary[1].toFloat(),true)

   }


    /**
     * 点击每个模块的动画，隐藏没点击的所有的头部
     * 0是左上  1是右上   2是左下  3是右下  4是中间
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
     * 删除投注  就保留上一次的确定的钱,isEmpty是否清空临时的钱，如果是点击的叉叉就要清空~~如果是勾勾就不用清除
     */
    fun deleteBet(isEmpty:Boolean=true){
        if(isEmpty){
            ComputeDefault.leftTop.moneyTemporary=0
            ComputeDefault.rightTop.moneyTemporary=0
            ComputeDefault.leftBelow.moneyTemporary=0
            ComputeDefault.rightBelow.moneyTemporary=0
            ComputeDefault.centreDate.moneyTemporary=0
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
                if (mDatabind.rlClickRightTop.indexOfChild(showRightTopMoney) != -1) {
                    mDatabind.rlClickRightTop.removeView(showRightTopMoney)
                }
            }else{
                showRightTopMoney.hiddenTop()
            }



        //左下角确定的钱没有
            if(ComputeDefault.leftBelow.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlClickLeftBelow.indexOfChild(showLeftBelowMoney) != -1) {
                    mDatabind.rlClickLeftBelow.removeView(showLeftBelowMoney)
                }
            }else{
                showLeftBelowMoney.hiddenTop()
            }

        //右下角确定的钱没有
            if(ComputeDefault.rightBelow.moneyOkEmpty<=0){
                //判断控件是否加入了
                if (mDatabind.rlClickRightBelow.indexOfChild(showRightBelowMoney) != -1) {
                    mDatabind.rlClickRightBelow.removeView(showRightBelowMoney)
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












}
