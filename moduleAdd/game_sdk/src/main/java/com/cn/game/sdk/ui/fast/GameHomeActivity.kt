package com.cn.game.sdk.ui.fast


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk.MyGameApplication
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
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat


class GameHomeActivity : BaseGameActivity<GameHomeVm, ActivityGameHomeBinding>() {
    private var mFragList = ArrayList<Fragment>()

    /**
     * 是否显示骰子的结果组合
     */
    private var isShowResult:Boolean=true



    /**
     * 判断所有的按钮是否可以点击
     */
    private  var isClick:Boolean=true


    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        supportActionBar?.hide()
        // 设置状态栏颜色为透明
        window.statusBarColor = getColor(android.R.color.transparent)
        var homeDefaultFragment = HomeDefaultFragment()
        val basketball = Bundle().apply {
            putInt("type",0)
        }

//        val viewModelProvider = ViewModelProvider(this)
//  viewModelProvider[mViewModel::class.java]
        mViewModel.getddd()
        mDatabind.ivHomeLogo.clickNoRepeat {
            hiddenView()
            mDatabind.rlShowResult.visibility=View.VISIBLE
        }
        appGameViewModel.ceshEvent.observe(this){
            Log.i("CCCCCCCCCCCc","333333333")
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
//                            llShowDice.visibility = View.VISIBLE
//                            ObjectAnimator.ofFloat(llShowDice, "translationY", llShowDice.height.toFloat(), 0f).apply {
//                                duration = 500
//                                interpolator = AccelerateInterpolator()
//                                start()
//                            }


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
}