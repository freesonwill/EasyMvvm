package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
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
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.EventConst
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.databinding.FragFast3HomeBinding
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.databinding.ItemBetHistoryBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
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
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.ext.CommonExt.isCanGoOn
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.utils.ext.ViewExt.getDrawable
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.utils.tool.measureView
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.animator.EmptyAnimator
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("SetTextI18n")
class Fast3MainFragment : BaseVmDbFragment<Fast3ViewModel, FragFast3HomeBinding>() {
    companion object {
        const val TAG = "Fast3MainFragment"
    }

    private var mFragList = ArrayList<Fragment>()

    //是否执行关闭动画
    var isExecuteClose: Boolean = true
    private var isBetteUpAnimFirst = true

    // 定义属性动画常量
    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.4f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.4f, 1.0f)

    private var homeMorePop: BasePopupView? = null
    private var resultAnim: ValueAnimator? = null
    private var resultRvHeight = -1
    private var resultAnimMoveHeight = -1
    private var anchorMoneyView: MoneyOKView? = null

    //<areaCode,<money,View>>
    private val currentBetteAreaMap by lazy { mutableMapOf<Int, GameAreaView>() }
    private val allGameAreaMap by lazy { mutableMapOf<Int, GameAreaView>() }
    private val betteFlyAnimList by lazy { mutableListOf<ValueAnimator>() }

    //==================================== Method ===============================================//
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.model = mViewModel
        mDatabind.bottomLayout.setOnTouchListener { _, _ -> true }
        mDatabind.resultClickView.setOnClickListener { } //屏蔽底部recycler点击
        mDatabind.llHomeVideo.setOnClickListener {
            /* mDatabind.groupWinLottie.isVisible = true
             AnimHelper.doNumberAnim(mDatabind.tvAnimWin2,0, 987654399,600)
             showLottie {  }*/
            /*AnimHelper.doNumberAnim(
                mDatabind.txtCurrentMoney,
                7865458958,5
            )*/
        }
        mViewModel.navigationBarHeight.value = requireContext().navigationBarHeight



        Fast3ToastHelper.init(requireContext(), mDatabind.centerLayout).let {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                var startTime:Long = 0
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    startTime = System.currentTimeMillis()
                }

                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)
                    (System.currentTimeMillis() - startTime).let{ LogUtils.d(TAG,"Fast3MainFragment load costMills:$it") }
                }
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    Fast3ToastHelper.destroy()
                }
            })
        }

        lifecycleScope.launchWhenResumed {
            val startTime = System.currentTimeMillis()
            //viewpager
            mFragList.add(DXDSFragment(mViewModel))
            mFragList.add(SingleDiceFragment(mViewModel))
            mFragList.add(SumTotalFragment(mViewModel))
            mFragList.add(PairsDiceFragment(mViewModel))
            mFragList.add(LeopardFragment(mViewModel))
            (System.currentTimeMillis() - startTime).let{ LogUtils.d(TAG,"Fast3MainFragment load costMills1:$it") }
            mDatabind.viewPagerNew.initGameViewPager(
                childFragmentManager, mFragList, arrayListOf(
                    getString(R.string.g_home_txt_default),
                    getString(R.string.g_home_tab_single),
                    getString(R.string.g_home_tab_sum),
                    getString(R.string.g_home_tab_double),
                    getString(R.string.g_home_tab_leopard)
                )
            )
            (System.currentTimeMillis() - startTime).let{ LogUtils.d(TAG,"Fast3MainFragment load costMills2:$it") }
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
            (System.currentTimeMillis() - startTime).let{ LogUtils.d(TAG,"Fast3MainFragment load costMills3:$it") }
        }
        setBetAdapter()
        setClick()
        measureHistoryRvHeight()
    }

    override fun lazyLoadData() {
    }

    override fun initData() {
        //获取当前余额
        mDatabind.txtCurrentMoney.text = "¥ ${mViewModel.currentMoney.formatRealMoney()}"
        mDatabind.txtHomeTime.text = mViewModel.homeTimeSeconds.value.toString()
        lifecycleScope.launchWhenResumed {
            //开始下注
            Log.d(TAG, "initData startBetting")
            delay(200)
            updateGameStage()
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
                    resultAnimMoveHeight = this.measuredHeight
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
            mViewModel.isClickOperation = it == GameAboutModel.Stage.NEW
            mDatabind.txtHomeTime.isVisible = it == GameAboutModel.Stage.NEW
            mDatabind.txtHomeUnit.isVisible = it == GameAboutModel.Stage.NEW
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
            Log.d(TAG, "updateGameStage-->${it},countDown:${mViewModel.countDown}")
        }
    }


    private fun onStartBetting() {
        lifecycleScope.launch {
            //开始语音
            mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_txt_please)
            Log.d(TAG, "onStartBetting, isCountDownStart:${mViewModel.isCountDownStart}")
            if (mViewModel.isCountDownStart) {
                Fast3ToastHelper.showToastNormal(getString(R.string.g_home_betting_begin), 2000)
                //PromptSoundPlay.startGameTip(requireContext())
                //下注筹码向上升起动画
                startBetteRecyclerShowOrHideAnim(isShow = true, onStart = {
                    //筹码
                    mDatabind.betteLayout.isVisible = true
                    mDatabind.betteAgainLayout.isVisible = true

                    //开奖结果
                    mDatabind.rlShowResult.isVisible = false
                    mDatabind.ivHomeBg.isVisible = false
                    mDatabind.ivHomeBgCenter.isVisible = false
                })
            }
            //重置注区筹码
            notifyMoneyOkView(null)
        }
    }

    private fun onStartSetting() { //开始结算
        val roundInfo: RoundInfoBean? = gameAboutModel.currentSettleResult
        lifecycleScope.launch {
            mDatabind.apply {
                //Fast3ToastHelper.showToastNormal(getString(R.string.g_home_setting_begin), 1000)
                txtHomeStatic.text = resources.getString(R.string.g_f3_setting)
                Log.e(TAG, "结算item" + roundInfo.toString())
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
                    ivBetSize.setImageResource(if (isBig) R.drawable.icon_home_result_big else R.drawable.icon_home_result_small)
                    ivBetOdd.setImageResource(if (isDouble) R.drawable.icon_home_result_double else R.drawable.icon_home_result_single)
                }


                //开奖结果显示动画
                startGameResultShowAnim {
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
     * 开奖中
     */
    private fun onStartDrawing() {
        lifecycleScope.launch {//关闭
            if (mViewModel.isCountDownStart) {
                //PromptSoundPlay.endGameTip(requireContext())
                //Fast3ToastHelper.showToastNormal(getString(R.string.g_home_drawing_begin), 1000)
            }
            Fast3ToastHelper.showToastNormal(getString(R.string.g_home_betting_end))
            cancelBetteFlyAnim()
            cancelTemBetting()
            //开奖时取消临时下注的
            mDatabind.apply {
                txtHomeStatic.text = getString(R.string.g_f3_dealing)
                //隐藏筹码牌动画
                startBetteRecyclerShowOrHideAnim(isShow = false, onEnd = {
                    //注区
                    betteLayout.isInvisible = true
                    betteAgainLayout.isVisible = false

                    //开奖结果
                    ivHomeBg.isVisible = true
                    ivHomeBgCenter.isVisible = true

                })
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
            AnimHelper.doNumberAnim(mDatabind.tvAnimWin2, 0, (winMoney).toLong(), 600)
            /*val originTxt = "¥" + winMoney.formatRealMoney()
            mDatabind.tvAnimWin.setText(originTxt.replace(Regex("[0-9]"), "0"), false)
            tvAnimWin.setText("¥${winMoney.formatRealMoney()}", true)*/
            showLottie(endCallBack)
        }
    }

    private fun showLottie(endCallBack: (() -> Unit)?) {
        mDatabind.apply {
            groupWinLottie.isVisible = true
            if (null == lottieListener) {
                lottieListener = object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        PromptSoundPlay.playWinEffect()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        endCallBack?.invoke()
                        groupWinLottie.isVisible = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                }
                lottieAnimView.addAnimatorListener(lottieListener)
            }
            lottieAnimView.playAnimation()
        }
    }

    override fun createObserver() {
        Log.i(TAG, "createObserver------------>")
        FlowBus.with<List<GameAreaView>>(EventConst.UPDATE_ALL_AREA_VIEW)
            .register(viewLifecycleOwner) { list ->
                list.forEach {
                    allGameAreaMap[it.areaCode] = it
                    "add code=${it.areaCode},${it.id}".loge("UPDATE_ALL_AREA_VIEW")
                }
            }
        GameSocketManager.getInstance()?.getGameService()?.observeAgainDoubleState(this)

        //总余额监听
        gameAboutModel.balance.observe(viewLifecycleOwner) {
            Log.e(TAG, "收到的总余额：${it},old:${mViewModel.currentMoney}, new:$it")
            if (it > mViewModel.currentMoney) {
                AnimHelper.doNumberAnim(
                    mDatabind.txtCurrentMoney,
                    startNum = mViewModel.currentMoney,
                    endNumber = it
                )
            } else {
                mDatabind.txtCurrentMoney.text = "¥ ${it.formatRealMoney()}"
            }
            mViewModel.currentMoney = it
        }

        //临时金额变化，用于刷新筹码可用
        gameAboutModel.tempBalance.observe(viewLifecycleOwner) {
            Log.e(TAG, "收到当前可用金额：${it}")
            notifyBetteBean(it)
        }

        gameAboutModel.countDownSecondsLD.observe(viewLifecycleOwner) { seconds ->
            //Log.d(TAG,"countdown: seconds:$seconds")
            if (mViewModel.gameState == GameState.Betting && seconds in 1..5) {
                PromptSoundPlay.countdownGameTip(requireContext())
            }
            if (seconds == 0) {
                if (mViewModel.gameState == GameState.Betting) {
                    mDatabind.txtHomeStatic.text = getString(R.string.g_f3_dealing)
                    mDatabind.txtHomeTime.isVisible = false
                    mDatabind.txtHomeUnit.isVisible = false
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
        mViewModel.betOkClick.observe(this) {
            hiddenAnchorTop()
            GameSocketManager.getInstance()?.getGameService()?.commitBetting { bettingState, bean ->
                bettingState.isCanGoOn(null) {}
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
                Log.d(TAG, "receive playAlphaAnimationLD:$play,view:$view")
                if (view == null) return
                if (play) {
                    animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f).apply {
                        duration = mViewModel.prizeAnimTime // 设置动画持续时间
                        repeatCount = 2
                        //if (mDatabind.rvHomeHistory.size == 1) 3 else mViewModel.prizeAnimCount
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    }
                    Log.d(TAG, "receive playAlphaAnimationLD:${animator}")
                    animator?.start()
                } else {
                    Log.d(TAG, "receive playAlphaAnimationLD:${animator}")
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
        GameSocketManager.getInstance()?.getGameService()?.cancelBetting { result ->
            notifyMoneyOkView(result)
        }
    }

    private fun cancelBetteFlyAnim() {
        betteFlyAnimList.forEach {
            it.cancel()
        }
        betteFlyAnimList.clear()
    }

    /**
     * 刷新页面上注区里moneyView显示
     * 取消下注、结算时刷新中奖区域金额
     */
    private fun notifyMoneyOkView(list: List<BettingRecordBean>?) {
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
                        if (bettingRecordBean.bettingArea.number == entry.key) {
                            bettingRecordBean.money.toString().loge("money")
                            entry.value.setShowMoney(bettingRecordBean.money)
                            hasFlag = true
                            return@beanEach
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
            layoutManager = CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                    if (bean.select || bean.money > (gameAboutModel.tempBalance.value ?: 0)) return@onClick
                    PromptSoundPlay.btnPlayMedia(requireContext())
                    val models: List<SelectAnnotationBean> = models as List<SelectAnnotationBean>
                    for (data in models) {
                        data.select = bean == data
                    }
                    mViewModel.userLastSelectBetteBean = bean
                    (mDatabind.llShowBetList.layoutManager as CenterLayoutManager).smoothScrollToPosition(
                        mDatabind.llShowBetList,
                        RecyclerView.State(),
                        layoutPosition
                    )
                    notifyItemRangeChanged(0, modelCount)

                }
            }.models = mViewModel.noteList
        }

        //历史结果
        mDatabind.rvHomeHistory.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                                    txtBetSize.background = getDrawable(R.drawable.shape_3_006ce4)
                                    txtBetOdd.background = getDrawable(R.drawable.shape_3_b83030)
                                    txtBetSize.text =
                                        if (mainTxtBean.isBig) getString(R.string.g_home_txt_big) else getString(
                                            R.string.g_home_txt_small
                                        )
                                    txtBetOdd.text =
                                        if (mainTxtBean.isDouble)
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

    private fun notifyBetteBean(money: Long) {
        mDatabind.apply {
            val selectBean = mViewModel.noteList.firstOrNull { it.select }
            if (selectBean != null) {
                if (selectBean.money > money) { //当前筹码不足
                    var selectedIndex = -1
                    for (i in mViewModel.noteList.lastIndex downTo 0) {
                        mViewModel.noteList[i].select = false
                        if (selectedIndex == -1) {
                            if (mViewModel.noteList[i].money <= money) {
                                selectedIndex = i
                                mViewModel.noteList[i].select = true
                            }
                        }
                    }
                    if (selectedIndex >= 0) {
                        (llShowBetList.layoutManager as CenterLayoutManager).smoothScrollToPosition(
                            mDatabind.llShowBetList,
                            RecyclerView.State(),
                            selectedIndex
                        )
                    }
                } else {
                    backUserLastSelectBette(selectBean)
                }
            } else {
                if ((mViewModel.userLastSelectBetteBean?.money ?: 0) <= money) {
                    backUserLastSelectBette(null)
                } else {
                    if (mViewModel.noteList[0].money <= money) {
                        mViewModel.noteList[0].select = true
                        mViewModel.userLastSelectBetteBean = mViewModel.noteList[0]
                        (llShowBetList.layoutManager as CenterLayoutManager).smoothScrollToPosition(
                            mDatabind.llShowBetList,
                            RecyclerView.State(),
                            0
                        )
                    }
                }
            }
            llShowBetList.bindingAdapter.notifyItemRangeChanged(0, mViewModel.noteList.count())
        }
    }

    /**
     * 取消下注筹码判断是否需要选中用户最近一次手选筹码
     */
    private fun backUserLastSelectBette(betteBean: SelectAnnotationBean?) {
        if (betteBean == mViewModel.userLastSelectBetteBean) return
        var index = 0
        for (i in 0..mViewModel.noteList.lastIndex) {
            if (mViewModel.noteList[i] == mViewModel.userLastSelectBetteBean) {
                mViewModel.noteList[i].select = true
                index = i
            } else {
                mViewModel.noteList[i].select = false
            }
        }
        (mDatabind.llShowBetList.layoutManager as CenterLayoutManager).smoothScrollToPosition(
            mDatabind.llShowBetList,
            RecyclerView.State(),
            index
        )
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
        duration: Long = 200L
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

    /**
     * 执行游戏结果点数显示动画
     */
    private fun startGameResultShowAnim(duration: Long = 200L, doEnd: () -> Unit) {
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
                    PromptSoundPlay.btnPlayMedia(requireContext())
                    resultAnimation(!mViewModel.isShowResult)
                }
            })

            flRvHistory.setOnClickListener {
                PromptSoundPlay.btnPlayMedia(requireContext())
                resultAnimation(!mViewModel.isShowResult)
            }

//            bottomHistoryLayout.setOnClickListener {
//                PromptSoundPlay.btnPlayMedia(requireContext())
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
                PromptSoundPlay.btnPlayMedia(requireContext())
                if (homeMorePop == null) {
                    val bubbleAttach = CustomBubbleAttachPopup(requireContext())
                    bubbleAttach.customBubbleAttachListener = object : CustomBubbleAttachPopup.CustomBubbleAttachListener {
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
                                    showMainGame(false)
                                    delay(100)
                                    val context = requireContext()
                                    val popupView = object : BottomPopupView(context) {
                                        override fun getImplLayoutId(): Int = R.layout.fragment_gamehall

                                        lateinit var binding: FragmentGamehallBinding
                                        override fun onCreate() {
                                            super.onCreate()
                                            binding = FragmentGamehallBinding.bind(popupImplView)
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
                                                    list.add(GameHallItem("a", "快三", "3389在线"))
                                                }
                                                val mViewBind = ItemGamehallPageBinding.inflate(
                                                    layoutInflater,
                                                    null,
                                                    false
                                                )
                                                mViewBind.rvContent.itemAnimator = null
                                                mViewBind.rvContent.dividerSpace(
                                                    requireContext().dp2px(20),
                                                    DividerOrientation.HORIZONTAL
                                                ).setup {
                                                    it.layoutManager = GridLayoutManager(context, 4)
                                                    addType<GameHallItem>(R.layout.item_gamehall_page_item)
                                                    onBind {
                                                        when (itemViewType) {
                                                            R.layout.item_gamehall_page_item -> {
                                                                getBinding<ItemGamehallPageItemBinding>().apply {
                                                                    val bean = _data as GameHallItem
                                                                    tvName.text = bean.name
                                                                    tvOnline.text = bean.onlineA
                                                                    if (bean.name == "快三") {
                                                                        root.clickNoRepeat {
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
                                            binding.viewPagerNew.offscreenPageLimit = mFragList.size
                                            binding.close.clickNoRepeat {
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
                                        .animationDuration(150)//默认300ms
                                        .isViewMode(true)
                                        .hasShadowBg(false) // 去掉半透明背景
                                        .enableDrag(true)
                                        .dismissOnTouchOutside(true)
                                        .asCustom(popupView)
                                        .show()
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
                        .customAnimator(EmptyAnimator(bubbleAttach, 0))
                        .atView(mDatabind.llHomeMore)
                        .navigationBarColor(android.R.color.transparent)
                        .hasShadowBg(false) // 去掉半透明背景
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
                    PromptSoundPlay.playAudio()
                    GameSocketManager.getInstance()?.getGameService()
                        ?.doubleBetting { bettingState, map, areaLimit ->
                            bettingState.isCanGoOn(areaLimit) {
                                if (!map.isNullOrEmpty()) {
                                    map.forEach {
                                        it.value.let { record ->
                                            if (currentBetteAreaMap.containsKey(record.bettingArea.number)) {
                                                currentBetteAreaMap[record.bettingArea.number]?.setShowMoney(
                                                    record.money
                                                )
                                            } else {
                                                //addview
                                            }
                                        }
                                    }
                                    "anchorView = $anchorMoneyView".loge()
                                    showAnchorTop()
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
                PromptSoundPlay.playAudio()
                val map = GameSocketManager.getInstance()?.getGameService()?.againBetting()
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
        Log.d(TAG, "onDetach~~~~~~~~~~~~~~")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView~~~~~~~~~~~~~~")
    }

    /**
     * 关闭页面
     */
    override fun onDestroy() {
        Log.d(TAG, "onDestroy~~~~~~~~~~~~~~")
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
        speed: Long = 300,
        areaView: GameAreaView,
        betteBean: SelectAnnotationBean,
        endCallBack: (() -> Unit)? = null
    ) {
        //PromptSoundPlay.goldPlayMedia(this)
        //PromptSoundPlay.goldPlayMediaNew(this)
        PromptSoundPlay.playAudio(requireContext())
        //获取选中的筹码所在的position
        val betList = mDatabind.llShowBetList.models as List<SelectAnnotationBean>
        val selectedPosition = betList.indexOf(betteBean)
        val layoutManager = mDatabind.llShowBetList.layoutManager as LinearLayoutManager
        var finallyView = layoutManager.findViewByPosition(selectedPosition)

        //判断选择的筹码是不是在屏幕外面
        if (finallyView != null) {
            startMoneyAnimation(x, y, speed, areaView, finallyView, endCallBack)
        } else {
            scrollToItemAndPerformAction(mDatabind.llShowBetList, selectedPosition) {
                finallyView = layoutManager.findViewByPosition(selectedPosition)
                finallyView?.let {
                    startMoneyAnimation(x, y, speed, areaView, it, endCallBack)
                }
            }
        }
    }


    private fun startMoneyAnimation(
        x: Float,
        y: Float,
        speed: Long = 300,
        areaView: GameAreaView,
        jettonView: View,
        endCallBack: (() -> Unit)?
    ) {
        if (anchorMoneyView != null && areaView.moneyView != anchorMoneyView) {
            hiddenAnchorTop()
        }
        updateAnchorView(areaView)

        //贝塞尔曲线中间过程的点的坐标
        val rootLocation = mDatabind.rlRoot.locationOnScreen
        val mCurrentPosition = FloatArray(2)

        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线))
        val betImageView = ImageView(requireContext())
        betImageView.setImageDrawable(jettonView.findViewById<ImageView>(R.id.ivShowBg).drawable)
        val params = ConstraintLayout.LayoutParams(
            requireContext().dp2px(32),
            requireContext().dp2px(32)
        )
        mDatabind.rlRoot.addView(betImageView, params)

        //正式开始计算动画开始/结束的坐标
        val location = IntArray(2)
        jettonView.getLocationOnScreen(location)
        val startX: Float =
            location[0].toFloat() + jettonView.measuredWidth / 2 - requireContext().dp2px(16)
        val startY: Float = location[1].toFloat()

        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(x, y)

//        val path = Path()
//        移动到起始点（贝塞尔曲线的起点）
//        path.moveTo(startX, startY)
//        使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
//        path.quadTo((startX + x) / 2, startY, x, y)
//
        val mPathMeasure = PathMeasure(path, false)

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure.length).apply {
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                mPathMeasure.getPosTan(value, mCurrentPosition, null)

                // 筹码图片偏移
                betImageView.translationX = mCurrentPosition[0]
                betImageView.translationY = mCurrentPosition[1] - rootLocation[1]
            }

            addListener(onEnd = {
                //动画结束
                endCallBack?.invoke()
                // 把移动的图片imageview从父布局里移除
                mDatabind.rlRoot.removeView(betImageView)
                val animator = ObjectAnimator.ofPropertyValuesHolder(
                    areaView.betteView.ivShowBg,
                    SCALE_X,
                    SCALE_Y
                )
                animator.duration = 200
                animator.start()

                //筹码栈处理
//                addTempMoney(areaView)
            })
        }

        valueAnimator.duration = speed
        valueAnimator.interpolator = LinearInterpolator()
        betteFlyAnimList.add(valueAnimator)
        valueAnimator.start()
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

    private fun scrollToItemAndPerformAction(
        recyclerView: RecyclerView,
        position: Int,
        action: () -> Unit
    ) {

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
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
