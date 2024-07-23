package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.BetteFlyData
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.FragFast3HomeBinding
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.databinding.ItemBetHistoryBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.animator.AlphaPopupAnimator
import com.cn.game.sdk2.ui.helper.AnimHelper
import com.cn.game.sdk2.ui.helper.Fast3ToastHelper
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager2
import com.cn.game.sdk2.ui.view.CenterLayoutManager
import com.cn.game.sdk2.ui.view.ClickRecyclerView
import com.cn.game.sdk2.ui.view.CommonLinearLayoutItemDecoration
import com.cn.game.sdk2.ui.view.CustomBubbleAttachPopup
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ext.BizExt.isLeopard
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.ext.CommonExt.isCanGoOn
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.utils.ext.ViewExt.getDrawable
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.utils.tool.measureView
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.gameMassageManager
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ktx.hasNavigationBar
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib2.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.dp2px
import com.xcjh.base_lib2.utils.loge
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper


@SuppressLint("SetTextI18n")
class Fast3MainFragment : BaseVmDbFragment<Fast3ViewModel, FragFast3HomeBinding>() {
    companion object {
        const val TAG = "Fast3MainFragment"
    }

    private var mFragList = ArrayList<Fragment>()
    private var isBetteUpAnimFirst = true

    private var homeMorePop: BasePopupView? = null
    private var resultAnim: ValueAnimator? = null
    private var resultRvHeight = -1
    private var resultAnimMoveHeight = -1
    private var anchorMoneyView: MoneyOKView? = null

    //<areaCode,<money,View>>
    private val currentBetteAreaMap by lazy { LinkedHashMap<Int, GameAreaView>() }
    private val allGameAreaMap by lazy { mutableMapOf<Int, GameAreaView>() }
    private val betteFlyAnimList by lazy { mutableMapOf<GameAreaView, MutableList<BetteFlyData>>() }
    private var selectBetteView: View? = null

    //==================================== Method ===============================================//
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.model = mViewModel
        OverScrollDecoratorHelper.setUpOverScroll(mDatabind.viewPagerNew);
        mDatabind.bottomLayout.setOnTouchListener { _, _ -> true }
        mDatabind.resultClickView.setOnClickListener { } //屏蔽底部recycler点击
        mViewModel.navigationBarHeight.value = requireContext().navigationBarHeight

        Fast3ToastHelper.attachToHost(mDatabind.centerLayout).let {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                var startTime: Long = 0
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    startTime = System.currentTimeMillis()
                }

                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)
                    (System.currentTimeMillis() - startTime).let {
                        LogUtils.d(TAG, "Fast3MainFragment load costMills:$it")
                    }
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    Fast3ToastHelper.destroy()
                }
            })
        }
        FlowBus.with<Boolean>(EventKey.LOAD_FRAGMENT).register(viewLifecycleOwner) {
            val startTime = System.currentTimeMillis()
            //viewpager
            mFragList.add(DXDSFragment())
            mFragList.add(SingleDiceFragment())
            mFragList.add(SumTotalFragment())
            mFragList.add(PairsDiceFragment())
            mFragList.add(LeopardFragment())
            (System.currentTimeMillis() - startTime).let {
                LogUtils.d(TAG, "Fast3MainFragment load costMills1:$it")
            }
            mDatabind.viewPagerNew.initGameViewPager(
                childFragmentManager, mFragList, arrayListOf(
                    getString(R.string.g_home_txt_default),
                    getString(R.string.g_home_tab_single),
                    getString(R.string.g_home_tab_sum),
                    getString(R.string.g_home_tab_double),
                    getString(R.string.g_home_tab_leopard)
                )
            )
            (System.currentTimeMillis() - startTime).let {
                LogUtils.d(
                    TAG,
                    "Fast3MainFragment load costMills2:$it"
                )
            }
            mDatabind.magicIndicator.bindViewPagerNewGame(
                mDatabind.viewPagerNew, arrayListOf(
                    getString(R.string.g_home_txt_default),
                    getString(R.string.g_home_tab_single),
                    getString(R.string.g_home_tab_sum),
                    getString(R.string.g_home_tab_double),
                    getString(R.string.g_home_tab_leopard)
                ),
                scrollEnable = true,
                action = { PromptSoundPlay.btnPlayMedia() }
            )
            lifecycleScope.launchWhenResumed {
                delay(1000)
                mDatabind.viewPagerNew.offscreenPageLimit = mFragList.size
            }
            (System.currentTimeMillis() - startTime).let {
                LogUtils.d(
                    TAG,
                    "Fast3MainFragment load costMills3:$it"
                )
            }
        }
        setBetAdapter()
        setClick()
        measureHistoryRvHeight()
        setNavigationBar()
    }

    override fun lazyLoadData() {
    }

    override fun initData() {
        //获取当前余额
        mDatabind.txtCurrentMoney.text = "¥ ${mViewModel.currentMoney.formatRealMoney()}"
        mDatabind.txtHomeTime.text = mViewModel.homeTimeSeconds.value.toString()
        lifecycleScope.launchWhenResumed {
            //开始下注
            LogUtils.d(TAG, "initData startBetting")
            delay(200)
            updateGameStage()
        }
    }

    private fun setNavigationBar() {
        mDatabind.apply {
            val defaultHeight = 34.dp2px
            val targetHeight = when {
                requireContext().hasNavigationBar -> {
                    val navigationBarHeight = navigationBarHeight
                    if (navigationBarHeight > defaultHeight) navigationBarHeight else defaultHeight
                }

                else -> {
                    defaultHeight
                }
            }
            val layoutParams = bottomLayout.layoutParams
            layoutParams.height = targetHeight
            bottomLayout.layoutParams = layoutParams
        }
    }

    //测量耗时操作可放到IO线程
    private fun measureHistoryRvHeight() {
        lifecycleScope.launch(Dispatchers.IO) {
            LayoutInflater.from(context).inflate(R.layout.item_bet_history, null).apply {
                measureView()
                LogUtils.d(TAG, "historyRvHeight->$measuredHeight")
                resultRvHeight = this.measuredHeight
                findViewById<LinearLayout>(R.id.llShowDice).apply {
                    this.measureView()
                    LogUtils.d(TAG, "historyMoveHeight->$measuredHeight")
                    resultAnimMoveHeight = this.measuredHeight - 2.dp2px
                }
                val params = mDatabind.flRvHistory.layoutParams
                params?.height = resultRvHeight - resultAnimMoveHeight
                mDatabind.flRvHistory.layoutParams = params
            }
        }
    }

    /**Ï
     * 刷新游戏状态
     */
    private fun updateGameStage() {
        gameAboutModel.currentStage.value?.let {
            if (mViewModel.localGameStage == it) return@let
            mViewModel.isClickOperation = it == GameAboutModel.Stage.NEW
            mDatabind.txtHomeTime.isVisible = it == GameAboutModel.Stage.NEW
            mDatabind.txtHomeUnit.isVisible = it == GameAboutModel.Stage.NEW
            mViewModel.localGameStage = it
            when (it) {
                GameAboutModel.Stage.NEW -> {
                    onStartBetting()
                }

                GameAboutModel.Stage.DEAL -> {
                    onStartDrawing()
                }

                GameAboutModel.Stage.SETTLE -> {
                    onStartSetting()
                }
            }
            //if(!mViewModel.isCountDownInit) mViewModel.countDown = gameAboutModel.countDown * 1L
            LogUtils.dTag(
                TAG,
                "updateGameStage-->${it},countDown:${mViewModel.countDown},isCountDownStart:${mViewModel.isCountDownStart}"
            )
        }
    }


    private fun onStartBetting() {
        lifecycleScope.launch {
            //开始语音
            if (mViewModel.isCountDownStart) {
                Fast3ToastHelper.showToastNormal(getString(R.string.g_home_betting_begin), 2000)
            }
            mDatabind.apply {
                async {
                    playAlphaAnimTogether(
                        arrayOf(txtHomeStatic, txtHomeTime, txtHomeUnit),
                        floatArrayOf(0f, 1f)
                    )
                    txtHomeStatic.text = resources.getString(R.string.g_home_txt_please)
                }
            }
            LogUtils.d(TAG, "onStartBetting, isCountDownStart:${mViewModel.isCountDownStart}")
            //取消注区闪烁
            mViewModel.cancelAreaFlickAnimLiveData.value = true
            //重置注区筹码
            notifyMoneyOkView(null)
            if (mViewModel.isCountDownStart) {
                Fast3ToastHelper.showToastNormal(getString(R.string.g_home_betting_begin), 2000)
            }
            notifyBetteBean()
            //下注筹码向上升起动画
            startBetteRecyclerShowOrHideAnim(isShow = true, onStart = {
                //筹码
                mDatabind.betteLayout.isVisible = true
                mDatabind.betteAgainLayout.isVisible = true

                //开奖结果x
                mDatabind.rlShowResult.isVisible = false
                mDatabind.ivHomeBg.isVisible = false
                mDatabind.ivHomeBgCenter.isVisible = false
                mDatabind.resultBgTop.isVisible = false
            }, duration = if (mViewModel.isCountDownStart) 250 else 0)

            //暂时解决筹码栏被隐藏问题
            delay(500)
            if (mViewModel.gameState == GameAboutModel.Stage.NEW && !mDatabind.betteLayout.isVisible) {
                resetBetteRecyclerVisible()
            }
        }
    }

    private fun onStartSetting() { //开始结算
        lifecycleScope.launch {
            mDatabind.apply {
                //Fast3ToastHelper.showToastNormal(getString(R.string.g_home_setting_begin), 1000)
                async {
                    playAlphaAnimTogether(arrayOf(txtHomeStatic), floatArrayOf(0f, 1f))
                    txtHomeStatic.text = resources.getString(R.string.g_f3_setting)
                }
                mDatabind.betteLayout.isInvisible = true
                mDatabind.betteAgainLayout.isVisible = false
                updateCenterRoundInfoData()
                //开奖结果显示动画
                startCenterRoundInfoShowAnim {
                    //中奖动画
                    startWinLottieAnim(endCallBack = {
                        //开奖结果注区动画闪烁
                        Log.e(TAG, "中奖注区结果监听--->${gameAboutModel.lotteryResultList}")
                        mViewModel.userLotteryResultLiveData.value =
                            gameAboutModel.lotteryResultList

                        //中奖区域金额刷新
                        Log.e(TAG, "中奖注区筹码监听--->${gameAboutModel.userLotteryResult}")
                        notifyMoneyOkView(gameAboutModel.userLotteryResult)
                    })
                }
            }
        }
    }

    /**
     * 播放透明度动画
     */
    private fun playAlphaAnimTogether(views: Array<View>, alphas: FloatArray, d: Long = 200) {
        val set = AnimatorSet()
        val animators = views.map { v ->
            val animator = v.getTag(v.id) as Animator?
            animator?.cancel()
            ObjectAnimator.ofFloat(v, "alpha", *alphas).apply {
                duration = d
                v.setTag(v.id, this)
                addListener(
                    onStart = { v.alpha = alphas[0] },
                    onEnd = { v.setTag(v.id, null) }
                )
            }
        }
        set.playTogether(animators)
        set.start()
    }

    /**
     * 开奖中
     */
    private fun onStartDrawing() {
        lifecycleScope.launch {//关闭
            if (mViewModel.isCountDownStart) {
                //PromptSoundPlay.endGameTip(requireContext())
                //Fast3ToastHelper.showToastNormal(getString(R.string.g_home_drawing_begin), 1000)
            }
            cancelBetteFlyAnim()
            cancelTemBetting()
            //开奖时取消临时下注的
            mDatabind.apply {
                async {
                    playAlphaAnimTogether(arrayOf(txtHomeStatic), floatArrayOf(0f, 1f))
                    txtHomeStatic.text = getString(R.string.g_f3_dealing)
                }
                //隐藏筹码牌动画
                startBetteRecyclerShowOrHideAnim(isShow = false, onEnd = {
                    //注区
                    betteLayout.isInvisible = true
                    betteAgainLayout.isVisible = false

                    //开奖结果
                    ivHomeBg.isVisible = true
                    ivHomeBgCenter.isVisible = true
                    resultBgTop.isVisible = true
                })
            }
        }
    }

    /**
     * 更新当局游戏结果信息
     */
    private fun updateCenterRoundInfoData() {
        val roundInfo: RoundInfoBean? = gameAboutModel.currentSettleResult
        Log.e(TAG, "结算item" + roundInfo.toString())
        mDatabind.apply {
            roundInfo?.run {
                performs.forEachIndexed { index, item ->
                    val id = resources.getIdentifier(
                        "icon_dice_" + item.toPinyin(),
                        "drawable",
                        requireContext().packageName
                    )
                    when (index) {
                        0 -> ivDrawYi.setImageResource(id)
                        1 -> ivDrawEr.setImageResource(id)
                        2 -> ivDrawSan.setImageResource(id)
                    }
                }
                txtHomeTotal.text = sum.toString()
                //豹子只显示骰子和点数，不显示大小和单双
                if (isLeopard) {
                    ivBetSize.isVisible = false
                    ivBetOdd.isVisible = false
                } else {
                    ivBetSize.isVisible = true
                    ivBetOdd.isVisible = true
                    ivBetSize.setImageResource(if (isBig) R.drawable.icon_home_result_big else R.drawable.icon_home_result_small)
                    ivBetOdd.setImageResource(if (isDouble) R.drawable.icon_home_result_double else R.drawable.icon_home_result_single)
                }
            }
        }
    }

    /**
     * 播放中奖lottie动画
     */
    private var lottieListener: AnimatorListener? = null
    private fun startWinLottieAnim(endCallBack: (() -> Unit)?) {
        mDatabind.apply {
            val winMoney = gameAboutModel.netIncome
            Log.e(TAG, "本轮赢钱了--->$winMoney")
            if (winMoney <= 0) {
                endCallBack?.invoke()
                return
            }
            var duration = when (winMoney) {
                in 0 .. 1000 -> 500L
                in 1000 .. 100000 -> 600L
                else -> 700L
            }
            AnimHelper.doNumberAnim(mDatabind.tvAnimWin2, 0, (winMoney).toLong(), duration)
            showLottie(endCallBack)
        }
    }

    private fun showLottie(endCallBack: (() -> Unit)?) {
        mDatabind.apply {
            groupWinLottie.isVisible = true
            var isAnimating = false
            val onAnimationEnd: () -> Unit = {
                endCallBack?.invoke()
                groupWinLottie.isVisible = false
                isAnimating = false
            }
            //groupWinLottie没在前台显示，不要做Lottie动画
            if (!groupWinLottie.isShown) {
                LogUtils.w(TAG, "groupWinLottie is not shown at the front, ignore showLottie")
                onAnimationEnd()
                return
            }
            if (null == lottieListener) {
                lottieListener = object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        Log.e(TAG, "groupWinLottie onAnimationStart")
                        PromptSoundPlay.playWinEffect()
                        isAnimating = true
                        mDatabind.tvAnimWin2.alpha = 1f
                        txtWinMoneyLabel.alpha = 1f
                        lottieLayout.postDelayed({
                            AnimatorSet().apply {
                                playTogether(
                                    listOf(
                                        ObjectAnimator.ofFloat(
                                            mDatabind.tvAnimWin2,
                                            "alpha",
                                            1f,
                                            0f
                                        ).apply {
                                            duration = 1000 // 设置动画持续时间
                                        },
                                        ObjectAnimator.ofFloat(txtWinMoneyLabel, "alpha", 1f, 0f)
                                            .apply {
                                                duration = 1000 // 设置动画持续时间
                                            }
                                    )
                                )
                                start()
                            }
                        }, 2500)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        //LogUtils.d(TAG,"groupWinLottie onAnimationEnd")
                        onAnimationEnd()
                        isAnimating = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        //LogUtils.d(TAG,"groupWinLottie onAnimationCancel")
                        isAnimating = false
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                }
                lottieAnimView.addAnimatorListener(lottieListener)
                lottieAnimView.addOnAttachStateChangeListener(object :
                    View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(p0: View) {}
                    override fun onViewDetachedFromWindow(p0: View) {
                        //groupWinLottie播发动画一半被window移除了，lottieListener不会执行onAnimationEnd，在这里执行
                        LogUtils.w(
                            TAG,
                            "groupWinLottie is detached from window，isAnimating:${isAnimating}"
                        )
                        if (isAnimating) {
                            onAnimationEnd()
                        }
                    }
                })
            }
            lottieAnimView.playAnimation()
            lottieAnimView2.playAnimation()

        }
    }


    override fun createObserver() {
        Log.i(TAG, "createObserver------------>")
        FlowBus.with<List<GameAreaView>>(EventKey.UPDATE_ALL_AREA_VIEW)
            .register(viewLifecycleOwner) { list ->
                list.forEach {
                    allGameAreaMap[it.areaCode] = it
                    "add code=${it.areaCode},${it.id}".loge("UPDATE_ALL_AREA_VIEW")
                }
            }
        gameMassageManager?.observeAgainDoubleState(this)

        //总余额监听
        gameAboutModel.balance.observe(viewLifecycleOwner) {
            Log.e(TAG, "收到的总余额：${it},old:${mViewModel.currentMoney}, new:$it")
            if (it > mViewModel.currentMoney) {
                val start = mViewModel.currentMoney
                val end = it
                mDatabind.txtCurrentMoney.postDelayed({
                    AnimHelper.doNumberAnim(
                        mDatabind.txtCurrentMoney,
                        startNum = start,
                        endNumber = end,
                        duration1 = 600
                        //duration1 = mDatabind.lottieAnimView.duration
                    )
                }, 600)
            } else {
                mDatabind.txtCurrentMoney.text = "¥ ${it.formatRealMoney()}"
            }
            mViewModel.currentMoney = it
        }

//        //临时金额变化，用于刷新筹码可用
        gameAboutModel.tempBalance.observe(viewLifecycleOwner) {
            Log.e(TAG, "收到当前可用金额：${it}")
//            notifyBetteBean(it)
        }

        gameAboutModel.countDownSecondsLD.observe(viewLifecycleOwner) { seconds ->
            //Log.d(TAG,"countdown: seconds:$seconds")
            if (mViewModel.gameState == GameAboutModel.Stage.NEW && seconds in 1..5) {
                if (gameAboutModel.fast3MainFloatVisible.value == false)
                    PromptSoundPlay.countdownGameTip()
            }
            if (seconds == 0) {
                if (mViewModel.gameState == GameAboutModel.Stage.NEW) {
                    lifecycleScope.launch {
                        playAlphaAnimTogether(
                            arrayOf(mDatabind.txtHomeStatic),
                            floatArrayOf(0f, 1f)
                        )
                        mDatabind.txtHomeStatic.text = getString(R.string.g_f3_dealing)
                        mDatabind.txtHomeTime.isVisible = false
                        mDatabind.txtHomeUnit.isVisible = false
                        mViewModel.isClickOperation = false
                        Fast3ToastHelper.showToastNormal(
                            getString(R.string.g_home_betting_end),
                            canReplace = false
                        )
                        //防止断网状态
                        if (mViewModel.gameState != GameAboutModel.Stage.DEAL)
                            gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
                    }
                }
            } else {
                mDatabind.txtHomeTime.text = seconds.toString()
            }
        }
        mViewModel.moneyAnimCallback = object : Fast3ViewModel.MoneyAnimCallback {
            override fun startAnim(
                x: Float,
                y: Float,
                speed: Long,
                areaView: GameAreaView,
                betteBean: SelectAnnotationBean,
                endCallBack: (() -> Unit)?
            ) {
                tryMoneyAnimation(x, y, speed, areaView, betteBean, endCallBack)
            }
        }

        mViewModel.addMoneyOkViewLiveData.observe(viewLifecycleOwner) {
            val areaView = it.first
            betteViewGroup = it.second
            areaView.moneyView.let { moneyOkView ->
                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                betteViewGroup?.addView(moneyOkView, params)
                moneyOkView.translationX = 0f
                moneyOkView.translationY = 0f
                moneyOkView.translationZ = 2f
            }

            areaView.betteView.let { betteView ->
                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                betteView.translationX = 0f
                betteView.translationY = 0f
                //betteView.translationZ = 1f
                betteViewGroup?.addView(betteView, params)
            }
        }

        mViewModel.betOkClick.observe(this) {
            gameMassageManager
                ?.commitBetting { bettingState, areaLimit ->
                    bettingState.isCanGoOn(areaLimit) {
                        hiddenAnchorTop()
                    }
                }
        }

        mViewModel.betDeleteClick.observe(this) {
            cancelBetteFlyAnim()
            cancelTemBetting()
        }

        //游戏状态监听
        gameAboutModel.currentStage.observe(viewLifecycleOwner) { _ ->
            updateGameStage()
        }

        //续压、加倍状态监听
        gameAboutModel.currentAgainDoubleState.observe(viewLifecycleOwner) {
            Log.e(TAG, "续压加倍状态监听--->${it}")
            mDatabind.apply {

                when (it) {
                    GameAboutModel.AgainDoubleState.NUll, GameAboutModel.AgainDoubleState.AGAIN_CAN_NOT_50 -> {
                        ivXuya.isVisible = true
                        ivXuya.setImageResource(R.drawable.icon_xuya_gray)
                        ivMultiple2.isVisible = false
                    }

                    GameAboutModel.AgainDoubleState.AGAIN -> {
                        ivXuya.isVisible = true
                        ivXuya.setImageResource(R.drawable.icon_xuya)
                        ivMultiple2.isVisible = false
                    }

                    GameAboutModel.AgainDoubleState.DOUBLE -> {
                        ivXuya.isVisible = false
                        ivMultiple2.isVisible = true
                        ivMultiple2.setImageResource(R.drawable.icon_multiple2)
                    }

                    GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT, GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT_50 -> {
                        ivXuya.isVisible = false
                        ivMultiple2.isVisible = true
                        ivMultiple2.setImageResource(R.drawable.icon_multiple2_gray)
                    }
                }
            }
        }

        //开奖历史记录
        gameAboutModel.historyRounds.observe(viewLifecycleOwner) {
            Log.e(TAG, "开奖历史结果--->$it")
            lifecycleScope.launch {
                val adapter = mDatabind.rvHomeHistory.bindingAdapter
                adapter.models = it
            }
        }

        //下注结果
        gameAboutModel.isBettingSuccess.observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "下注结果监听--->${response}")
            if (!response.isSuccess) {
                //失败时显示delete ok按钮
                Fast3ToastHelper.showToastNormal("网络连接失败")
                showAnchorTop()
            } else {
                Fast3ToastHelper.showToastNormal(
                    getString(
                        R.string.bet_success_prompt,
                        response.money.formatRealMoney()
                    )
                )
            }
        }
        mViewModel.playAlphaAnimationLD.observe(viewLifecycleOwner, object : Observer<Boolean> {
            var animator: ObjectAnimator? = null
            override fun onChanged(play: Boolean) {
                mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size - 1)
                val layoutManager = mDatabind.rvHomeHistory.layoutManager as LinearLayoutManager
                val position = layoutManager.findLastVisibleItemPosition()
                val view = layoutManager.findViewByPosition(position)
                LogUtils.dTag(TAG, "receive playAlphaAnimationLD:$play,view:$view")
                if (view == null) return
                if (play) {
                    assert(animator == null)
                    animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f).apply {
                        duration = mViewModel.prizeAnimTime // 设置动画持续时间
                        repeatCount = 2
                        //if (mDatabind.rvHomeHistory.size == 1) 3 else mViewModel.prizeAnimCount
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                        addListener(
                            onCancel = {
                                //LogUtils.dTag(TAG,"receive playAlphaAnimationLD onCancel:animator:${animator.hashCode()}")
                                animator = null
                            },
                            onEnd = {
                                //LogUtils.dTag(TAG,"receive playAlphaAnimationLD onEnd:animator:${animator.hashCode()}")
                                animator = null
                            }
                        )
                    }
                    LogUtils.d(TAG, "receive playAlphaAnimationLD:animator:${animator.hashCode()}")
                    animator?.start()
                } else {
                    LogUtils.d(
                        TAG,
                        "receive playAlphaAnimationLD:animator:${animator.hashCode()},cancel"
                    )
                    animator?.cancel()
                    view.alpha = 1f
                }
            }
        })

        //error
        gameAboutModel.toastErrorMessage.observe(viewLifecycleOwner) { msg ->
            Fast3ToastHelper.showToastNormal(msg)
        }
    }

    /**
     * 取消临时下注
     */
    private fun cancelTemBetting() {
        hiddenAnchorTop()
        gameMassageManager?.cancelBetting { result ->
            notifyBetteBean()
            notifyMoneyOkView(result)
        }
    }

    private fun cancelBetteFlyAnim() {
        betteFlyAnimList.forEach {
            it.value.forEach {
                it.animator.cancel()
                it.isRunning = false
            }
        }
        betteFlyAnimList.clear()
    }

    /**
     * 刷新页面上注区里moneyView显示
     * 取消下注、结算时刷新中奖区域金额
     */
    private fun notifyMoneyOkView(list: List<BettingRecordBean?>?) {
        anchorMoneyView = null
        if (list.isNullOrEmpty()) {
            currentBetteAreaMap.forEach {
                it.value.removeChildViewFromParent()
            }
            currentBetteAreaMap.clear()
        } else {
            val iterator = currentBetteAreaMap.iterator()
            var hasFlag: Boolean
            while (iterator.hasNext()) {
                hasFlag = false
                val entry = iterator.next()
                run beanEach@{
                    list.forEach { bettingRecordBean ->
                        bettingRecordBean?.let {
                            if (bettingRecordBean.bettingArea.number == entry.key) {
                                bettingRecordBean.money.toString().loge("money")
                                entry.value.setShowMoney(bettingRecordBean.money)
                                hasFlag = true
                                return@beanEach
                            }
                        }
                    }
                }
                if (!hasFlag) {
                    entry.value.removeChildViewFromParent()
                    iterator.remove()
                }
            }
        }
    }

    /**
     * 开奖结果显示或者隐藏动画
     */
    private fun resultAnimation(isShowResult: Boolean) {
        mDatabind.apply {
            resultAnim?.cancel()
            mViewModel.isShowResult = isShowResult
            ivHomeRotation.rotation = if (isShowResult) 0f else 180f
            val startHeight = flRvHistory.height
            val endHeight =
                if (isShowResult) resultRvHeight else resultRvHeight - resultAnimMoveHeight
            if (resultAnim == null) {
                resultAnim = ValueAnimator.ofInt(startHeight, endHeight).apply {
                    duration = 150
                    addUpdateListener {
                        val value = it.animatedValue as Int
                        val params = flRvHistory.layoutParams
                        params?.height = value
                        flRvHistory.layoutParams = params
                    }
                }
            } else {
                resultAnim!!.setIntValues(startHeight, endHeight)
            }
            resultAnim?.start()
        }
    }


    /**
     * 投注的适配器
     */
    private fun setBetAdapter() {
        mDatabind.llShowBetList.apply {
            itemAnimator = null
            layoutManager =
                CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    CommonLinearLayoutItemDecoration(
                        spacingV = requireContext().dp2px(10),
                        start = requireContext().dp2px(8),
                        end = requireContext().dp2px(40)
                    )
                )
            }
            setup {
                addType<SelectAnnotationBean>(R.layout.item_annotation_list)
                onBind {
                    when (itemViewType) {
                        R.layout.item_annotation_list -> {
                            val binding = getBinding<ItemAnnotationListBinding>()
                            val bean = _data as SelectAnnotationBean
                            val id = if ((gameAboutModel.tempBalance.value ?: 0) < bean.money) {
                                resources.getIdentifier(
                                    "icon_shortage_" + bean.moneyPinyin,
                                    "drawable",
                                    requireContext().packageName
                                )
                            } else {
                                if (bean.select) {
                                    resources.getIdentifier(
                                        "icon_select_" + bean.moneyPinyin,
                                        "drawable",
                                        requireContext().packageName
                                    )
                                } else {
                                    resources.getIdentifier(
                                        "icon_no_" + bean.moneyPinyin,
                                        "drawable",
                                        requireContext().packageName
                                    )
                                }
                            }
                            binding.ivShowBg.setImageResource(id)
                            //Log.d(TAG, "onBind-->${layoutPosition},bean:${bean}")
                            if (bean.select) {
                                selectBetteView = binding.ivShowBg
                                if (binding.ivShowBg.translationY == 0f) {
                                    startBetteSelectAnim(
                                        binding.ivShowBg,
                                        if (isBetteUpAnimFirst) 0 else 100
                                    )
                                    //scrollBetteItemToCenter(layoutPosition)
                                }
                            } else {
                                binding.ivShowBg.translationY = 0f
                            }
                        }
                    }

                }
                onClick(R.id.ivShowBg) {
                    val bean = _data as SelectAnnotationBean
                    if (bean.select || bean.money > (gameAboutModel.tempBalance.value
                            ?: 0)
                    ) return@onClick
                    PromptSoundPlay.btnPlayMedia()
                    val models: List<SelectAnnotationBean> = models as List<SelectAnnotationBean>
                    for (data in models) {
                        data.select = bean == data
                    }
                    mViewModel.userLastSelectBetteBean = bean
                    notifyDataSetChangedSafe {
                        scrollSelectPosition2Center(true)
                    }
//                    betteScrollToCenter(layoutPosition, isScrollQuick = false)

                }
            }.models = mViewModel.noteList
        }
        //历史结果
        mDatabind.rvHomeHistory.apply {
            itemAnimator = null
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            dividerSpace(requireContext().dp2px(2), DividerOrientation.HORIZONTAL)
            setup {
                addType<RoundInfoBean>(R.layout.item_bet_history)
                onBind {
                    when (itemViewType) {
                        R.layout.item_bet_history -> {
                            getBinding<ItemBetHistoryBinding>().apply {
                                val mainTxtBean = _data as RoundInfoBean
                                mainTxtBean.performs.forEachIndexed { index, item ->
                                    val child = llShowDice.getChildAt(index) as ImageView
                                    val id = resources.getIdentifier(
                                        "icon_dice_" + item.toPinyin(),
                                        "drawable",
                                        requireContext().packageName
                                    )
                                    child.setImageResource(id)
                                }
                                txtBetNum.text = mainTxtBean.sum.toString()
                                if (mainTxtBean.isLeopard) {
                                    txtBetSize.text = getString(R.string.g_home_txt_leopard)
                                    txtBetOdd.text = getString(R.string.g_home_txt_leopard)
                                    txtBetSize.background = getDrawable(R.drawable.shape_3_01933b)
                                    txtBetOdd.background = getDrawable(R.drawable.shape_3_01933b)

                                } else {
                                    txtBetSize.background =
                                        getDrawable(if (mainTxtBean.isBig) R.drawable.shape_3_b83030 else R.drawable.shape_3_006ce4)
                                    txtBetOdd.background =
                                        getDrawable(if (mainTxtBean.isDouble) R.drawable.shape_3_b83030 else R.drawable.shape_3_006ce4)
                                    txtBetSize.text =
                                        if (mainTxtBean.isBig) getString(R.string.g_home_txt_big) else getString(
                                            R.string.g_home_txt_small
                                        )
                                    txtBetOdd.text = if (mainTxtBean.isDouble)
                                        getString(R.string.g_home_txt_double)
                                    else
                                        getString(R.string.g_home_txt_single)
                                }
                            }
                        }
                    }
                }
            }.models = gameAboutModel.historyRounds.value
            lifecycleScope.launchWhenResumed {
                if (!mDatabind.rvHomeHistory.models.isNullOrEmpty()) {
                    mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size - 1)
                }
            }
        }
    }

    private fun notifyBetteBean() {
        val money = gameAboutModel.tempBalance.value ?: 0
        var scrollIndex = -1
        mDatabind.apply {
            val selectBean = mViewModel.noteList.firstOrNull { it.select }
            if (selectBean != null) {
                if (selectBean.money > money) { //当前筹码不足
                    for (i in mViewModel.noteList.lastIndex downTo 0) {
                        mViewModel.noteList[i].select = false
                        if (scrollIndex == -1) {
                            if (mViewModel.noteList[i].money <= money) {
                                scrollIndex = i
                                mViewModel.noteList[i].select = true
                            }
                        }
                    }
                } else {
                    scrollIndex = backUserLastSelectBette(selectBean, money)
                }
            } else {
                if ((mViewModel.userLastSelectBetteBean?.money ?: 0) <= money) {
                    scrollIndex = backUserLastSelectBette(null, money)
                } else {
                    if (mViewModel.noteList[0].money <= money) {
                        mViewModel.noteList[0].select = true
                        mViewModel.userLastSelectBetteBean = mViewModel.noteList[0]
                        scrollIndex = 0
                    }
                }
            }

            notifyDataSetChangedSafe {
                scrollSelectPosition2Center(true)
            }
        }
    }

    /**
     * 取消下注筹码判断是否需要选中用户最近一次手选筹码
     */
    private fun backUserLastSelectBette(betteBean: SelectAnnotationBean?, money: Long): Int {
        var index = -1
        if (betteBean == mViewModel.userLastSelectBetteBean) return index
        if ((mViewModel.userLastSelectBetteBean?.money ?: 0) > money) return index
        for (i in 0..mViewModel.noteList.lastIndex) {
            if (mViewModel.noteList[i] == mViewModel.userLastSelectBetteBean) {
                mViewModel.noteList[i].select = true
                index = i
            } else {
                mViewModel.noteList[i].select = false
            }
        }
        return index
    }


    private fun notifyDataSetChangedSafe(action: () -> Unit) {
        mDatabind.llShowBetList.bindingAdapter.notifyItemRangeChanged(
            0,
            mViewModel.noteList.count()
        )
        if (mDatabind.llShowBetList.isComputingLayout) {
            LogUtils.e("isComputingLayout")
            mDatabind.llShowBetList.post(action)
        } else {
            action.invoke()
        }
    }

    /**
     * 执行筹码选中向上平移动画
     */
    private fun startBetteSelectAnim(showView: View, duration: Long = 100L) {
        if (duration == 0L) isBetteUpAnimFirst = false
        val anim =
            ObjectAnimator.ofFloat(showView, "translationY", -requireContext().dp2px(5).toFloat())
        anim.duration = duration
        anim.start()
    }

    /**
     * 执行筹码列表view平移出现或关闭动画
     */
    private fun startBetteRecyclerShowOrHideAnim(
        isShow: Boolean,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        duration: Long = 250L
    ) {
        mDatabind.apply {
            val recyclerAnim = ObjectAnimator.ofFloat(
                llShowBetList,
                "translationY",
                if (isShow) 0f else llShowBetList.measuredHeight.toFloat()
            )
            val againAnim = ObjectAnimator.ofFloat(
                betteAgainLayout,
                "translationX",
                if (isShow) 0f else betteAgainLayout.measuredWidth.toFloat()
            )
            AnimatorSet().apply {
                this.duration = duration
                addListener(
                    onStart = {
                        onStart?.invoke()
                    },
                    onEnd = {
                        onEnd?.invoke()
                    }
                )
                playTogether(listOf(recyclerAnim, againAnim))
                start()
            }
        }
    }

    private fun resetBetteRecyclerVisible() {
        mDatabind.apply {
            betteLayout.isVisible = true
            mDatabind.betteAgainLayout.isVisible = true

            //开奖结果x
            rlShowResult.isVisible = false
            ivHomeBg.isVisible = false
            ivHomeBgCenter.isVisible = false
            resultBgTop.isVisible = false

            llShowBetList.translationY = 0f
            betteAgainLayout.translationX = 0f
        }
    }

    /**
     * 执行游戏结果点数显示动画
     */
    private fun startCenterRoundInfoShowAnim(duration: Long = 200L, doEnd: () -> Unit) {
        mDatabind.apply {
            val leftAnimX = ObjectAnimator.ofFloat(llResultLeft, "scaleX", 0f, 1f).apply {
                this.duration = duration
            }
            val leftAnimY = ObjectAnimator.ofFloat(llResultLeft, "scaleY", 0f, 1f).apply {
                this.duration = duration
            }
            val rightAnimX = ObjectAnimator.ofFloat(llResultRight, "scaleX", 0f, 1f).apply {
                this.duration = duration
                startDelay = 500
            }
            val rightAnimY = ObjectAnimator.ofFloat(llResultRight, "scaleY", 0f, 1f).apply {
                this.duration = duration
                startDelay = 500
            }
            AnimatorSet().apply {
                play(leftAnimX).with(leftAnimY).with(rightAnimX).with(rightAnimY)
                addListener(onStart = {
                    rlShowResult.isVisible = true
                    llResultLeft.scaleX = 0f
                    llResultLeft.scaleY = 0f
                    llResultRight.scaleX = 0f
                    llResultRight.scaleY = 0f
                },
                    onEnd = { doEnd.invoke() })
                start()
            }
        }
    }

    private fun setClick() {
        mDatabind.apply {
            rvHomeHistory.setOnRecycleClickListener(object :
                ClickRecyclerView.RecyclerClickListener {
                override fun onRecyclerClick() {
                    PromptSoundPlay.btnPlayMedia()
                    resultAnimation(!mViewModel.isShowResult)
                }
            })

            flRvHistory.setOnClickListener {
                PromptSoundPlay.btnPlayMedia()
                resultAnimation(!mViewModel.isShowResult)
            }

//            bottomHistoryLayout.setOnClickListener {
//                PromptSoundPlay.btnPlayMedia()
//                resultAnimation(!mViewModel.isShowResult)
            /*val v = (gameAboutModel.balance as MutableLiveData).value
            if(v == null){
                (gameAboutModel.balance as MutableLiveData).value = 100L + Random.nextLong(100,10000)
            } else {
                (gameAboutModel.balance as MutableLiveData).value = v +  Random.nextLong(100_00,1000_00)
            }*/
//            }

            //点击更多弹出框
            llHomeMore.setOnClickListener {
                PromptSoundPlay.btnPlayMedia()
                if (homeMorePop == null) {
                    val bubbleAttach = CustomBubbleAttachPopup(requireContext())
                    bubbleAttach.customBubbleAttachListener =
                        object : CustomBubbleAttachPopup.CustomBubbleAttachListener {
                            private var gameHall: BasePopupView? = null
                            private var isDismissing = false
                            fun backMainGame() {
                                if (gameHall == null || isDismissing) return
                                isDismissing = true
                                lifecycleScope.launch {
                                    gameHall?.dismiss()
                                    showMainGame(true)
                                }
                            }

                            fun showMainGame(show: Boolean) {
                                ViewHelper.showFastViewPop(requireContext(), show)
                            }

                            override fun switchGame() {
                                lifecycleScope.launch {
                                    listOf(
                                        async { showMainGame(false) },
                                        async {
                                            //delay(20)
                                            val context = requireContext()
                                            val popupView = object : BottomPopupView(context) {
                                                override fun getImplLayoutId(): Int =
                                                    R.layout.fragment_gamehall

                                                lateinit var binding: FragmentGamehallBinding
                                                override fun onCreate() {
                                                    super.onCreate()
                                                    binding =
                                                        FragmentGamehallBinding.bind(popupImplView)
                                                    binding.lltRoot.layoutParams.also {
                                                        it.height = mDatabind.root.height
                                                        binding.lltRoot.layoutParams = it
                                                    }
                                                    initView()
                                                }

                                                fun initView() {
                                                    val views = ArrayList<View>()
                                                    repeat(1) {
                                                        val list = mutableListOf<GameHallItem>()
                                                        for (i in 1..1) {
                                                            list.add(
                                                                GameHallItem(
                                                                    "a",
                                                                    "快三",
                                                                    "3389在线"
                                                                )
                                                            )
                                                        }
                                                        val mViewBind =
                                                            ItemGamehallPageBinding.inflate(
                                                                layoutInflater,
                                                                null,
                                                                false
                                                            )
                                                        mViewBind.rvContent.itemAnimator = null
                                                        mViewBind.rvContent.dividerSpace(
                                                            requireContext().dp2px(20),
                                                            DividerOrientation.HORIZONTAL
                                                        ).setup {
                                                            it.layoutManager =
                                                                GridLayoutManager(context, 4)
                                                            addType<GameHallItem>(R.layout.item_gamehall_page_item)
                                                            onBind {
                                                                when (itemViewType) {
                                                                    R.layout.item_gamehall_page_item -> {
                                                                        getBinding<ItemGamehallPageItemBinding>().apply {
                                                                            val bean =
                                                                                _data as GameHallItem
                                                                            tvName.text = bean.name
                                                                            tvOnline.text =
                                                                                bean.onlineA
                                                                            if (bean.name == "快三") {
                                                                                root.clickNoRepeat(true) {
                                                                                    backMainGame()
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }.models = list
                                                        views.add(mViewBind.root)
                                                    }

                                                    binding.viewPagerNew.initGameViewPager2(views)
                                                    binding.magicIndicator.bindViewPagerNewGame(
                                                        binding.viewPagerNew, arrayListOf(
                                                            "热门",
                                                            /*"棋牌",
                                                            "视讯",
                                                            "捕鱼",
                                                            "体育",
                                                            "电子",*/
                                                        ),
                                                        scrollEnable = true,
                                                        action = { PromptSoundPlay.btnPlayMedia() }
                                                    )
                                                    binding.viewPagerNew.offscreenPageLimit =
                                                        mFragList.size
                                                    binding.close.clickNoRepeat(true) {
                                                        backMainGame()
                                                    }
                                                }
                                            }
                                            XPopup.Builder(context)
                                                .isTouchThrough(false)
                                                .setPopupCallback(object : SimpleCallback() {
                                                    override fun onCreated(popupView: BasePopupView?) {
                                                        super.onCreated(popupView)
                                                        gameHall = popupView
                                                        ViewHelper.isShowOtherPop = true
                                                    }

                                                    override fun beforeDismiss(popupView: BasePopupView?) {
                                                        super.onDismiss(popupView)
                                                        backMainGame()
                                                        gameHall = null
                                                        ViewHelper.isShowOtherPop = false
                                                    }
                                                })
                                                .popupAnimation(PopupAnimation.TranslateFromBottom)
                                                .navigationBarColor(android.R.color.transparent)
                                                .animationDuration(100)//默认300ms
                                                .isViewMode(true)
                                                .hasShadowBg(false) // 去掉半透明背景
                                                .enableDrag(true)
                                                .dismissOnTouchOutside(true)
                                                .asCustom(popupView)
                                                .show()
                                        }
                                    ).awaitAll()
                                }
                            }
                        }
                    homeMorePop = XPopup.Builder(requireContext())
                        .isTouchThrough(true)
                        .setPopupCallback(object : SimpleCallback() {
                            override fun onDismiss(popupView: BasePopupView?) {
                                super.onDismiss(popupView)
                                homeMorePop = null
                            }
                        })
                        .customAnimator(AlphaPopupAnimator(bubbleAttach, 100, floatArrayOf(0f, 1f)))
                        .animationDuration(100)
                        .isDestroyOnDismiss(false)
                        .atView(mDatabind.llHomeMore)
                        .navigationBarColor(android.R.color.transparent)
                        .hasShadowBg(false) // 去掉半透明背景
                        .offsetX((-8).dp2px)
                        .offsetY((5).dp2px)
                        .asCustom(bubbleAttach)
                    homeMorePop?.show()
                } else {
                    homeMorePop?.dismiss()
                }
            }
            //加倍
            ivMultiple2.setOnClickListener {
                if (gameAboutModel.currentAgainDoubleState.value == GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT_50) {
                    Fast3ToastHelper.showToastNormal(getString(R.string.money_insufficient_50))
                    return@setOnClickListener
                }
                if (gameAboutModel.currentAgainDoubleState.value == GameAboutModel.AgainDoubleState.DOUBLE
                    || gameAboutModel.currentAgainDoubleState.value == GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT
                ) {
                    if (gameAboutModel.currentAgainDoubleState.value == GameAboutModel.AgainDoubleState.DOUBLE) {
                        PromptSoundPlay.playGoldCoinAudio()
                        AnimHelper.doScaleAnimRecovery(ivMultiple2)
                    }
                    gameMassageManager
                        ?.doubleBetting { bettingState, map, areaLimit ->
                            bettingState.isCanGoOn(areaLimit) {
                                if (!map.isNullOrEmpty()) {
                                    map.forEach {
                                        it.value.let { record ->
                                            if (record != null) {
                                                if (currentBetteAreaMap.containsKey(record.bettingArea.number)) {
                                                    currentBetteAreaMap[record.bettingArea.number]?.setShowMoney(
                                                        record.money
                                                    )
                                                } else {
                                                    //addview
                                                }
                                            }
                                        }
                                    }
                                    "anchorView = $anchorMoneyView".loge()
                                    if (anchorMoneyView == null) {
                                        if (currentBetteAreaMap.values.isNotEmpty()) {
                                            updateAnchorView(currentBetteAreaMap.values.last())
                                        }
                                    } else {
                                        showAnchorTop()
                                    }
                                } else {
                                    //余额不足
                                }
                            }
                        }
                }
            }
            //续压
            ivXuya.setOnClickListener {
                if (gameAboutModel.currentAgainDoubleState.value == GameAboutModel.AgainDoubleState.AGAIN_CAN_NOT_50) {
                    Fast3ToastHelper.showToastNormal(getString(R.string.money_insufficient_50))
                    return@setOnClickListener
                }
                if (gameAboutModel.currentAgainDoubleState.value != GameAboutModel.AgainDoubleState.AGAIN) {
                    return@setOnClickListener
                }
                PromptSoundPlay.playGoldCoinAudio()
                val map = gameMassageManager?.againBetting()
                map.toString().loge("again3")
                if (!map.isNullOrEmpty()) {
                    map.forEach {
                        allGameAreaMap[it.key.number]?.let { areaView ->
                            areaView.againAdd(it)

                            currentBetteAreaMap[it.key.number] = areaView
                            areaView.setShowMoney(it.value.money)
                        }
                    }
                    if (currentBetteAreaMap.containsKey(gameAboutModel.lastBetting?.number)) {
                        currentBetteAreaMap[gameAboutModel.lastBetting?.number]?.let {
                            "lastbettting = ${gameAboutModel.lastBetting?.number}".loge()
                            "lastbettting areaCode = ${it.areaCode}".loge()
                            updateAnchorView(it)
                        }
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        LogUtils.d(TAG, "onDetach~~~~~~~~~~~~~~")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d(TAG, "onDestroyView~~~~~~~~~~~~~~")
    }

    /**
     * 关闭页面
     */
    override fun onDestroy() {
        LogUtils.d(TAG, "onDestroy~~~~~~~~~~~~~~")
        //关闭的时候要把这个赋值为0选择
        mViewModel.noteList.forEach {
            it.select = false
        }
        mViewModel.noteList[0].select = true
        //清空临时的

        mViewModel.clear()
        //关闭倒计时
        //关闭动画
        resultAnim?.cancel()
        super.onDestroy()
    }


    /**
     * 执行动画  isCentered如果是true就是可以超出父类的
     */
    fun tryMoneyAnimation(
        x: Float,
        y: Float,
        speed: Long,
        areaView: GameAreaView,
        betteBean: SelectAnnotationBean,
        endCallBack: (() -> Unit)? = null
    ) {
        PromptSoundPlay.playGoldCoinAudio()
        if (anchorMoneyView != null && areaView.moneyView != anchorMoneyView) {
            hiddenAnchorTop()
        }
        val isFirstAdd = !currentBetteAreaMap.containsKey(areaView.areaCode)
        updateAnchorView(areaView)
        val betList = mDatabind.llShowBetList.models as List<SelectAnnotationBean>
        val selectedPosition = betList.indexOf(betteBean)
        scrollSelectPosition2Center(false) {
            notifyBetteBean()
            safeBetteFly(selectedPosition) { betteView ->
                betteView?.let {
                    startMoneyAnimation(
                        x,
                        y,
                        speed,
                        areaView,
                        it,
                        betteBean,
                        isFirstAdd,
                        endCallBack
                    )
                }
            }
        }

    }

    private fun safeBetteFly(position: Int, action: (View?) -> Unit) {
        if (!isBetteItemVisible(position)) {
            mDatabind.llShowBetList.post {
                val betteView =
                    mDatabind.llShowBetList.layoutManager?.findViewByPosition(position)
                action.invoke(betteView)
            }
        } else {
            val betteView = mDatabind.llShowBetList.layoutManager?.findViewByPosition(position)

            action.invoke(betteView)
        }
    }

    private var betteViewGroup: ViewGroup? = null

    private fun startMoneyAnimation(
        x: Float,
        y: Float,
        speed: Long,
        areaView: GameAreaView,
        jettonView: View,
        betteBean: SelectAnnotationBean,
        isFirstAdd: Boolean,
        endCallBack: (() -> Unit)?
    ) {
        //贝塞尔曲线中间过程的点的坐标
        val viewPagerLocation = mDatabind.viewPagerNew.locationOnScreen
        val jettonViewLocation = jettonView.locationOnScreen
        val targetLocation = areaView.betteView.ivShowBg.locationOnScreen

        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线))
        val betImageView = ImageView(requireContext()).apply {
            setImageResource(
                resources.getIdentifier(
                    "icon_select_" + betteBean.moneyPinyin,
                    "drawable",
                    requireContext().packageName
                )
            )
            translationZ = 3f
        }
        val betteSize = jettonView.measuredWidth
        val targetSize = 32.dp2px
        val params = FrameLayout.LayoutParams(betteSize, betteSize)
        params.topMargin = jettonViewLocation[1] - viewPagerLocation[1] - 5.dp2px
        params.leftMargin = jettonViewLocation[0] - viewPagerLocation[0] - 2.dp2px
        betImageView.layoutParams = params
        betteViewGroup?.addView(betImageView)

        val scale = targetSize.toFloat() / betteSize.toFloat()
        val offset = (betteSize - targetSize) / 2
        val animator: ViewPropertyAnimator = betImageView.animate()
            .scaleX(scale)
            .scaleY(scale)
            .translationX((targetLocation[0] - jettonViewLocation[0] - offset + 2.dp2px).toFloat())
            .translationY((targetLocation[1] - jettonViewLocation[1] - offset + 5.dp2px).toFloat())
            .setDuration(300)
        animator.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                areaView.betteView.translationZ = 4f
                if (betteFlyAnimList.containsKey(areaView)) {
                    betteFlyAnimList[areaView]?.add(BetteFlyData(animator, true))
                } else {
                    betteFlyAnimList[areaView] = mutableListOf(BetteFlyData(animator, true))
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                //动画结束
                endCallBack?.invoke()
                // 把移动的图片imageview从父布局里移除
                if (betImageView.isAdd()) {
                    (betImageView.parent as ViewGroup).removeView(betImageView)
                }
                betteFlyAnimList[areaView]?.find { it.animator == animator }?.isRunning = false
                if (betteFlyAnimList.containsKey(areaView)) {
                    if (!betteFlyAnimList[areaView]!!.last().isRunning) {
                        areaView.betteView.translationZ = 0f
                    }
                } else {
                    areaView.betteView.translationZ = 0f
                }


                if (!isFirstAdd) {
                    AnimHelper.doScaleAnimRecovery(areaView.betteView.ivShowBg, duration = 100)
                }
            }
        })
        animator.start()

//        val path = Path()
//        path.moveTo(startX, startY)
//        path.lineTo(x, y)
////
//        val mPathMeasure = PathMeasure(path, false)
//
//        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
//        val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure.length).apply {
//            addUpdateListener { animation ->
//                val value = animation.animatedValue as Float
//                // ★★★★★获取当前点坐标封装到mCurrentPosition
//                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
//                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
//                mPathMeasure.getPosTan(value, mCurrentPosition, null)
//
//                // 筹码图片偏移
//                betImageView.translationX = mCurrentPosition[0]
//                betImageView.translationY = mCurrentPosition[1] - viewPagerLocation[1]
//            }
//
//            addListener(
//                onStart = {
//                    if (areaView.betteView.translationZ == 0f) {
//                        areaView.betteView.translationZ = 4f
//                    }
//                },
//                onEnd = {
//                    //动画结束
//                    endCallBack?.invoke()
//                    // 把移动的图片imageview从父布局里移除
//                    if (betImageView.isAdd()) {
//                        (betImageView.parent as ViewGroup).removeView(betImageView)
//                    }
//
//                    if (betteFlyAnimList.containsKey(areaView)) {
//                        if (!betteFlyAnimList[areaView]!!.last().isRunning) {
//                            areaView.betteView.translationZ = 0f
//                        }
//                    } else {
//                        areaView.betteView.translationZ = 0f
//                    }
//
//                    if (!isFirstAdd) {
//                        AnimHelper.doScaleAnimRecovery(areaView.betteView.ivShowBg, duration = 100)
//                    }
//                })
//        }
//
//        valueAnimator.duration = speed
//        valueAnimator.interpolator = LinearInterpolator()
//        if (betteFlyAnimList.containsKey(areaView)) {
//            betteFlyAnimList[areaView]?.add(valueAnimator)
//        } else {
//            betteFlyAnimList[areaView] = mutableListOf(valueAnimator)
//        }
//        valueAnimator.start()
    }

    private fun updateAnchorView(areaView: GameAreaView) {
        hiddenAnchorTop()
        anchorMoneyView = areaView.moneyView
        currentBetteAreaMap[areaView.areaCode] = areaView
        mDatabind.tempTouch.setAnchorMoneyView(areaView.moneyView)
        showAnchorTop()
    }

    private fun hiddenAnchorTop() {
        anchorMoneyView?.hiddenTop()
    }

    private fun showAnchorTop() {
        anchorMoneyView?.showTop()
        reLocatePage()
    }

    private fun reLocatePage() {
        anchorMoneyView?.let {
            val index = it.pageIndex
            if (mDatabind.viewPagerNew.currentItem != index) {
                mDatabind.viewPagerNew.currentItem = index
            }
        }
    }

    private fun betteScrollToCenter(
        position: Int,
        scrollEnd: ((View) -> Unit)? = null,
        isScrollQuick: Boolean = true
    ) {
//        mDatabind.apply {
//            val layoutManager = llShowBetList.layoutManager as CenterLayoutManager
//            val finallyView = layoutManager.findViewByPosition(position)
//            if (finallyView == null) {
//                llShowBetList.scrollToPosition(position)
//            }
//            llShowBetList.post {
//                layoutManager.smoothScrollToPosition(
//                    llShowBetList,
//                    if (isScrollQuick) null else RecyclerView.State(),
//                    position
//                )
//
//                layoutManager.findViewByPosition(position)?.let {
//                    scrollEnd?.invoke(it)
//                }
//            }
//        }
//        scrollSelectPosition2Center(position)
    }

    private fun scrollSelectPosition2Center(
        isSmooth: Boolean = true,
        action: (() -> Unit)? = null
    ) {
        mDatabind.apply {
            val selectedIndex = mViewModel.noteList.indexOfFirst { it.select }
            if (!isBetteItemVisible(selectedIndex)) {
                llShowBetList.scrollToPosition(selectedIndex)
            }
            llShowBetList.post {
                selectBetteView?.let { selectedBetteView ->
                    val chipsLocation = selectedBetteView.locationOnScreen
                    val targetX: Int = selectedBetteView.getRootView().measuredWidth / 2
                    val chipsX: Int = chipsLocation[0] + (selectedBetteView.width / 2)
                    if (chipsX != targetX) {
                        if (chipsX > targetX && !llShowBetList.canScrollHorizontally(1)) {
                            action?.invoke()
                            return@post
                        }
                        if (chipsX < targetX && !llShowBetList.canScrollHorizontally(-1)) {
                            action?.invoke()
                            return@post
                        }
                        if (isSmooth) {
                            (llShowBetList.layoutManager as CenterLayoutManager).smoothScrollToPosition(
                                llShowBetList,
                                null,
                                selectedIndex
                            )
//                            llShowBetList.smoothScrollBy(chipsX - targetX, 0)
                        } else {
                            llShowBetList.scrollBy(chipsX - targetX - 1.dp2px, 0)

                        }
                    }
                    action?.invoke()
                }
            }
//            isNeedSmoothScroll(position) { targetView, smooth ->
//                targetView?.let { selectedBetteView ->
//                    val chipsLocation = selectedBetteView.locationOnScreen
//                    val targetX: Int = selectedBetteView.getRootView().measuredWidth / 2
//                    val chipsX: Int = chipsLocation[0] + (selectedBetteView.width / 2)
//                    if (chipsX != targetX) {
//                        if (chipsX > targetX && !llShowBetList.canScrollHorizontally(1)) {
//                            return@isNeedSmoothScroll
//                        }
//                        if (chipsX < targetX && !llShowBetList.canScrollHorizontally(-1)) {
//                            return@isNeedSmoothScroll
//                        }
//                        if (isSmooth) {
//                            llShowBetList.smoothScrollBy(chipsX - targetX, 0)
//                        } else {
//                            llShowBetList.scrollBy(chipsX - targetX, 0)
//                        }
//                    }
//                }
//
//            }
        }
    }

    private fun isBetteItemVisible(position: Int): Boolean {
        mDatabind.apply {
            val layoutManager = llShowBetList.layoutManager as LinearLayoutManager
            return position in layoutManager.findFirstCompletelyVisibleItemPosition()..layoutManager.findLastCompletelyVisibleItemPosition()
        }
    }
}
