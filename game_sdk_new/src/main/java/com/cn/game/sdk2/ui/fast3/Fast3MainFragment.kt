package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Typeface
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.EventConst
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.databinding.FragFast3HomeBinding
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.databinding.ItemBetHistoryBinding
import com.cn.game.sdk2.ui.helper.Fast3ToastHelper
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.ui.view.CenterLayoutManager
import com.cn.game.sdk2.ui.view.CommonLinearLayoutItemDecoration
import com.cn.game.sdk2.ui.view.CustomBubbleAttachPopup
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.CommonUtils
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ext.BizExt.isLeopard
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.ext.CommonExt.isCanGoOn
import com.cn.game.sdk2.utils.ext.CommonExt.px2dp
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.utils.ext.ViewExt.getDrawable
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
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.animator.EmptyAnimator
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.robinhood.ticker.TickerUtils
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


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
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.model = mViewModel
        mDatabind.tvAnimWin.setCharacterLists(TickerUtils.provideNumberList())
        context?.assets?.let {
            mDatabind.tvAnimWin.typeface = Typeface.createFromAsset(it, "fonts/alibabapuhuiti.otf");
        }
        mDatabind.llHomeVideo.setOnClickListener {
            Fast3ToastHelper.showToastNormal(getString(R.string.g_home_betting_begin), 2000)
        }
        CommonUtils.getNavigationBarHeight(mDatabind.root).let {
            mViewModel.navigationBarHeight.value = it
            Log.d(TAG, "getNavigationBarHeight $it,-->${it.px2dp}")
        }

        //viewpager
        mFragList.add(DXDSFragment(mViewModel))
        mFragList.add(SingleDiceFragment(mViewModel))
        mFragList.add(SumTotalFragment(mViewModel))
        mFragList.add(PairsDiceFragment(mViewModel))
        mFragList.add(LeopardFragment(mViewModel))
        Fast3ToastHelper.init(requireContext(), mDatabind.centerLayout).let {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    Fast3ToastHelper.destroy()
                }
            })
        }

        mDatabind.viewPagerNew.initGameViewPager(
            childFragmentManager, mFragList, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
                requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_sum),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard)
            )
        )
        mDatabind.magicIndicator.bindViewPagerNewGame(
            mDatabind.viewPagerNew, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
                requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_sum),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard)
            ),
            scrollEnable = true,
            action = {
                PromptSoundPlay.btnPlayMedia()
            }
        )
        mDatabind.viewPagerNew.offscreenPageLimit = mFragList.size
        setBetAdapter()
        setClick()
        measureHistoryRvHeight()
    }

    override fun lazyLoadData() {
    }


    private fun doNumberAnim(targetView: TextView, startNum: Long, endNumber: Long) {
        ValueAnimator.ofFloat(startNum.toFloat(), endNumber.toFloat()).apply {
            duration = 500
            addUpdateListener {
                targetView.text = "¥ ${(it.animatedValue as Float).formatRealMoney()}"
            }
            doOnEnd { targetView.text = "¥ ${endNumber.formatRealMoney()}" }
            start()
        }
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
            //mViewModel.startBetting()
        }
    }

    private fun measureHistoryRvHeight() {
        LayoutInflater.from(context).inflate(R.layout.item_bet_history, null).apply {
            measureView()
            Log.e(TAG, "historyRvHeight->$measuredHeight")
            resultRvHeight = this.measuredHeight
            findViewById<LinearLayout>(R.id.llShowDice).apply {
                this.measureView()
                Log.e(TAG, "historyMoveHeight->$measuredHeight")
                resultAnimMoveHeight = this.measuredHeight
            }
            val params = mDatabind.flRvHistory.layoutParams
            params?.height = resultRvHeight - resultAnimMoveHeight
            mDatabind.flRvHistory.layoutParams = params
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
                Fast3ToastHelper.showToastNormal(getString(R.string.g_home_setting_begin), 1000)
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
                Fast3ToastHelper.showToastNormal(getString(R.string.g_home_drawing_begin), 1000)
            }
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
    private fun startWinLottieAnim(endCallBack: (() -> Unit)?) {
        mDatabind.apply {
            val winMoney = gameAboutModel.netIncome
            Log.e(TAG, "本轮赢钱了--->$winMoney")
            if (winMoney <= 0) {
                endCallBack?.invoke()
                return
            }
            groupWinLottie.isVisible = true
            val originTxt = "$" + winMoney.formatRealMoney()
            mDatabind.tvAnimWin.setText(originTxt.replace(Regex("[0-9]"), "0"), false)
            tvAnimWin.setText("$${winMoney.formatRealMoney()}", true)
            lottieAnimView.addAnimatorListener(object : Animator.AnimatorListener {
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
            })

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
                doNumberAnim(
                    mDatabind.txtCurrentMoney,
                    startNum = mViewModel.currentMoney,
                    endNumber = it
                )
            } else {
                mDatabind.txtCurrentMoney.text = "¥ ${it.formatRealMoney()}"
            }
            mViewModel.currentMoney = it

            mDatabind.llShowBetList.adapter?.notifyItemRangeChanged(
                0,
                mDatabind.llShowBetList.adapter?.itemCount ?: 0
            )
        }

        mViewModel.homeTimeSeconds.observe(viewLifecycleOwner) { seconds ->
            mDatabind.txtHomeTime.text = seconds.toString()
            if (mViewModel.gameState == GameState.Betting && seconds in 1..5) {
                PromptSoundPlay.countdownGameTip(requireContext())
            }
        }
        mViewModel.moneyAnimCallback = object : Fast3ViewModel.MoneyAnimCallback {
            override fun startAnim(
                x: Float,
                y: Float,
                speed: Long,
                areaView: GameAreaView,
                endCallBack: (() -> Unit)?
            ) {
                tryMoneyAnimation(x, y, speed, areaView, endCallBack)
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
                    GameAboutModel.AgainDoubleState.NUll -> {
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
                    GameAboutModel.AgainDoubleState.DOUBLE_CAN_NOT -> {
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
                Fast3ToastHelper.showToastNormal(getString(R.string.bet_success_prompt,response.money.formatRealMoney()))
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
                        repeatCount = mViewModel.prizeAnimCount // 设置无限循环
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
            val startHeight = flRvHistory.height.toFloat()
            val endHeight =
                if (isShowResult) resultRvHeight else resultRvHeight - resultAnimMoveHeight
            resultAnim = ValueAnimator.ofFloat(startHeight, endHeight.toFloat()).apply {
                duration = 150
                addUpdateListener {
                    val value = (it.animatedValue as Float).toInt()
                    val params = flRvHistory.layoutParams
                    params?.height = value
                    flRvHistory.layoutParams = params
                }
            }
            resultAnim?.start()
        }
    }


    /**
     * 投注的适配器
     */
    private fun setBetAdapter() {
        mDatabind.llShowBetList.itemAnimator = null
        mDatabind.llShowBetList.layoutManager =
            CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        if (mDatabind.llShowBetList.itemDecorationCount == 0) {
            mDatabind.llShowBetList.addItemDecoration(
                CommonLinearLayoutItemDecoration(
                    spacingV = requireContext().dp2px(10),
                    start = requireContext().dp2px(8),
                    end = requireContext().dp2px(40)
                )
            )
        }
        mDatabind.llShowBetList.setup {
            addType<SelectAnnotationBean>(R.layout.item_annotation_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_annotation_list -> {
                        val binding = getBinding<ItemAnnotationListBinding>()
                        val bean = _data as SelectAnnotationBean
                        val id = if ((gameAboutModel.balance.value ?: 0) < bean.money) {
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
//                                scrollBetteItemToCenter(layoutPosition)
                            }
                        } else {
                            binding.ivShowBg.translationY = 0f
                        }
                    }
                }

            }
            onClick(R.id.ivShowBg) {
                val bean = _data as SelectAnnotationBean
                PromptSoundPlay.btnPlayMedia(requireContext())
                if (bean.select) return@onClick
                val models: List<SelectAnnotationBean> = models as List<SelectAnnotationBean>
                for (data in models) {
                    data.select = bean == data
                }
                (mDatabind.llShowBetList.layoutManager as CenterLayoutManager).smoothScrollToPosition(
                    mDatabind.llShowBetList,
                    RecyclerView.State(),
                    layoutPosition
                )
                notifyItemRangeChanged(0, modelCount)

            }
        }.models = mViewModel.noteList
        //历史结果
        mDatabind.rvHomeHistory.itemAnimator = null
        mDatabind.rvHomeHistory.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )
        mDatabind.rvHomeHistory.dividerSpace(
            requireContext().dp2px(2),
            DividerOrientation.HORIZONTAL
        ).setup {
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
            rlClickHide.setOnClickListener {
                PromptSoundPlay.btnPlayMedia(requireContext())
                resultAnimation(!mViewModel.isShowResult)
                /*val v = (gameAboutModel.balance as MutableLiveData).value
                if(v == null){
                    (gameAboutModel.balance as MutableLiveData).value = 100L + Random.nextLong(100,10000)
                } else {
                    (gameAboutModel.balance as MutableLiveData).value = v +  Random.nextLong(100_00,1000_00)
                }*/
            }

            //点击更多弹出框
            llHomeMore.setOnClickListener {
                PromptSoundPlay.btnPlayMedia(requireContext())
                if (homeMorePop == null) {
                    val bubbleAttach = CustomBubbleAttachPopup(requireContext())
                    bubbleAttach.customBubbleAttachListener =
                        object : CustomBubbleAttachPopup.CustomBubbleAttachListener {
                            override fun switchGame() {
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
                        //.customAnimator(EmptyAnimator(bubbleAttach, 0))
                        .atView(mDatabind.llHomeMore)
                        //.navigationBarColor(android.R.color.transparent)
                        .hasShadowBg(false) // 去掉半透明背景
                        .asCustom(bubbleAttach)
                    homeMorePop?.show()
                } else {
                    homeMorePop?.dismiss()
                }
            }
            //加倍
            ivMultiple2.setOnClickListener {
                if (gameAboutModel.currentAgainDoubleState.value != GameAboutModel.AgainDoubleState.DOUBLE) {
                    return@setOnClickListener
                }
                PromptSoundPlay.btnPlayMedia()
                GameSocketManager.getInstance()?.getGameService()
                    ?.doubleBetting { bettingState, map,areaLimit ->
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
            //续压
            ivXuya.setOnClickListener {
                if (gameAboutModel.currentAgainDoubleState.value != GameAboutModel.AgainDoubleState.AGAIN) {
                    return@setOnClickListener
                }
                PromptSoundPlay.btnPlayMedia()
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
        endCallBack: (() -> Unit)? = null
    ) {
        //PromptSoundPlay.goldPlayMedia(this)
        //PromptSoundPlay.goldPlayMediaNew(this)
        PromptSoundPlay.playAudio(requireContext())
        //获取选中的筹码所在的position
        val betList = mDatabind.llShowBetList.models as List<SelectAnnotationBean>
        val selectedPosition = betList.indexOfFirst { it.select }
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
        val mCurrentPosition = FloatArray(2)

        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线))
        val betImageView = ImageView(requireContext())
        betImageView.setImageDrawable(jettonView.findViewById<ImageView>(R.id.ivShowBg).drawable)
        val params = RelativeLayout.LayoutParams(
            requireContext().dp2px(32),
            requireContext().dp2px(32)
        )
        mDatabind.rlRoot.addView(betImageView, params)

        //正式开始计算动画开始/结束的坐标
        val location = IntArray(2)
        jettonView.getLocationInWindow(location)
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
                betImageView.translationY = mCurrentPosition[1]
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
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
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
