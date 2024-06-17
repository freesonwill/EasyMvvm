package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.FragFast3HomeBinding
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.databinding.ItemBetHistoryBinding
import com.cn.game.sdk2.listener.GameTimeStatic
import com.cn.game.sdk2.ui.view.CustomBubbleAttachPopup
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.utils.MyGameManager
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.viewmodel.fast3.Fast3ViewModel
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lzf.easyfloat.EasyFloat
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.bean.MutablePair
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.lang.ref.WeakReference

class Fast3MainFragment : BaseVmDbFragment<Fast3ViewModel, FragFast3HomeBinding>() {
    private var mFragList = ArrayList<Fragment>()

    //默认
    lateinit var dxdsFragment : DXDSFragment

    //是否执行关闭动画
    var isExecuteClose: Boolean = true


    // 定义属性动画常量
    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.4f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.4f, 1.0f)

    var betView : View ?=null
    var betMoney : Int = 0

    /**
     * 是否显示骰子的结果组合
     */
    private var isShowResult: Boolean = true


    var popup: BasePopupView? = null
    var bubbleAttach: CustomBubbleAttachPopup? = null


    private val initialUpperLayoutHeightMap = mutableMapOf<Int, Int>()


    private val resultAnimatorList by lazy { mutableListOf<Animator>() }
    private val resultAnimatorSet by lazy { AnimatorSet() }
    private var resultAnimMoveHeight = 0
    private var isAdd: Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        MyGameManager.static = 1
        MyGameManager.countdownTime = 10000
        MyGameManager.setLiveStatusListener("home", object : GameTimeStatic {
            override fun onCountdown(time: Long) {
                super.onCountdown(time)

                val seconds = Math.round(time.toDouble() / 1000)

                if (seconds.toInt() != 0 && seconds.toInt() <= 5 && MyGameManager.static == 1) {

                    PromptSoundPlay.countdownGameTip(requireContext())
                }
                mDatabind.txtHomeTime.text = seconds.toString()


            }

            override fun onStatic(mStatic: Int) {
                super.onStatic(mStatic)
                mDatabind.txtHomeTime.text = "0"
                MyGameManager.countdownTime = 10000
                MyGameManager.staCountDownTimer()
                //1是下注   2结算
                if (mStatic == 2) {
                    Log.i("SSSSSSSSSSs", "==========" + mStatic)
                    //关闭
                    PromptSoundPlay.endGameTip(requireContext())
                    val childAlphaAnimator =
                        ObjectAnimator.ofFloat(mDatabind.llShowBetList, "alpha", 1f, 0f)
                    childAlphaAnimator.duration = 200 // 设置渐隐动画持续时间
                    val animatorSet = AnimatorSet()
                    animatorSet.play(childAlphaAnimator)
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            //注区
                            mDatabind.llShowBetList.visibility = View.INVISIBLE
                            //显示开奖结果
                            mDatabind.rlShowResult.visibility = View.VISIBLE
                            mDatabind.ivHomeBg.visibility = View.VISIBLE
                            hiddenView()
                        }
                    })
                    animatorSet.start()

//                    mDatabind.llShowBetList.visibility=View.INVISIBLE

                    mDatabind.txtHomeTime.visibility = View.GONE
                    mDatabind.txtHomeUnit.visibility = View.GONE
                    mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_balance)
                    //结算的时候要把每个模块中奖的信息显示出来 默认
                   /* dxdsFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())
                    singleDiceFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())
                    pairsDiceFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())
                    leopardFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())

                    sumTotalFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())*/
                } else {
                    Log.i("SSSSSSSSSSs", "2222===")
                    //开始语音
                    PromptSoundPlay.startGameTip(requireContext())
                    val childAlphaAnimator =
                        ObjectAnimator.ofFloat(mDatabind.llShowBetList, "alpha", 0f, 1f)
                    childAlphaAnimator.duration = 200 // 设置渐隐动画持续时间
                    val animatorSet = AnimatorSet()
                    animatorSet.play(childAlphaAnimator)
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            //注区
                            mDatabind.llShowBetList.visibility = View.VISIBLE
                            //显示开奖结果
                            mDatabind.rlShowResult.visibility = View.GONE
                            mDatabind.ivHomeBg.visibility = View.GONE
                            hiddenView(true)
                        }
                    })
                    animatorSet.start()

                    //新的下注要删除所有的
                    MyGameManager.isClickOperation = true
                    mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_txt_please)
                    mDatabind.txtHomeTime.visibility = View.VISIBLE
                    mDatabind.txtHomeUnit.visibility = View.VISIBLE

                    //默认
                   /* dxdsFragment.closeBetting()
                    dxdsFragment.fadeOut(false, ArrayList<InPrizeBean>())
                    //单筛子
                    singleDiceFragment.closeBetting()
                    singleDiceFragment.fadeOut(false, ArrayList<InPrizeBean>())
                    //对子
                    pairsDiceFragment.closeBetting()
                    pairsDiceFragment.fadeOut(false, ArrayList<InPrizeBean>())
                    //豹子
                    leopardFragment.closeBetting()
                    leopardFragment.fadeOut(false, ArrayList<InPrizeBean>())

                    //豹子
                    sumTotalFragment.closeBetting()
                    sumTotalFragment.fadeOut(false, ArrayList<InPrizeBean>())*/

                }

            }
        })

        dxdsFragment = DXDSFragment(mViewModel)
        //viewpager
        mFragList.add(dxdsFragment)

        mDatabind.viewPagerNew.initGameViewPager(
            childFragmentManager, mFragList, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
              /*  requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard),
                requireContext().getString(R.string.g_home_tab_sum)*/
            )
        )
        mDatabind.magicIndicator.bindViewPagerNewGame(
            mDatabind.viewPagerNew, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
                /*requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard),
                requireContext().getString(R.string.g_home_tab_sum)*/
            ), scrollEnable = true
        )
        mDatabind.viewPagerNew.offscreenPageLimit = mFragList.size

        adapter()
        setClick()
        bubbleAttach = CustomBubbleAttachPopup(requireContext())
        bubbleAttach!!.customBubbleAttachListener =
            object : CustomBubbleAttachPopup.CustomBubbleAttachListener {
                override fun switchGame() {
                    popup!!.dismiss()
                }

            }

        popup = XPopup.Builder(requireContext())
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

            PromptSoundPlay.btnPlayMedia(requireContext())
            popup!!.show()


        }
        //获取当前余额
        //mDatabind.txtCurrentMoney.text = MyGameManager.currentMoney.addCommas()
        /*rewritingTouch(
            tempTouth = mDatabind.tempTouth,
            viewPager = mDatabind.viewPagerNew,
            homeDefaultFragment = dxdsFragment,
            singleDiceFragment = singleDiceFragment,
            sumTotalFragment = sumTotalFragment,
            pairsDiceFragment = pairsDiceFragment,
            leopardFragment = leopardFragment
        )*/
    }

    override fun lazyLoadData() {
    }

    override fun createObserver() {
        mViewModel.moneyAnimCallback = object : Fast3ViewModel.MoneyAnimCallback{
            override fun startAnim(x: Float, y: Float, isCentered: Boolean, speed: Long, areaView: GameAreaView) {
                tryMoneyAnimation(x, y, isCentered, speed, areaView)
            }
        }
        mViewModel.betOkClick.observe(this){
            mViewModel.anchorMoneyView?.get()?.hiddenTop()
            var tempMoney:Int
            //将tempMap中的数据更新至savedMap
            for(key in mViewModel.tempMoneyMap.keys){
                tempMoney = mViewModel.tempMoneyMap[key]?.first ?: 0
                //保存新数据saved+temp
                if(mViewModel.savedMoneyMap.containsKey(key)){
                    mViewModel.savedMoneyMap[key]?.apply { first += tempMoney }
                //创建saved数据
                }else{
                    mViewModel.tempMoneyMap[key]?.let {
                        mViewModel.savedMoneyMap[key] = MutablePair(tempMoney,(it.second))
                    }
                }
            }
            mViewModel.anchorMoneyView?.get()?.hiddenTop()

        }

        mViewModel.betDeleteClick.observe(this){
            for(key in mViewModel.tempMoneyMap.keys){
                //还原savedmap中的数据
                if(mViewModel.savedMoneyMap.containsKey(key)){
                    mViewModel.savedMoneyMap[key]?.apply {
                        second.get()?.apply {
                            setShowMoney(first)
                        }
                    }
                //直接移除MoneyView
                }else{
                    mViewModel.tempMoneyMap[key]?.second?.get()?.let{
                        Log.e(this.toString(),"moneyOkView = $it")
                        Log.e(this.toString(),"viewparent = "+it.parent)
                        var parent = it.parent as ViewGroup
                        parent.removeView(it)
                    }
                }
            }
            mViewModel.anchorMoneyView?.get()?.hiddenTop()
            mViewModel.tempMoneyMap.clear()
        }
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }


    /**
     * 开奖结果隐藏不要的控件  。如果是投注的话就不应酬
     */
    fun hiddenView(isBetting: Boolean = false) {
        if (isShowResult && !isBetting) {
            resultAnimation()
        }
    }


    /**
     * 开奖结果显示或者隐藏动画
     */
    @SuppressLint("ObjectAnimatorBinding")
    fun resultAnimation() {
        resultAnimatorList.clear()
        if (isShowResult) {
            //这个是隐藏往下的动画
            mDatabind.ivHomeRotation.rotation = 180f
            isShowResult = !isShowResult
            //todo:
            for (i in 0 until mDatabind.rvHomeHistory.models!!.size) {
                val viewHolder = mDatabind.rvHomeHistory.findViewHolderForLayoutPosition(i)
                if (viewHolder != null) {
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow = false
                    val llShowDice = viewHolder.itemView.findViewById<LinearLayout>(R.id.llShowDice)
                    //llShowDice.height.toFloat()高度是205
                    if (isAdd) {
                        resultAnimMoveHeight = llShowDice.height
                        isAdd = false
                    }
                    ValueAnimator.ofFloat(resultAnimMoveHeight.toFloat(), 0f).apply {
                        addUpdateListener {
                            val value = (it.animatedValue as Float).toInt()
                            val params = llShowDice.layoutParams
                            params?.height = value
                            llShowDice.layoutParams = params
                        }
                        resultAnimatorList.add(this)
                    }
                } else {
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow = false
                    mDatabind.rvHomeHistory.bindingAdapter.notifyItemChanged(i)
                }
            }
        } else {
            //显示 往上的动画
            mDatabind.ivHomeRotation.rotation = 0f
            isShowResult = !isShowResult
            for (i in 0 until mDatabind.rvHomeHistory.models!!.size) {
                val viewHolder = mDatabind.rvHomeHistory.findViewHolderForLayoutPosition(i)
                if (viewHolder != null) {
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow = true
                    val llShowDice = viewHolder.itemView.findViewById<LinearLayout>(R.id.llShowDice)
                    ValueAnimator.ofFloat(0f, resultAnimMoveHeight.toFloat()).apply {
                        addUpdateListener {
                            val value = (it.animatedValue as Float).toInt()
                            val params = llShowDice.layoutParams
                            params?.height = value
                            llShowDice.layoutParams = params
                            llShowDice.requestLayout()
                        }
                        resultAnimatorList.add(this)
                    }
                } else {
                    (mDatabind.rvHomeHistory.models!![i] as HistoryResultBean).isShow = true
                }
            }
        }

        //执行动画集合
        if (resultAnimatorList.isNotEmpty()) {
            resultAnimatorSet.cancel()
            resultAnimatorSet.apply {
                duration = 400
                playTogether(resultAnimatorList)
                start()
            }
        }
    }


    /**
     * 投注的适配器
     */
    private fun adapter() {
        mDatabind.llShowBetList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mDatabind.llShowBetList.setup {
            addType<SelectAnnotationBean>(R.layout.item_annotation_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_annotation_list -> {
                        if(betView == null){
                            betView = itemView
                            betMoney = MyGameManager.noteList[0].money
                        }
                        var binding = getBinding<ItemAnnotationListBinding>()
                        val bean = _data as SelectAnnotationBean
                        //todo:
                        if (layoutPosition == 0) {
                            if (MyGameManager.temporaryCurrentMoney < 10) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_shi
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_shi
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_shi
                                        )
                                    )
                                }
                            }
                        } else if (layoutPosition == 1) {
                            if (MyGameManager.temporaryCurrentMoney < 50) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_wushi
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_wushi
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_wushi
                                        )
                                    )
                                }
                            }
                        } else if (layoutPosition == 2) {
                            if (MyGameManager.temporaryCurrentMoney < 100) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_yibai
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_yibai
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_yibai
                                        )
                                    )
                                }
                            }

                        } else if (layoutPosition == 3) {

                            if (MyGameManager.temporaryCurrentMoney < 200) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_liangbai
                                    )
                                )
                            } else {

                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_liangbai
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_liangbai
                                        )
                                    )
                                }
                            }
                        } else if (layoutPosition == 4) {
                            if (MyGameManager.temporaryCurrentMoney < 500) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_wubai
                                    )
                                )
                            } else {

                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_wubai
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_wubai
                                        )
                                    )
                                }
                            }
                        } else if (layoutPosition == 5) {
                            if (MyGameManager.temporaryCurrentMoney < 1000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_qian
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_qian
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_qian
                                        )
                                    )
                                }
                            }

                        } else if (layoutPosition == 6) {

                            if (MyGameManager.temporaryCurrentMoney < 2000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_liangqian
                                    )
                                )
                            } else {

                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_liangqian
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_liangqian
                                        )
                                    )
                                }

                            }
                        } else if (layoutPosition == 7) {

                            if (MyGameManager.temporaryCurrentMoney < 5000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_wuqian
                                    )
                                )
                            } else {

                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_wuqian
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_wuqian
                                        )
                                    )
                                }
                            }
                        } else if (layoutPosition == 8) {
                            if (MyGameManager.temporaryCurrentMoney < 10000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_yiwan
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_yiwan
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_yiwan
                                        )
                                    )
                                }
                            }

                        } else if (layoutPosition == 9) {
                            if (MyGameManager.temporaryCurrentMoney < 20000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_liangwan
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_liangwan
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_liangwan
                                        )
                                    )
                                }
                            }
                        } else if (layoutPosition == 10) {

                            if (MyGameManager.temporaryCurrentMoney < 50000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_wuwan
                                    )
                                )
                            } else {
                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_wuwan
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_wuwan
                                        )
                                    )
                                }

                            }
                        } else {
                            if (MyGameManager.temporaryCurrentMoney < 100000) {
                                binding.ivShowBg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.icon_shortage_shiwan
                                    )
                                )
                            } else {

                                if (bean.select) {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_select_shiwan
                                        )
                                    )
                                } else {
                                    binding.ivShowBg.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.icon_no_shiwan
                                        )
                                    )
                                }
                            }

                        }
                    }

                }

            }
            R.id.ivShowBg.onClick {
                PromptSoundPlay.btnPlayMedia(requireContext())
                //选择新的筹码
                var binding = getBinding<ItemAnnotationListBinding>()
                for (i in 0 until mDatabind.llShowBetList.models!!.size) {
                    (mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).select = false
                    MyGameManager.noteList[i].select = false
                }
                (mDatabind.llShowBetList.models!![modelPosition] as SelectAnnotationBean).select =
                    true
                //todo:
                notifyDataSetChanged()
                MyGameManager.noteList[modelPosition].select = true
                betMoney = MyGameManager.noteList[modelPosition].money
                betView = itemView
            }

        }.addModels(MyGameManager.noteList)

        var list = ArrayList<HistoryResultBean>()
        for (c in 0 until 20) {
            list.add(HistoryResultBean())
        }
        //历史结果
        mDatabind.rvHomeHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mDatabind.rvHomeHistory.setup {

            addType<HistoryResultBean>(R.layout.item_bet_history)

            onBind {
                when (itemViewType) {
                    R.layout.item_bet_history -> {
                        var binding = getBinding<ItemBetHistoryBinding>()
                        var mainTxtBean = _data as HistoryResultBean

                        if (mainTxtBean.isShow) {
                            binding.llShowDice.visibility = View.VISIBLE
                            // 获取并保存初始的上半部分布局高度
                            if (!initialUpperLayoutHeightMap.containsKey(modelPosition)) {
                                binding.llShowDice.post {
                                    val initialHeight = binding.llShowDice.height
                                    initialUpperLayoutHeightMap[modelPosition] = initialHeight
                                }
                            }
                        } else {
                            binding.llShowDice.visibility = View.GONE
                        }
                    }
                }
            }
        }.addModels(list)

        mDatabind.rvHomeHistory.postDelayed({
            mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size - 1)
        }, 200)
    }

    private fun setClick() {
        mDatabind.rlClickHide.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia(requireContext())
//            if(MyGameManager.isClickOperation){
//                resultAnimation()
//            }
            resultAnimation()
        }
    }


    /**
     * 判断当前余额是否支持投注,并且扣了临时的总金额的钱
     */
    fun isCanBetting(): Boolean {
        for (i in 0 until mDatabind.llShowBetList.models!!.size) {
            if ((mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).select) {
                if (MyGameManager.temporaryCurrentMoney >= (mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).money) {
                    MyGameManager.temporaryCurrentMoney =
                        (MyGameManager.temporaryCurrentMoney - (mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).money)
                    mDatabind.llShowBetList.adapter!!.notifyDataSetChanged()
                    return true

                } else {

                    return false
                }

                break
            }
        }

        return false
    }

    /**
     * 关闭页面
     */
    override fun onDestroy() {
        MyGameManager.removeLiveStatusListener("home")
        //关闭的时候要把这个赋值为0选择
        MyGameManager.noteList.forEach {
            it.select = false

        }
        MyGameManager.noteList[0].select = true
        //清空临时的
        //clickDelete()

        //关闭倒计时
//        MyGameManager.mTimer!!.stop()
        MyGameManager.countDownTimer!!.cancel()
        MyGameManager.countDownTimer = null
        EasyFloat.show(MyGameManager.TAG_1)
        //关闭动画
        resultAnimatorSet.cancel()
        resultAnimatorList.clear()
        super.onDestroy()
    }


    /**
     * 执行动画  isCentered如果是true就是可以超出父类的
     */
    fun tryMoneyAnimation(x: Float,
                          y: Float,
                          isCentered: Boolean = false,
                          speed: Long = 300,
                          areaView: GameAreaView
    ) {
//        PromptSoundPlay.goldPlayMedia(this)
//        PromptSoundPlay.goldPlayMediaNew(this)

        PromptSoundPlay.playAudio(requireContext())

        var num: Int = 0

        for (i in 0 until mDatabind.llShowBetList.models!!.size) {
            if ((mDatabind.llShowBetList.models!![i] as SelectAnnotationBean).money == betMoney) {
                num = i
                break
            }
        }
        val itemCount = mDatabind.llShowBetList.adapter!!.itemCount
        val layoutManager = mDatabind.llShowBetList.layoutManager as LinearLayoutManager
        var finallyView: View? = null

        for (i in 0 until itemCount) {
            val view = layoutManager.findViewByPosition(i)
            if (view === betView) {
                finallyView = view
                break
            }

        }
        //判断选择的筹码是不是在屏幕外面
        if (finallyView != null) {
            startMoneyAnimation(x,y,isCentered,speed,areaView,finallyView)
        } else {
            scrollToItemAndPerformAction(mDatabind.llShowBetList, num) {
                //从新获取到为止
                for (i in 0 until itemCount) {
                    val view = layoutManager.findViewByPosition(i)
                    if (num == i) {
                        finallyView = view
                        break
                    }
                }
                finallyView?.let { startMoneyAnimation(x,y,isCentered,speed,areaView, it) }
            }
        }

    }

    private fun startMoneyAnimation(x: Float,
                                    y: Float,
                                    isCentered: Boolean = false,
                                    speed: Long = 300,
                                    areaView: GameAreaView,
                                    jettonView : View){
        var mPathMeasure: PathMeasure? = null

        /**
         * 贝塞尔曲线中间过程的点的坐标
         */
        val mCurrentPosition = FloatArray(2)

        var num: Int = 0
        var viewX: Int = 0
        var viewY: Int = 0
        val location = IntArray(2)
        jettonView.getLocationInWindow(location)
        viewX = location[0] + jettonView.width / 2 - requireContext().dp2px(25)
        viewY = location[1]

        //===============
        //      一、创造出执行动画的主题---imageview
        //代码new一个imageview，图片资源是上面的imageview的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        val goods = ImageView(requireContext())
        goods.setImageDrawable(MyGameManager.getListImage(num, requireContext()))
        val params = RelativeLayout.LayoutParams(
            requireContext().dp2px(32),
            requireContext().dp2px(32)
        )
        mDatabind.rlRoot.addView(goods, params)
//        二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        val parentLocation = IntArray(2)
        mDatabind.rlRoot.getLocationInWindow(parentLocation)
        //得到商品图片的坐标（用于计算动画开始的坐标）
        val startLoc = IntArray(2)
        startLoc[0] = viewX
        startLoc[0] = viewY
        //得到购物车图片的坐标(用于计算动画结束后的坐标)  动画结束的位置
        val endLoc = IntArray(2)
//            if(isCentered){
//                endLoc[0]=x.toInt()-dp2px(10)
//
//            }else{
//                endLoc[0]=x.toInt()+dp2px(10)
//            }
        endLoc[0] = x.toInt() + requireContext().dp2px(20)
        endLoc[1] = y.toInt() + requireContext().dp2px(20)
//        三、正式开始计算动画开始/结束的坐标
        //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
//        val startX: Float = (startLoc[0] - parentLocation[0] + selectImageView!!.width / 2).toFloat()
//        val startY: Float = (startLoc[1] - parentLocation[1] + selectImageView!!.height / 2).toFloat()
        val startX: Float = viewX.toFloat() + requireContext().dp2px(12)
        val startY: Float = viewY.toFloat() - requireContext().dp2px(24)

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5   动画结束的时候
//        val toX: Float = (endLoc[0] - parentLocation[0] +32).toFloat()
//        val toY = (endLoc[1] - parentLocation[1]).toFloat()
        val toX: Float = endLoc[0].toFloat()
        val toY = endLoc[1].toFloat() - requireContext().dp2px(32)

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
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //动画结束
                // 把移动的图片imageview从父布局里移除
                mDatabind.rlRoot.removeView(goods)
                val animator =
                    ObjectAnimator.ofPropertyValuesHolder(areaView.moneyView.ivShowBg, SCALE_X, SCALE_Y)
                animator.duration = 200
                animator.start()

                /*val params = FrameLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                areaView.addView(areaView.moneyView, params)
                // 将新按钮设置为居中
                params.bottomMargin = AutoSizeUtils.dp2px(requireContext(), 20f)
                params.gravity = Gravity.CENTER
                areaView.moneyView.layoutParams = params*/
                //筹码栈处理
                mViewModel.updateAnchorView(areaView.moneyView)
                mViewModel.tempMoneyMap.apply {
                    if(!containsKey(areaView.areaCode)){
                        put(areaView.areaCode,
                            MutablePair(betMoney, WeakReference(areaView.moneyView))
                        )
                    }else{
                        get(areaView.areaCode)?.apply { first += betMoney }
                    }
                    get(areaView.areaCode)?.first?.let {
                        val sum = mViewModel.savedMoneyMap[areaView.areaCode]?.first ?: 0
                        areaView.moneyView.setShowMoney(it.plus(sum)) }
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })
    }

    private fun scrollToItemAndPerformAction(
        recyclerView: RecyclerView,
        position: Int,
        action: () -> Unit
    ) {

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
        scrollToMiddleHorizontal(recyclerView, position)
    }


    private fun scrollToMiddleHorizontal(recyclerView: RecyclerView, position: Int) {
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
