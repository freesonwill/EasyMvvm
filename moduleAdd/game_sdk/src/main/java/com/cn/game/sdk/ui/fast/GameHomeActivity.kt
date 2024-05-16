package com.cn.game.sdk.ui.fast


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk.R
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameActivity
import com.cn.game.sdk.bean.BettingRecordBean
import com.cn.game.sdk.bean.ComputeDefault
import com.cn.game.sdk.bean.ComputeLeopard
import com.cn.game.sdk.bean.ComputePairs
import com.cn.game.sdk.bean.ComputeSingle
import com.cn.game.sdk.bean.ComputeSum
import com.cn.game.sdk.bean.HistoryResultBean
import com.cn.game.sdk.bean.InPrizeBean
import com.cn.game.sdk.bean.SelectAnnotationBean
import com.cn.game.sdk.databinding.ActivityGameHomeBinding
import com.cn.game.sdk.databinding.ItemAnnotationListBinding
import com.cn.game.sdk.databinding.ItemBetHistoryBinding
import com.cn.game.sdk.listener.GameTimeStatic
import com.cn.game.sdk.popup.CustomBubbleAttachPopup
import com.cn.game.sdk.tool.bindViewPagerNew
import com.cn.game.sdk.tool.init
import com.cn.game.sdk.ui.fast.fragment.HomeDefaultFragment
import com.cn.game.sdk.ui.fast.fragment.LeopardFragment
import com.cn.game.sdk.ui.fast.fragment.PairsDiceFragment
import com.cn.game.sdk.ui.fast.fragment.SingleDiceFragment
import com.cn.game.sdk.ui.fast.fragment.SumTotalFragment
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.utils.addCommas
import com.cn.game.sdk.utils.rewritingTouch
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat


class GameHomeActivity : BaseGameActivity<GameHomeVm, ActivityGameHomeBinding>() {
    private var mFragList = ArrayList<Fragment>()
    //默认
    var homeDefaultFragment = HomeDefaultFragment()
    //单骰
    var singleDiceFragment = SingleDiceFragment()
    //总和
    var sumTotalFragment = SumTotalFragment()
    //对子
    var pairsDiceFragment = PairsDiceFragment()
    //豹子
    var leopardFragment = LeopardFragment()
    //是否执行关闭动画
    var isExecuteClose:Boolean=true


    // 定义属性动画常量
    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.4f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.4f, 1.0f)

    /**
     * 是否显示骰子的结果组合
     */
    private var isShowResult:Boolean=true



    var popup: BasePopupView?=null
    var bubbleAttach : CustomBubbleAttachPopup?=null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        MyGameManager.static=1
        MyGameManager.countdownTime=10000

        //停止
        mDatabind.tingzhi.setOnClickListener {
            MyGameManager.isActive=true
            MyGameManager.countDownTimer!!.cancel()
//            MyGameManager.countdownTime=0
        }

        mDatabind.kaishi.setOnClickListener {
//            MyGameManager.countDownTimer.start()
            MyGameManager.staCountDownTimer()

        }

        MyGameManager.setLiveStatusListener(this.toString(),object : GameTimeStatic{
            override fun onCountdown(time: Long) {
                super.onCountdown(time)
                val seconds: Long = Math.round(time.toDouble() / 1000)

                mDatabind.txtHomeTime.text=seconds.toString()


            }

            override fun onStatic(mStatic: Int) {
                super.onStatic(mStatic)
                mDatabind.txtHomeTime.text="0"
                MyGameManager.countdownTime=10000
                MyGameManager.countDownTimer!!.start()
//                MyGameManager.staCountDownTimer()
                //1是下注   2结算
                if(mStatic==2){
                    //显示开奖结果
                    hiddenView()
                    mDatabind.rlShowResult.visibility=View.VISIBLE
                    mDatabind.llShowBetList.visibility=View.INVISIBLE

                    mDatabind.txtHomeTime.visibility=View.GONE
                    mDatabind.txtHomeUnit.visibility=View.GONE
                    mDatabind.txtHomeStatic.text=resources.getString(R.string.g_home_balance)
                    //结算的时候要把每个模块中奖的信息显示出来 默认
                    homeDefaultFragment.flicker(ArrayList<InPrizeBean>(),ArrayList<InPrizeBean>())
                    singleDiceFragment.flicker(ArrayList<InPrizeBean>(),ArrayList<InPrizeBean>())
                    pairsDiceFragment.flicker(ArrayList<InPrizeBean>(),ArrayList<InPrizeBean>())
                    leopardFragment.flicker(ArrayList<InPrizeBean>(),ArrayList<InPrizeBean>())

                    sumTotalFragment.flicker(ArrayList<InPrizeBean>(),ArrayList<InPrizeBean>())
                }else{
                    //显示开奖结果
                    hiddenView()
                    mDatabind.rlShowResult.visibility=View.GONE
                    mDatabind.llShowBetList.visibility=View.VISIBLE

                    //新的下注要删除所有的
                    MyGameManager.isClickOperation=true
                    mDatabind.txtHomeStatic.text=resources.getString(R.string.g_home_txt_please)
                    mDatabind.txtHomeTime.visibility=View.VISIBLE
                    mDatabind.txtHomeUnit.visibility=View.VISIBLE

                    //默认
                    homeDefaultFragment.closeBetting()
                    homeDefaultFragment.fadeOut(false,ArrayList<InPrizeBean>())
                    //单筛子
                    singleDiceFragment.closeBetting()
                    singleDiceFragment.fadeOut(false,ArrayList<InPrizeBean>())
                    //对子
                    pairsDiceFragment.closeBetting()
                    pairsDiceFragment.fadeOut(false,ArrayList<InPrizeBean>())
                    //豹子
                    leopardFragment.closeBetting()
                    leopardFragment.fadeOut(false,ArrayList<InPrizeBean>())

                    //豹子
                    sumTotalFragment.closeBetting()
                    sumTotalFragment.fadeOut(false,ArrayList<InPrizeBean>())

                }

            }
        })
        MyGameManager.staCountDownTimer()
//        MyGameManager.countDownTimer.start()

        // 获取屏幕的高度
        val screenHeight = resources.displayMetrics.heightPixels

        // 设置动画的起始值和结束值（百分比）
        val startPercentage = 1f // 从屏幕底部开始（百分之一处）
        val endPercentage = 0f // 移动到屏幕顶部（百分之百处）

        // 将百分比转换为实际像素值
        val startY = screenHeight * startPercentage
        val endY = screenHeight * endPercentage
        // 设置进入动画
        val enterAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rlRoot), "translationY", startY, endY)
        enterAnimator.duration =1700

        // 启动进入动画
        enterAnimator.start()

        supportActionBar?.hide()
        // 设置状态栏颜色为透明getColor(android.R.color.transparent)
        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
          homeDefaultFragment = HomeDefaultFragment()

        val basketball = Bundle().apply {
            putInt("type",0)
        }

//        GameData.getInstance().rootView = mDatabind.tempTouth




        mDatabind.ivHomeLogo.clickNoRepeat {

        }
        appGameViewModel.ceshEvent.observe(this){
        }
        homeDefaultFragment.arguments = basketball


        mFragList.add(homeDefaultFragment)
        mFragList.add(singleDiceFragment)

        mFragList.add(pairsDiceFragment)
        mFragList.add(leopardFragment)
        mFragList.add(sumTotalFragment)
        mDatabind.viewPager.init(supportFragmentManager,mFragList,arrayListOf(
            getString(R.string.g_home_txt_default),
            getString(R.string.g_home_tab_single),
            getString(R.string.g_home_tab_double),
            getString(R.string.g_home_tab_leopard),
            getString(R.string.g_home_tab_sum)))
//        mDatabind.viewPager.offscreenPageLimit =mFragList.size

        mDatabind.magicIndicator.bindViewPagerNew(mDatabind.viewPager,arrayListOf(
            getString(R.string.g_home_txt_default),
            getString(R.string.g_home_tab_single),
            getString(R.string.g_home_tab_double),
            getString(R.string.g_home_tab_leopard),
            getString(R.string.g_home_tab_sum)),scrollEnable=true){

        }
        mDatabind.viewPager.offscreenPageLimit = mFragList.size


        mDatabind.txtHomeDefault.setOnClickListener {
            select(0)
            false
        }
        mDatabind.txtHomeSingle.setOnClickListener {
            select(1)
            false
        }
        mDatabind.txtHomeSum.setOnClickListener {
            select(2)
            false
        }

        mDatabind.txtHomeDouble.setOnClickListener {
            select(3)
            false
        }
        mDatabind.txtHomeLeopard.setOnClickListener {
            select(4)
            false
        }


        adapter()
        setClick()
        bubbleAttach=CustomBubbleAttachPopup(this)
        bubbleAttach!!.customBubbleAttachListener=object :CustomBubbleAttachPopup.CustomBubbleAttachListener{
            override fun switchGame() {
                popup!!.dismiss()
            }

        }

        popup= XPopup.Builder(this)
            .hasShadowBg(false)
            .isTouchThrough(true)
            .atView(mDatabind.llHomeMore)
            .hasShadowBg(false) // 去掉半透明背景
            .asCustom(bubbleAttach)
        //点击更多弹出框
         mDatabind.llHomeMore.clickNoRepeat {
//             XPopup.Builder(this)
//                 .hasShadowBg(false)
//                 .isTouchThrough(true)
//                 .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                 .atView(mDatabind.llHomeMore)
//                 .hasShadowBg(false) // 去掉半透明背景
//                 .asCustom(CustomBubbleAttachPopup(this))
//                 .show()
             popup!!.show()



         }
        //获取当前余额
        mDatabind.txtCurrentMoney.text= MyGameManager.currentMoney.addCommas()
        rewritingTouch(tempTouth=mDatabind.tempTouth,viewPager=mDatabind.viewPager,homeDefaultFragment=homeDefaultFragment,
            singleDiceFragment=singleDiceFragment,sumTotalFragment=sumTotalFragment,pairsDiceFragment=pairsDiceFragment,
            leopardFragment=leopardFragment)





    }


    fun isTouchInsideView(view: View, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val viewX = location[0]
        val viewY = location[1]
        val viewWidth = view.width
        val viewHeight = view.height

        return x >= viewX && x <= viewX + viewWidth && y >= viewY && y <= viewY + viewHeight
    }


    private val initialUpperLayoutHeightMap = mutableMapOf<Int, Int>()
    private var initia = 0
    var isAdd:Boolean=true
    fun setClick(){


        mDatabind.rlClickHide.clickNoRepeat {

//            if(MyGameManager.isClickOperation){
//                resultAnimation()
//            }
            resultAnimation()
        }
    }


    /**
     * 关闭页面
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 判断是否按下了返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //执行了一次就不能执行了
            if(isExecuteClose){
                //关闭的时候要把这个赋值为0选择
                MyGameManager.noteList.forEach {
                    it.select=false

                }
                MyGameManager.noteList[0].select=true
                //清空临时的
                clickDelete()
                //======

                // 在这里执行你想要的操作，比如关闭当前活动
                // 获取屏幕的高度
                val screenHeight = resources.displayMetrics.heightPixels

                    // 设置动画的起始值和结束值（百分比）
                val startPercentage = 1f // 从屏幕底部开始（百分之百处）
                val endPercentage = 0f // 移动到屏幕顶部（百分之零处）

                // 将百分比转换为实际像素值
                val startY = screenHeight * startPercentage
                val endY = screenHeight * endPercentage
                  //            val exitAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rlRoot), "translationY", 0f, 1000f)
                val exitAnimator = ObjectAnimator.ofFloat(findViewById(R.id.rlRoot), "translationY", endY,startY )
                    //            exitAnimator.interpolator = AccelerateInterpolator()
                exitAnimator.duration = 1000
                // 添加动画监听器
                exitAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // 在动画结束时调用 finish() 方法关闭 Activity
                        finish()
                        homeDefaultFragment.closeActivity()
                        singleDiceFragment.closeActivity()
                        pairsDiceFragment.closeActivity()
                        leopardFragment.closeActivity()
                        sumTotalFragment.closeActivity()
                    }
                })
                // 启动退出动画
                exitAnimator.start()
            MyGameManager.showFastThreeView()
                isExecuteClose=false
            }



//            finish()
//            overridePendingTransition(R.anim.slide_up,  R.anim.slide_down)
            return true  // 返回 true 表示事件已经处理，不会继续传递
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun createObserver() {
        super.createObserver()

    }

    /**
     * 投注的适配器
     */
    fun adapter() {
        mDatabind.llShowBetList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mDatabind.llShowBetList.setup {
            addType<SelectAnnotationBean>(R.layout.item_annotation_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_annotation_list -> {
                        var binding=getBinding<ItemAnnotationListBinding>()
                        val bean = _data as SelectAnnotationBean

                        if(layoutPosition==0){
                            if(MyGameManager.temporaryCurrentMoney<10){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_shi))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_shi))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_shi))
                                }
                            }
                        }else if(layoutPosition==1){
                            if(MyGameManager.temporaryCurrentMoney<50){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_wushi))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_wushi))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wushi))
                                }
                            }
                        }else if(layoutPosition==2){
                            if(MyGameManager.temporaryCurrentMoney<100){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_yibai))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_yibai))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_yibai))
                                }
                            }

                        }else if(layoutPosition==3){

                            if(MyGameManager.temporaryCurrentMoney<200){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_liangbai))
                            }else{

                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_liangbai))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_liangbai))
                                }
                            }
                        }else if(layoutPosition==4){
                            if(MyGameManager.temporaryCurrentMoney<500){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_wubai))
                            }else{

                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_wubai))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wubai))
                                }
                            }
                        }else if(layoutPosition==5){
                            if(MyGameManager.temporaryCurrentMoney<1000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_qian))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_qian))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_qian))
                                }
                            }

                        }else if(layoutPosition==6){

                            if(MyGameManager.temporaryCurrentMoney<2000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_liangqian))
                            }else{

                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_liangqian))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_liangqian))
                                }

                            }
                        }else if(layoutPosition==7){

                            if(MyGameManager.temporaryCurrentMoney<5000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_wuqian))
                            }else{

                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_wuqian))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wuqian))
                                }
                            }
                        }else if(layoutPosition==8){
                            if(MyGameManager.temporaryCurrentMoney<10000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_yiwan))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_yiwan))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_yiwan))
                                }
                            }

                        }else if(layoutPosition==9){
                            if(MyGameManager.temporaryCurrentMoney<20000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_liangwan))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_liangwan))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_liangwan))
                                }
                            }
                        }else if(layoutPosition==10){

                            if(MyGameManager.temporaryCurrentMoney<50000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_wuwan))
                            }else{
                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_wuwan))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wuwan))
                                }

                            }
                        }else{
                            if(MyGameManager.temporaryCurrentMoney<100000){
                                binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_shortage_shiwan))
                            }else{

                                if(bean.select){
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_select_shiwan))
                                }else{
                                    binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_shiwan))
                                }
                            }

                        }
                    }

                }

            }
            R.id.ivShowBg.onClick {
                //选择新的筹码
                var binding=getBinding<ItemAnnotationListBinding>()
                for (i in 0 until  mDatabind.llShowBetList.models!!.size) {
                    (mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).select=false
                    MyGameManager.noteList[i].select=false

                }
                (mDatabind.llShowBetList.models!![modelPosition] as SelectAnnotationBean).select=true
                notifyDataSetChanged()
                MyGameManager.noteList[modelPosition].select=true

            }

        }.addModels(MyGameManager.noteList)
        var list=ArrayList<HistoryResultBean>()
        for (c in 0 until 20) {
            list.add(HistoryResultBean())
        }
        //历史结果
        mDatabind.rvHomeHistory.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mDatabind.rvHomeHistory.setup {

            addType<HistoryResultBean>(R.layout.item_bet_history)

            onBind {
                when (itemViewType) {
                    R.layout.item_bet_history -> {
                        var binding=getBinding<ItemBetHistoryBinding>()
                        var mainTxtBean=_data as HistoryResultBean

                        if(mainTxtBean.isShow){
                            binding.llShowDice.visibility=View.VISIBLE
                            // 获取并保存初始的上半部分布局高度
                            if (!initialUpperLayoutHeightMap.containsKey(modelPosition)) {
                                binding.llShowDice.post {
                                    val initialHeight =   binding.llShowDice.height
                                    initialUpperLayoutHeightMap[modelPosition] = initialHeight
                                }
                            }
                        }else{
                            binding.llShowDice.visibility=View.GONE
                        }

                    }


                }


            }


        }.addModels(list)

        mDatabind.rvHomeHistory.postDelayed({
            mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size-1)
        }, 200)



    }

    /**
     * 开奖结果隐藏不要的控件
     */
    fun  hiddenView(){
        if(isShowResult){
            resultAnimation()
        }


    }

    /**
     * 开奖结果显示或者隐藏动画
     */
    fun  resultAnimation(){
        if(isShowResult){
            mDatabind.ivHomeRotation.rotation=180f
            isShowResult=!isShowResult
            for (i in 0 until  mDatabind.rvHomeHistory.models!!.size) {
                var viewHolder=  mDatabind.rvHomeHistory.findViewHolderForLayoutPosition(i)
                if(viewHolder!=null){
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow=false
                    var  llShowDice= viewHolder!!.itemView.findViewById<LinearLayout>(R.id.llShowDice)
                    //llShowDice.height.toFloat()高度是205
                    if(isAdd){
                        initia=llShowDice.height
                        isAdd=false
                    }
                    val anim = ObjectAnimator.ofFloat(llShowDice, "translationY", 0f, llShowDice.height.toFloat())
                    anim.duration = 500 // 设置动画持续时间
                    anim.start()
                    // 动画结束后隐藏上半部分布局
                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator ) {
                            super.onAnimationEnd(animation)
                            llShowDice.visibility = View.GONE
                        }
                    })
                }else{
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow=false
                    mDatabind.rvHomeHistory.bindingAdapter.notifyItemChanged(i)
                }


            }
        }else{
            mDatabind.ivHomeRotation.rotation=0f
            isShowResult=!isShowResult
            for (i in 0 until  mDatabind.rvHomeHistory.models!!.size) {
                var viewHolder=  mDatabind.rvHomeHistory.findViewHolderForLayoutPosition(i)
                if(viewHolder!=null){
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow=true
                    var  llShowDice= viewHolder!!.itemView.findViewById<LinearLayout>(R.id.llShowDice)
                    llShowDice.visibility = View.VISIBLE
                    llShowDice.translationY =initia.toFloat()

                    // 创建动画，将视图向上平移显示
                    val anim = ObjectAnimator.ofFloat(llShowDice, "translationY", initia.toFloat(), 0f)
                    anim.duration = 500 // 设置动画持续时间
                    anim.start()
//                         动画结束后显示上半部分布局
                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)

                        }
                    })



                }else{
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow=true
                }


            }
            mDatabind.rvHomeHistory.postDelayed({
                mDatabind.rvHomeHistory.bindingAdapter.notifyDataSetChanged()
            }, 600)

        }
    }

    /**
     *重点 点击了某个页面的叉叉会调用此方法，要把所有的页面的临时钱清空并且只显示确定了的钱的图片
     * 通知所有子页面都要进行
     */
    fun clickDelete(){
            //默认
            //恢复实际的钱
            MyGameManager.temporaryCurrentMoney=MyGameManager.currentMoney
            homeDefaultFragment.deleteBet()
            //删除后要设置钱
            homeDefaultFragment.setAllShowViewMoney()

             //单筛
            singleDiceFragment.deleteBet()
             //删除后要设置钱
             singleDiceFragment.setAllShowViewMoney()


            //对子
            pairsDiceFragment.deleteBet()
            //删除后要设置钱
            pairsDiceFragment.setAllShowViewMoney()


        //豹子
        leopardFragment.deleteBet()
        //删除后要设置钱
        leopardFragment.setAllShowViewMoney()

        //总和
        sumTotalFragment.deleteBet()
        //删除后要设置钱
        sumTotalFragment.setAllShowViewMoney()

            //刷新投注区适配器
       mDatabind.llShowBetList.adapter!!.notifyDataSetChanged()



    }

    /**
     * 重点 点击勾勾投注确定会调用此方法，要把所有的子页面的临时钱都要投注然后清空
     */
    fun clickOKBet(){
        //默认的 ============
        //点击确定后，购买成功后要把临时的总金额传递给实际的
        MyGameManager.currentMoney= MyGameManager.temporaryCurrentMoney
        //获取当前余额
        mDatabind.txtCurrentMoney.text= MyGameManager.currentMoney.addCommas()
        //默认的  点击确定后加入然后清空临时的钱
        ComputeDefault.leftTop.moneyOkEmpty += ComputeDefault.leftTop.moneyTemporary
        ComputeDefault.leftTop.moneyTemporary=0
        ComputeDefault.rightTop.moneyOkEmpty+= ComputeDefault.rightTop.moneyTemporary
        ComputeDefault.rightTop.moneyTemporary=0
        ComputeDefault.leftBelow.moneyOkEmpty+= ComputeDefault.leftBelow.moneyTemporary
        ComputeDefault.leftBelow.moneyTemporary=0
        ComputeDefault.rightBelow.moneyOkEmpty+= ComputeDefault.rightBelow.moneyTemporary
        ComputeDefault.rightBelow.moneyTemporary=0
        ComputeDefault.centreDate.moneyOkEmpty+= ComputeDefault.centreDate.moneyTemporary
        ComputeDefault.centreDate.moneyTemporary=0
        //保存需要的钱
        ComputeDefault.leftTop.moneyOk= ComputeDefault.leftTop.moneyOkEmpty
        ComputeDefault.rightTop.moneyOk= ComputeDefault.rightTop.moneyOkEmpty
        ComputeDefault.leftBelow.moneyOk= ComputeDefault.leftBelow.moneyOkEmpty
        ComputeDefault.rightBelow.moneyOk= ComputeDefault.rightBelow.moneyOkEmpty
        ComputeDefault.centreDate.moneyOk= ComputeDefault.centreDate.moneyOkEmpty

        //点击确定的时候要更新一下动画位置
        if(ComputeDefault.leftTop.moneyOkEmpty>0){
            ComputeDefault.leftTop.viewXYLast= ComputeDefault.leftTop.viewXYTemporary
        }
        if(ComputeDefault.rightTop.moneyOkEmpty>0){
            ComputeDefault.rightTop.viewXYLast= ComputeDefault.rightTop.viewXYTemporary
        }
        if(ComputeDefault.leftBelow.moneyOkEmpty>0){
            ComputeDefault.leftBelow.viewXYLast= ComputeDefault.leftBelow.viewXYTemporary
        }
        if(ComputeDefault.rightBelow.moneyOkEmpty>0){
            ComputeDefault.rightBelow.viewXYLast= ComputeDefault.rightBelow.viewXYTemporary
        }
        if(ComputeDefault.centreDate.moneyOkEmpty>0){
            ComputeDefault.centreDate.viewXYLast= ComputeDefault.centreDate.viewXYTemporary
        }
        //设置一下最新的钱
        homeDefaultFragment.setAllShowViewMoney()
        //判断现在哪些要展示在注区
        homeDefaultFragment.deleteBet(false)


        //=============默认设置结束

       //单骰子============
        // 点击确定后加入然后清空临时的钱
        ComputeSingle.singleYi.moneyOkEmpty += ComputeSingle.singleYi.moneyTemporary
        ComputeSingle.singleYi.moneyTemporary=0
        ComputeSingle.singleEr.moneyOkEmpty += ComputeSingle.singleEr.moneyTemporary
        ComputeSingle.singleEr.moneyTemporary=0
        ComputeSingle.singleSan.moneyOkEmpty += ComputeSingle.singleSan.moneyTemporary
        ComputeSingle.singleSan.moneyTemporary=0
        ComputeSingle.singleSi.moneyOkEmpty += ComputeSingle.singleSi.moneyTemporary
        ComputeSingle.singleSi.moneyTemporary=0
        ComputeSingle.singleWu.moneyOkEmpty += ComputeSingle.singleWu.moneyTemporary
        ComputeSingle.singleWu.moneyTemporary=0
        ComputeSingle.singleLiu.moneyOkEmpty += ComputeSingle.singleLiu.moneyTemporary
        ComputeSingle.singleLiu.moneyTemporary=0
        //保存需要的钱
        ComputeSingle.singleYi.moneyOk= ComputeSingle.singleYi.moneyOkEmpty
        ComputeSingle.singleEr.moneyOk= ComputeSingle.singleEr.moneyOkEmpty
        ComputeSingle.singleSan.moneyOk= ComputeSingle.singleSan.moneyOkEmpty
        ComputeSingle.singleSi.moneyOk= ComputeSingle.singleSi.moneyOkEmpty
        ComputeSingle.singleWu.moneyOk= ComputeSingle.singleWu.moneyOkEmpty
        ComputeSingle.singleLiu.moneyOk= ComputeSingle.singleLiu.moneyOkEmpty
        //点击确定的时候要更新一下动画位置
        if(ComputeSingle.singleYi.moneyOkEmpty>0){
            ComputeSingle.singleYi.viewXYLast= ComputeSingle.singleYi.viewXYTemporary
        }
        if(ComputeSingle.singleEr.moneyOkEmpty>0){
            ComputeSingle.singleEr.viewXYLast= ComputeSingle.singleEr.viewXYTemporary
        }

        if(ComputeSingle.singleSan.moneyOkEmpty>0){
            ComputeSingle.singleSan.viewXYLast= ComputeSingle.singleSan.viewXYTemporary
        }

        if(ComputeSingle.singleSi.moneyOkEmpty>0){
            ComputeSingle.singleSi.viewXYLast= ComputeSingle.singleSi.viewXYTemporary
        }

        if(ComputeSingle.singleWu.moneyOkEmpty>0){
            ComputeSingle.singleWu.viewXYLast= ComputeSingle.singleWu.viewXYTemporary
        }

        if(ComputeSingle.singleLiu.moneyOkEmpty>0){
            ComputeSingle.singleLiu.viewXYLast= ComputeSingle.singleLiu.viewXYTemporary
        }
        //设置一下最新的钱
        singleDiceFragment.setAllShowViewMoney()
        //判断现在哪些要展示在注区
        singleDiceFragment.deleteBet(false)


        //设置对子
        // 点击确定后加入然后清空临时的钱
        ComputePairs.pairsYi.moneyOkEmpty += ComputePairs.pairsYi.moneyTemporary
        ComputePairs.pairsYi.moneyTemporary=0
        ComputePairs.pairsEr.moneyOkEmpty += ComputePairs.pairsEr.moneyTemporary
        ComputePairs.pairsEr.moneyTemporary=0
        ComputePairs.pairsSan.moneyOkEmpty += ComputePairs.pairsSan.moneyTemporary
        ComputePairs.pairsSan.moneyTemporary=0
        ComputePairs.pairsSi.moneyOkEmpty += ComputePairs.pairsSi.moneyTemporary
        ComputePairs.pairsSi.moneyTemporary=0
        ComputePairs.pairsWu.moneyOkEmpty += ComputePairs.pairsWu.moneyTemporary
        ComputePairs.pairsWu.moneyTemporary=0
        ComputePairs.pairsLiu.moneyOkEmpty += ComputePairs.pairsLiu.moneyTemporary
        ComputePairs.pairsLiu.moneyTemporary=0
        //保存需要的钱
        ComputePairs.pairsYi.moneyOk= ComputePairs.pairsYi.moneyOkEmpty
        ComputePairs.pairsEr.moneyOk= ComputePairs.pairsEr.moneyOkEmpty
        ComputePairs.pairsSan.moneyOk= ComputePairs.pairsSan.moneyOkEmpty
        ComputePairs.pairsSi.moneyOk= ComputePairs.pairsSi.moneyOkEmpty
        ComputePairs.pairsWu.moneyOk= ComputePairs.pairsWu.moneyOkEmpty
        ComputePairs.pairsLiu.moneyOk= ComputePairs.pairsLiu.moneyOkEmpty
        //点击确定的时候要更新一下动画位置
        if(ComputePairs.pairsYi.moneyOkEmpty>0){
            ComputePairs.pairsYi.viewXYLast= ComputePairs.pairsYi.viewXYTemporary
        }
        if(ComputePairs.pairsEr.moneyOkEmpty>0){
            ComputePairs.pairsEr.viewXYLast= ComputePairs.pairsEr.viewXYTemporary
        }

        if(ComputePairs.pairsSan.moneyOkEmpty>0){
            ComputePairs.pairsSan.viewXYLast= ComputePairs.pairsSan.viewXYTemporary
        }

        if(ComputePairs.pairsSi.moneyOkEmpty>0){
            ComputePairs.pairsSi.viewXYLast= ComputePairs.pairsSi.viewXYTemporary
        }

        if(ComputePairs.pairsWu.moneyOkEmpty>0){
            ComputePairs.pairsWu.viewXYLast= ComputePairs.pairsWu.viewXYTemporary
        }

        if(ComputePairs.pairsLiu.moneyOkEmpty>0){
            ComputePairs.pairsLiu.viewXYLast= ComputePairs.pairsLiu.viewXYTemporary
        }
        //设置一下最新的钱
        pairsDiceFragment.setAllShowViewMoney()
        //判断现在哪些要展示在注区
        pairsDiceFragment.deleteBet(false)



        //设置对子
        // 点击确定后加入然后清空临时的钱
        ComputeLeopard.leopardYi.moneyOkEmpty += ComputeLeopard.leopardYi.moneyTemporary
        ComputeLeopard.leopardYi.moneyTemporary=0
        ComputeLeopard.leopardEr.moneyOkEmpty += ComputeLeopard.leopardEr.moneyTemporary
        ComputeLeopard.leopardEr.moneyTemporary=0
        ComputeLeopard.leopardSan.moneyOkEmpty += ComputeLeopard.leopardSan.moneyTemporary
        ComputeLeopard.leopardSan.moneyTemporary=0
        ComputeLeopard.leopardSi.moneyOkEmpty += ComputeLeopard.leopardSi.moneyTemporary
        ComputeLeopard.leopardSi.moneyTemporary=0
        ComputeLeopard.leopardWu.moneyOkEmpty += ComputeLeopard.leopardWu.moneyTemporary
        ComputeLeopard.leopardWu.moneyTemporary=0
        ComputeLeopard.leopardLiu.moneyOkEmpty += ComputeLeopard.leopardLiu.moneyTemporary
        ComputeLeopard.leopardLiu.moneyTemporary=0
        //保存需要的钱
        ComputeLeopard.leopardYi.moneyOk= ComputeLeopard.leopardYi.moneyOkEmpty
        ComputeLeopard.leopardEr.moneyOk= ComputeLeopard.leopardEr.moneyOkEmpty
        ComputeLeopard.leopardSan.moneyOk= ComputeLeopard.leopardSan.moneyOkEmpty
        ComputeLeopard.leopardSi.moneyOk= ComputeLeopard.leopardSi.moneyOkEmpty
        ComputeLeopard.leopardWu.moneyOk= ComputeLeopard.leopardWu.moneyOkEmpty
        ComputeLeopard.leopardLiu.moneyOk= ComputeLeopard.leopardLiu.moneyOkEmpty
        //点击确定的时候要更新一下动画位置
        if(ComputeLeopard.leopardYi.moneyOkEmpty>0){
            ComputeLeopard.leopardYi.viewXYLast= ComputeLeopard.leopardYi.viewXYTemporary
        }
        if(ComputeLeopard.leopardEr.moneyOkEmpty>0){
            ComputeLeopard.leopardEr.viewXYLast= ComputeLeopard.leopardEr.viewXYTemporary
        }

        if(ComputeLeopard.leopardSan.moneyOkEmpty>0){
            ComputeLeopard.leopardSan.viewXYLast= ComputeLeopard.leopardSan.viewXYTemporary
        }

        if(ComputeLeopard.leopardSi.moneyOkEmpty>0){
            ComputeLeopard.leopardSi.viewXYLast= ComputeLeopard.leopardSi.viewXYTemporary
        }

        if(ComputeLeopard.leopardWu.moneyOkEmpty>0){
            ComputeLeopard.leopardWu.viewXYLast= ComputeLeopard.leopardWu.viewXYTemporary
        }

        if(ComputeLeopard.leopardLiu.moneyOkEmpty>0){
            ComputeLeopard.leopardLiu.viewXYLast= ComputeLeopard.leopardLiu.viewXYTemporary
        }
        //设置一下最新的钱
        leopardFragment.setAllShowViewMoney()
        //判断现在哪些要展示在注区
        leopardFragment.deleteBet(false)


        //总和
        // 点击确定后加入然后清空临时的钱
        ComputeSum.sumTotalSi.moneyOkEmpty += ComputeSum.sumTotalSi.moneyTemporary
        ComputeSum.sumTotalSi.moneyTemporary=0
        ComputeSum.sumTotalWu.moneyOkEmpty += ComputeSum.sumTotalWu.moneyTemporary
        ComputeSum.sumTotalWu.moneyTemporary=0
        ComputeSum.sumTotalLiu.moneyOkEmpty += ComputeSum.sumTotalLiu.moneyTemporary
        ComputeSum.sumTotalLiu.moneyTemporary=0
        ComputeSum.sumTotalQi.moneyOkEmpty += ComputeSum.sumTotalQi.moneyTemporary
        ComputeSum.sumTotalQi.moneyTemporary=0
        ComputeSum.sumTotalBa.moneyOkEmpty += ComputeSum.sumTotalBa.moneyTemporary
        ComputeSum.sumTotalBa.moneyTemporary=0
        ComputeSum.sumTotalJiu.moneyOkEmpty += ComputeSum.sumTotalJiu.moneyTemporary
        ComputeSum.sumTotalJiu.moneyTemporary=0
        ComputeSum.sumTotalShi.moneyOkEmpty += ComputeSum.sumTotalShi.moneyTemporary
        ComputeSum.sumTotalShi.moneyTemporary=0
        ComputeSum.sumTotalShiYi.moneyOkEmpty += ComputeSum.sumTotalShiYi.moneyTemporary
        ComputeSum.sumTotalShiYi.moneyTemporary=0
        ComputeSum.sumTotalShiEr.moneyOkEmpty += ComputeSum.sumTotalShiEr.moneyTemporary
        ComputeSum.sumTotalShiEr.moneyTemporary=0
        ComputeSum.sumTotalShiSan.moneyOkEmpty += ComputeSum.sumTotalShiSan.moneyTemporary
        ComputeSum.sumTotalShiSan.moneyTemporary=0
        ComputeSum.sumTotalShiSi.moneyOkEmpty += ComputeSum.sumTotalShiSi.moneyTemporary
        ComputeSum.sumTotalShiSi.moneyTemporary=0
        ComputeSum.sumTotalShiWu.moneyOkEmpty += ComputeSum.sumTotalShiWu.moneyTemporary
        ComputeSum.sumTotalShiWu.moneyTemporary=0
        ComputeSum.sumTotalShiLiu.moneyOkEmpty += ComputeSum.sumTotalShiLiu.moneyTemporary
        ComputeSum.sumTotalShiLiu.moneyTemporary=0
        ComputeSum.sumTotalShiQi.moneyOkEmpty += ComputeSum.sumTotalShiQi.moneyTemporary
        ComputeSum.sumTotalShiQi.moneyTemporary=0

        //保存需要的钱
        ComputeSum.sumTotalSi.moneyOk= ComputeSum.sumTotalSi.moneyOkEmpty
        ComputeSum.sumTotalWu.moneyOk= ComputeSum.sumTotalWu.moneyOkEmpty
        ComputeSum.sumTotalLiu.moneyOk= ComputeSum.sumTotalLiu.moneyOkEmpty
        ComputeSum.sumTotalQi.moneyOk= ComputeSum.sumTotalQi.moneyOkEmpty
        ComputeSum.sumTotalBa.moneyOk= ComputeSum.sumTotalBa.moneyOkEmpty
        ComputeSum.sumTotalJiu.moneyOk= ComputeSum.sumTotalJiu.moneyOkEmpty
        ComputeSum.sumTotalShi.moneyOk= ComputeSum.sumTotalShi.moneyOkEmpty
        ComputeSum.sumTotalShiYi.moneyOk= ComputeSum.sumTotalShiYi.moneyOkEmpty
        ComputeSum.sumTotalShiEr.moneyOk= ComputeSum.sumTotalShiEr.moneyOkEmpty
        ComputeSum.sumTotalShiSan.moneyOk= ComputeSum.sumTotalShiSan.moneyOkEmpty
        ComputeSum.sumTotalShiSi.moneyOk= ComputeSum.sumTotalShiSi.moneyOkEmpty
        ComputeSum.sumTotalShiWu.moneyOk= ComputeSum.sumTotalShiWu.moneyOkEmpty
        ComputeSum.sumTotalShiLiu.moneyOk= ComputeSum.sumTotalShiLiu.moneyOkEmpty
        ComputeSum.sumTotalShiQi.moneyOk= ComputeSum.sumTotalShiQi.moneyOkEmpty

        //点击确定的时候要更新一下动画位置
        if(ComputeSum.sumTotalSi.moneyOkEmpty>0){
            ComputeSum.sumTotalSi.viewXYLast= ComputeSum.sumTotalSi.viewXYTemporary
        }
        if(ComputeSum.sumTotalWu.moneyOkEmpty>0){
            ComputeSum.sumTotalWu.viewXYLast= ComputeSum.sumTotalWu.viewXYTemporary
        }
        if(ComputeSum.sumTotalLiu.moneyOkEmpty>0){
            ComputeSum.sumTotalLiu.viewXYLast= ComputeSum.sumTotalLiu.viewXYTemporary
        }
        if(ComputeSum.sumTotalQi.moneyOkEmpty>0){
            ComputeSum.sumTotalQi.viewXYLast= ComputeSum.sumTotalQi.viewXYTemporary
        }
        if(ComputeSum.sumTotalBa.moneyOkEmpty>0){
            ComputeSum.sumTotalBa.viewXYLast= ComputeSum.sumTotalBa.viewXYTemporary
        }
        if(ComputeSum.sumTotalJiu.moneyOkEmpty>0){
            ComputeSum.sumTotalJiu.viewXYLast= ComputeSum.sumTotalJiu.viewXYTemporary
        }
        if(ComputeSum.sumTotalShi.moneyOkEmpty>0){
            ComputeSum.sumTotalShi.viewXYLast= ComputeSum.sumTotalShi.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiYi.moneyOkEmpty>0){
            ComputeSum.sumTotalShiYi.viewXYLast= ComputeSum.sumTotalShiYi.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiEr.moneyOkEmpty>0){
            ComputeSum.sumTotalShiEr.viewXYLast= ComputeSum.sumTotalShiEr.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiSan.moneyOkEmpty>0){
            ComputeSum.sumTotalShiSan.viewXYLast= ComputeSum.sumTotalShiSan.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiSi.moneyOkEmpty>0){
            ComputeSum.sumTotalShiSi.viewXYLast= ComputeSum.sumTotalShiSi.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiWu.moneyOkEmpty>0){
            ComputeSum.sumTotalShiWu.viewXYLast= ComputeSum.sumTotalShiWu.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiLiu.moneyOkEmpty>0){
            ComputeSum.sumTotalShiLiu.viewXYLast= ComputeSum.sumTotalShiLiu.viewXYTemporary
        }
        if(ComputeSum.sumTotalShiQi.moneyOkEmpty>0){
            ComputeSum.sumTotalShiQi.viewXYLast= ComputeSum.sumTotalShiQi.viewXYTemporary
        }
        //设置一下最新的钱
        sumTotalFragment.setAllShowViewMoney()
        //判断现在哪些要展示在注区
        sumTotalFragment.deleteBet(false)


        //刷新投注区适配器
        mDatabind.llShowBetList.adapter!!.notifyDataSetChanged()
    }


    private var mPathMeasure: PathMeasure? = null

    /**
     * 贝塞尔曲线中间过程的点的坐标
     */
    private val mCurrentPosition = FloatArray(2)

    /**
     * 执行动画  isCentered如果是true就是可以超出父类的
     */
    fun startAnimation(x:Float, y:Float,isCentered:Boolean=false,speed:Long=500,animationView:View){

        var num:Int=0
        var viewX:Int=0
        var viewY:Int=0

        for (i in 0 until  mDatabind.llShowBetList.models!!.size) {
                if((mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).select){
                    num=i
                    break
                }
        }

        val itemCount = mDatabind.llShowBetList.adapter!!.itemCount
        val layoutManager = mDatabind.llShowBetList.layoutManager as LinearLayoutManager
        var finallyView:View?=null

        for (i in 0 until itemCount) {
            val view = layoutManager!!.findViewByPosition(i)
            if(num==i){
                finallyView=view
                break
            }

        }
        //判断选择的筹码是不是在屏幕外面
        if(finallyView!=null){
            val location = IntArray(2)
            finallyView?.getLocationInWindow(location)
            viewX = location[0] + finallyView!!.width / 2-dp2px(25)
            viewY = location[1]



            //===============
            //      一、创造出执行动画的主题---imageview
            //代码new一个imageview，图片资源是上面的imageview的图片
            // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
            val goods = ImageView(this)
            goods.setImageDrawable( MyGameManager.getListImage(num,this))
            val params = RelativeLayout.LayoutParams(dp2px(32), dp2px(32))
            mDatabind.rlRoot.addView(goods, params)
//        二、计算动画开始/结束点的坐标的准备工作
            //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
            val parentLocation = IntArray(2)
            mDatabind.rlRoot.getLocationInWindow(parentLocation)
            //得到商品图片的坐标（用于计算动画开始的坐标）
            val startLoc = IntArray(2)
            startLoc[0]=viewX
            startLoc[0]=viewY
            //得到购物车图片的坐标(用于计算动画结束后的坐标)  动画结束的位置
            val endLoc = IntArray(2)
            if(isCentered){
                endLoc[0]=x.toInt()-dp2px(10)

            }else{
                endLoc[0]=x.toInt()+dp2px(10)
            }
            endLoc[1]=y.toInt()
//        三、正式开始计算动画开始/结束的坐标
            //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
//        val startX: Float = (startLoc[0] - parentLocation[0] + selectImageView!!.width / 2).toFloat()
//        val startY: Float = (startLoc[1] - parentLocation[1] + selectImageView!!.height / 2).toFloat()
            val startX: Float =viewX.toFloat()+dp2px(12)
            val startY: Float =viewY.toFloat()-dp2px(24)

            //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5   动画结束的时候
//        val toX: Float = (endLoc[0] - parentLocation[0] +32).toFloat()
//        val toY = (endLoc[1] - parentLocation[1]).toFloat()
            val toX: Float = endLoc[0].toFloat()
            val toY = endLoc[1].toFloat()-dp2px(32)

            //   四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
            //开始绘制贝塞尔曲线
//        val path = Path()
//        //移动到起始点（贝塞尔曲线的起点）
//        path.moveTo(startX, startY)
//        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
//        path.quadTo((startX + toX) / 2, startY, toX, toY)

            val path = Path()
// 移动到起始点
            path.moveTo(startX, startY)
// 添加一条直线到目标点
            path.lineTo(toX, toY)
            //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
            // 如果是true，path会形成一个闭环
            mPathMeasure = PathMeasure(path, false)

            //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
            val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure!!.length)
            valueAnimator.duration = speed
            // 匀速线性插值器 LinearInterpolator
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()

            valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    // 当插值计算进行时，获取中间的每个值，
                    // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                    val value = animation.animatedValue as Float
                    // ★★★★★获取当前点坐标封装到mCurrentPosition
                    // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                    // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                    // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                    mPathMeasure!!.getPosTan(
                        value,
                        mCurrentPosition,
                        null
                    ) //mCurrentPosition此时就是中间距离点的坐标值

                    // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                    goods.translationX = mCurrentPosition.get(0)
                    goods.translationY = mCurrentPosition.get(1)

                }

            })
            //五、 开始执行动画
            valueAnimator.start()
            //  六、动画结束后的处理
            valueAnimator.addListener(object :Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    //动画结束
                    // 把移动的图片imageview从父布局里移除
                    mDatabind.rlRoot.removeView(goods)
                    val animator = ObjectAnimator.ofPropertyValuesHolder(animationView, SCALE_X, SCALE_Y)
                    animator.duration = 200
                    animator.start()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })
//============================

        }else{
            //
            scrollToItemAndPerformAction(mDatabind.llShowBetList,num){
                //从新获取到为止
                for (i in 0 until itemCount) {
                    val view = layoutManager!!.findViewByPosition(i)
                    if(num==i){
                        finallyView=view
                        break
                    }

                }
                val location = IntArray(2)
                finallyView?.getLocationInWindow(location)
                viewX = location[0] + finallyView!!.width / 2-dp2px(25)
                viewY = location[1]

                //===============
                //      一、创造出执行动画的主题---imageview
                //代码new一个imageview，图片资源是上面的imageview的图片
                // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
                val goods = ImageView(this)
                goods.setImageDrawable( MyGameManager.getListImage(num,this))
                val params = RelativeLayout.LayoutParams(dp2px(32), dp2px(32))
                mDatabind.rlRoot.addView(goods, params)
//        二、计算动画开始/结束点的坐标的准备工作
                //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
                val parentLocation = IntArray(2)
                mDatabind.rlRoot.getLocationInWindow(parentLocation)
                //得到商品图片的坐标（用于计算动画开始的坐标）
                val startLoc = IntArray(2)
                startLoc[0]=viewX
                startLoc[0]=viewY
                //得到购物车图片的坐标(用于计算动画结束后的坐标)  动画结束的位置
                val endLoc = IntArray(2)
                if(isCentered){
                    endLoc[0]=x.toInt()-dp2px(10)

                }else{
                    endLoc[0]=x.toInt()+dp2px(10)
                }
                endLoc[1]=y.toInt()
//        三、正式开始计算动画开始/结束的坐标
                //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
//        val startX: Float = (startLoc[0] - parentLocation[0] + selectImageView!!.width / 2).toFloat()
//        val startY: Float = (startLoc[1] - parentLocation[1] + selectImageView!!.height / 2).toFloat()
                val startX: Float =viewX.toFloat()+dp2px(12)
                val startY: Float =viewY.toFloat()-dp2px(24)

                //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5   动画结束的时候
//        val toX: Float = (endLoc[0] - parentLocation[0] +32).toFloat()
//        val toY = (endLoc[1] - parentLocation[1]).toFloat()
                val toX: Float = endLoc[0].toFloat()
                val toY = endLoc[1].toFloat()-dp2px(32)

                //   四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
                //开始绘制贝塞尔曲线
//        val path = Path()
//        //移动到起始点（贝塞尔曲线的起点）
//        path.moveTo(startX, startY)
//        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
//        path.quadTo((startX + toX) / 2, startY, toX, toY)

                val path = Path()
// 移动到起始点
                path.moveTo(startX, startY)
// 添加一条直线到目标点
                path.lineTo(toX, toY)
                //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
                // 如果是true，path会形成一个闭环
                mPathMeasure = PathMeasure(path, false)

                //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
                val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure!!.length)
                valueAnimator.duration = speed
                // 匀速线性插值器 LinearInterpolator
                valueAnimator.interpolator = AccelerateDecelerateInterpolator()

                valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                    override fun onAnimationUpdate(animation: ValueAnimator) {
                        // 当插值计算进行时，获取中间的每个值，
                        // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                        val value = animation.animatedValue as Float
                        // ★★★★★获取当前点坐标封装到mCurrentPosition
                        // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                        // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                        // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                        mPathMeasure!!.getPosTan(
                            value,
                            mCurrentPosition,
                            null
                        ) //mCurrentPosition此时就是中间距离点的坐标值

                        // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                        goods.translationX = mCurrentPosition.get(0)
                        goods.translationY = mCurrentPosition.get(1)

                    }

                })
                //五、 开始执行动画
                valueAnimator.start()
                //  六、动画结束后的处理
                valueAnimator.addListener(object :Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        //动画结束
                        // 把移动的图片imageview从父布局里移除
                        mDatabind.rlRoot.removeView(goods)
                        val animator = ObjectAnimator.ofPropertyValuesHolder(animationView, SCALE_X, SCALE_Y)
                        animator.duration = 200
                        animator.start()
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }

                })
//============================
            }






//            //第一个
//           var diyi= layoutManager.findFirstVisibleItemPosition()
//            var zui=layoutManager.findLastVisibleItemPosition()
//            var di=diyi-num
//            var hou=zui-num
//            //正数就是左边
//             if(di>0){
//                   finallyView = layoutManager!!.findViewByPosition(diyi)
//             val location = IntArray(2)
//            finallyView?.getLocationInWindow(location)
//             viewX = location[0] + finallyView!!.width / 2-dp2px(25)
//             viewY = location[1]
//             }else{
//                 //右边就是负数
//                   finallyView = layoutManager!!.findViewByPosition(zui)
//                 val location = IntArray(2)
//                 finallyView?.getLocationInWindow(location)
//                 viewX = location[0] + finallyView!!.width / 2-dp2px(25)
//                 viewY = location[1]
//             }

        }

    }

    /**
     * 判断当前余额是否支持投注,并且扣了临时的总金额的钱
     */
    fun isCanBetting() :Boolean{
        for (i in 0 until  mDatabind.llShowBetList.models!!.size) {
            if((mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).select){
                if(MyGameManager.temporaryCurrentMoney>=(mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).money){
                    MyGameManager.temporaryCurrentMoney=(MyGameManager.temporaryCurrentMoney-(mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).money)
                    mDatabind.llShowBetList.adapter!!.notifyDataSetChanged()
                    return true

                }else{

                    return false
                }

                break
            }
        }


        return false
    }

    fun select(num:Int){
        if(num==0){
            mDatabind.txtHomeDefault.setTextColor(ContextCompat.getColor(this,R.color.g_f7cf41))
            mDatabind.txtHomeSingle.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSum.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeDouble.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeLeopard.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.viewPager.currentItem = num
        }else if(num==1){
            mDatabind.txtHomeDefault.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSingle.setTextColor(ContextCompat.getColor(this,R.color.g_f7cf41))
            mDatabind.txtHomeSum.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeDouble.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeLeopard.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.viewPager.currentItem = num
        }else if(num==2){
            mDatabind.txtHomeDefault.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSingle.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSum.setTextColor(ContextCompat.getColor(this,R.color.g_f7cf41))
            mDatabind.txtHomeDouble.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeLeopard.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.viewPager.currentItem = num
        }else if(num==3){
            mDatabind.txtHomeDefault.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSingle.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSum.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeDouble.setTextColor(ContextCompat.getColor(this,R.color.g_f7cf41))
            mDatabind.txtHomeLeopard.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.viewPager.currentItem = num
        }else if(num==4){
            mDatabind.txtHomeDefault.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSingle.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeSum.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeDouble.setTextColor(ContextCompat.getColor(this,R.color.g_9696b8))
            mDatabind.txtHomeLeopard.setTextColor(ContextCompat.getColor(this,R.color.g_f7cf41))
            mDatabind.viewPager.currentItem = num
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        MyGameManager.removeLiveStatusListener(this.toString())

        MyGameManager.static=-1
        MyGameManager.countdownTime=20000
        MyGameManager.isClickOperation=true
        MyGameManager.countDownTimer!!.cancel()
        //清除
        ComputeDefault.leftTop= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeDefault.rightTop= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeDefault.leftBelow= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeDefault.leftBelow= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeDefault.centreDate= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )


        //单筛子
        ComputeSingle.singleYi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSingle.singleEr= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSingle.singleSan= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSingle.singleSi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSingle.singleWu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSingle.singleLiu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        //对子
        ComputePairs.pairsYi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputePairs.pairsEr= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputePairs.pairsSan= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputePairs.pairsSi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputePairs.pairsWu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputePairs.pairsLiu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )

        //豹子
        ComputeLeopard.leopardYi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeLeopard.leopardEr= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeLeopard.leopardSan= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeLeopard.leopardSi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeLeopard.leopardWu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeLeopard.leopardLiu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        //总和
        ComputeSum.sumTotalSi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalWu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalLiu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalQi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalBa= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalJiu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiYi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiEr= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiSan= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiSi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiWu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiLiu= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
        ComputeSum.sumTotalShiQi= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0), viewXYLast= intArrayOf(0, 0) )
    }




    fun scrollToItemAndPerformAction(recyclerView: RecyclerView, position: Int, action: () -> Unit) {

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val screenWidth = recyclerView.width
        // 添加滚动监听器
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // 当滚动停止时执行操作
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val visibleItem = layoutManager.findViewByPosition(position)
                    if (visibleItem != null) {
                        // 执行操作
                        action.invoke()
                        recyclerView.removeOnScrollListener(this)

                    }

                }
            }
        })
        scrollToMiddleHorizontal(recyclerView,position)
    }

    // 检查项是否完全可见
    private fun isItemFullyVisible(recyclerView: RecyclerView, position: Int): Boolean {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager  ?: return false
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        return position in firstVisibleItemPosition..lastVisibleItemPosition
    }

//    fun scrollToMiddleHorizontal(recyclerView: RecyclerView, position: Int) {
//        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//        val screenWidth = recyclerView.width
//        val itemWidth = layoutManager.findViewByPosition(position)?.width ?: 0
//        val scrollDistance = (screenWidth - itemWidth)/2
//
////        recyclerView.smoothScrollBy(scrollDistance,0)
//        recyclerView.smoothScrollToPosition(scrollDistance)
//    }

fun scrollToMiddleHorizontal(recyclerView: RecyclerView, position: Int) {
    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
    val screenWidth = recyclerView.width
    val itemWidth = layoutManager.findViewByPosition(position)?.width ?: 0
    val scrollDistance = (screenWidth - itemWidth) / 2

    layoutManager.scrollToPositionWithOffset(position, -scrollDistance)
    recyclerView.post {
        val targetView = layoutManager.findViewByPosition(position)
        if (targetView != null) {
            val targetDistance = targetView.left + targetView.width / 2 - screenWidth / 2
            recyclerView.smoothScrollBy(targetDistance, 0)
        }
    }
}
}