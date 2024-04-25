package com.cn.game.sdk.ui.fast


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk.R
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameActivity
import com.cn.game.sdk.bean.HistoryResultBean
import com.cn.game.sdk.bean.SelectAnnotationBean
import com.cn.game.sdk.databinding.ActivityGameHomeBinding
import com.cn.game.sdk.databinding.ItemAnnotationListBinding
import com.cn.game.sdk.databinding.ItemBetHistoryBinding
import com.cn.game.sdk.ui.fast.fragment.HomeDefaultFragment
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.view.clickNoRepeat


class GameHomeActivity : BaseGameActivity<GameHomeVm, ActivityGameHomeBinding>() {
    private var mFragList = ArrayList<Fragment>()

    var homeDefaultFragment = HomeDefaultFragment()

    /**
     * 是否显示骰子的结果组合
     */
    private var isShowResult:Boolean=true



    /**
     * 判断所有的按钮是否可以点击
     */
    private  var isClick:Boolean=true

    /**
     * 选择的注码
     */
    private  var selectAnnotation:Int=0

    /**
     * 选择的注码的View
     */
    private var  selectImageView: AppCompatImageView?=null



    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        supportActionBar?.hide()
        // 设置状态栏颜色为透明
        window.statusBarColor = getColor(android.R.color.transparent)
          homeDefaultFragment = HomeDefaultFragment()
        val basketball = Bundle().apply {
            putInt("type",0)
        }

        mViewModel.getddd()
        mDatabind.ivHomeLogo.clickNoRepeat {
            hiddenView()
            mDatabind.rlShowResult.visibility=View.VISIBLE
        }
        appGameViewModel.ceshEvent.observe(this){

        }


        homeDefaultFragment.arguments = basketball


        mFragList.add(homeDefaultFragment)

        mDatabind.viewPager.initActivity(this, mFragList, true,1)
        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.viewPager, arrayListOf(
                getString(R.string.g_home_txt_default)

            ),
            R.color.g_f7cf41,
            R.color.g_9696b8,
            14f, 14f, true, true,
            0, lineIndicatorWidth=0,margin = 10
        ){

        }

        mDatabind.viewPager.offscreenPageLimit = mFragList.size

        adapter()
        setClick()




    }
    private val initialUpperLayoutHeightMap = mutableMapOf<Int, Int>()
    private var initia = 0
    var isAdd:Boolean=true
    fun setClick(){


        mDatabind.rlClickHide.clickNoRepeat {

            if(isClick){
                resultAnimation()
            }

        }
    }




    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 判断是否按下了返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 在这里执行你想要的操作，比如关闭当前活动
            finish();
            overridePendingTransition(0,  R.anim.slide_down)
            return true; // 返回 true 表示事件已经处理，不会继续传递
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun createObserver() {
        super.createObserver()

    }

    fun adapter() {
        var listNew=ArrayList<SelectAnnotationBean>()
        for (c in 0 until 10) {
            if(c==0){
                listNew.add(SelectAnnotationBean(select=true))
            }else{
                listNew.add(SelectAnnotationBean())
            }

        }

        mDatabind.llShowBetList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mDatabind.llShowBetList.setup {
            addType<SelectAnnotationBean>(R.layout.item_annotation_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_annotation_list -> {
                        var binding=getBinding<ItemAnnotationListBinding>()
                        val bean = _data as SelectAnnotationBean
                        if(bean.select){
                            selectImageView=binding.ivShowBg
                        }

                        if(layoutPosition==0){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_shi))
                        }else if(layoutPosition==1){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wushi))
                        }else if(layoutPosition==2){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_yibai))
                        }else if(layoutPosition==3){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_liangbai))
                        }else if(layoutPosition==4){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wubai))
                        }else if(layoutPosition==5){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_qian))
                        }else if(layoutPosition==6){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wuqian))
                        }else if(layoutPosition==7){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_yiwan))
                        }else if(layoutPosition==8){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_liangwan))
                        }else if(layoutPosition==9){
                            binding.ivShowBg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_no_wuwan))
                        }
                    }

                }

            }
            R.id.ivShowBg.onClick {
                for (i in 0 until  mDatabind.llShowBetList.models!!.size) {
                    (mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).select=false
                }
                (mDatabind.llShowBetList.models!![modelPosition] as SelectAnnotationBean).select=true



                var binding=getBinding<ItemAnnotationListBinding>()
                val location = IntArray(2)
                binding.ivShowBg.getLocationInWindow(location)

              var  viewX = location[0]
                var viewY= location[1]
                Log.i("CCCCCCCCcc","=====x==="+viewX)
                Log.i("CCCCCCCCcc","=====y==="+viewY)

            }

        }.addModels(listNew)


        var list=ArrayList<HistoryResultBean>()
        for (c in 0 until 20) {
            list.add(HistoryResultBean())
        }

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
        mDatabind.llShowBetList.visibility=View.GONE

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
//                            mDatabind.rvHomeHistory.bindingAdapter.notifyItemChanged(i)
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
//                             mDatabind.rvHomeHistory.bindingAdapter.notifyItemChanged(i)
                }


            }
            mDatabind.rvHomeHistory.postDelayed({
                mDatabind.rvHomeHistory.bindingAdapter.notifyDataSetChanged()
            }, 600)

        }
    }
    private var mPathMeasure: PathMeasure? = null

    /**
     * 贝塞尔曲线中间过程的点的坐标
     */
    private val mCurrentPosition = FloatArray(2)

    /**
     * 执行右上角的动画
     */
    fun startRightTopAnimation(x:Float,y:Float){
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
        val layoutManager = mDatabind.llShowBetList.layoutManager
        for (i in 0 until itemCount) {
            if(num==i){
                val view = layoutManager!!.findViewByPosition(i)
                val location = IntArray(2)
                view?.getLocationInWindow(location)
//                viewX = location[0]
//                viewY= location[1]

                viewX = location[0] + view!!.width / 2-dp2px(25)
                viewY = location[1]
                break
            }

        }
//      一、创造出执行动画的主题---imageview
        //代码new一个imageview，图片资源是上面的imageview的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        val goods = ImageView(this)
        goods.setImageDrawable(selectImageView!!.drawable)
        val params = RelativeLayout.LayoutParams(dp2px(32), dp2px(32))
        mDatabind.rlRoot.addView(goods, params)


//        二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        val parentLocation = IntArray(2)
        mDatabind.rlRoot.getLocationInWindow(parentLocation)

        //得到商品图片的坐标（用于计算动画开始的坐标）

        //得到商品图片的坐标（用于计算动画开始的坐标）
        val startLoc = IntArray(2)
        startLoc[0]=viewX
        startLoc[0]=viewY

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        val endLoc = IntArray(2)
        endLoc[0]=x.toInt()
        endLoc[1]=y.toInt()

//        三、正式开始计算动画开始/结束的坐标
        //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
//        val startX: Float = (startLoc[0] - parentLocation[0] + selectImageView!!.width / 2).toFloat()
//        val startY: Float = (startLoc[1] - parentLocation[1] + selectImageView!!.height / 2).toFloat()
        val startX: Float =viewX.toFloat()
        val startY: Float =viewY.toFloat()

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
//        val toX: Float = (endLoc[0] - parentLocation[0] +32).toFloat()
//        val toY = (endLoc[1] - parentLocation[1]).toFloat()
        val toX: Float = endLoc[0].toFloat()+dp2px(16)
        val toY = endLoc[1].toFloat()-dp2px(32)

//        //   四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
//        //开始绘制贝塞尔曲线
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
        valueAnimator.duration = 200
        // 匀速线性插值器
        valueAnimator.interpolator = LinearInterpolator()

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
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })

    }



}