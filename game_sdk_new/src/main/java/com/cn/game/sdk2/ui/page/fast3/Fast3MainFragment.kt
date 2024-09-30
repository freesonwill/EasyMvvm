package com.cn.game.sdk2.ui.page.fast3

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.BetteFlyData
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.data.enums.ChipBean
import com.cn.game.sdk2.data.enums.GameEnum
import com.cn.game.sdk2.databinding.FragFast3HomeBinding
import com.cn.game.sdk2.ui.adapter.PagerAdapter
import com.cn.game.sdk2.ui.fragment.ChipsFragment
import com.cn.game.sdk2.ui.fragment.ChipsViewImp
import com.cn.game.sdk2.ui.fragment.DrawHistoryFragment
import com.cn.game.sdk2.ui.fragment.DrawResultFragment
import com.cn.game.sdk2.ui.fragment.WinningAnimationFragment
import com.cn.game.sdk2.ui.helper.AnimHelper
import com.cn.game.sdk2.ui.helper.ToastHelper
import com.cn.game.sdk2.ui.popup.game.MoreListPopup
import com.cn.game.sdk2.ui.view.TabLayoutMediator
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.view.game.MoneyOKView
import com.cn.game.sdk2.ui.viewmodel.MainViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.GameViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.ext.CommonExt.isCanGoOn
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.getCenterPoint
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.ext.removeAllTips
import com.cn.game.sdk2.utils.ext.setOverScrollModeExt
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.constants.AgainDoubleState
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.gameMassageManager
import com.eetrust.lib_bounce_effect.setBounceEdgeEffect
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ktx.hasNavigationBar
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class Fast3MainFragment : BaseFragment<MainViewModel, FragFast3HomeBinding>() {
    override val mBinding: FragFast3HomeBinding by viewBind()
    override val mViewModel: MainViewModel  by viewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()

    companion object {
        const val TAG = "Fast3MainFragment"
    }

    private var anchorMoneyView: MoneyOKView? = null
    private var switchTabAnimJob: Job? = null

    //<areaCode,<money,View>>
    private val currentBetteAreaMap by lazy { LinkedHashMap<Int, GameAreaView>() }
    private val allGameAreaMap by lazy { mutableMapOf<Int, GameAreaView>() }
    private val betteFlyAnimList by lazy { mutableMapOf<GameAreaView, MutableList<BetteFlyData>>() }

    //==================================== Method ===============================================//
    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.bottomLayout.setOnTouchListener { _, _ -> true }
        mBinding.resultClickView.setOnClickListener { } //屏蔽底部recycler点击

        setChipsView()
        setDrawResultView()
        setHistoryView()
        setWinningAnimationView()
        setClick()
        setNavigationBar()
    }

    private fun loadFragment(game: GameEnum) {

        with(mBinding) {
            val startTime = System.currentTimeMillis()
            val gameList = game.gamePage.map { it.gamePageBean }
            viewPagerNew.adapter = null
            viewPagerNew.adapter = PagerAdapter(childFragmentManager, lifecycle, gameList)

            viewPagerNew.setOverScrollModeExt(
                BottomPopupView.OVER_SCROLL_IF_CONTENT_SCROLLS,
                OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
//            viewPagerNew.setBounceEdgeEffect(overScrollMagnitude=1f, flingMagnitude = 10f)
            TabLayoutMediator(tlGame, viewPagerNew) { tab, position ->
                val tabView = tab.view
                tab.text = gameList[position].title
                tabView.setPadding(
                    32,
                    0,
                    32,
                    0
                )
                tabView.setOnClickListener {
                    PromptSoundPlay.btnPlayMedia()
                }
            }.attach()

            tlGame.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    switchTabAnimJob?.cancel()
                    switchTabAnimJob = AnimHelper.doDirectViewPagerAnim(
                        targetPosition = tab?.position ?: 0,
                        viewPager = viewPagerNew,
                        fakeViewPager = fragmentFakeViewPager
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
            tlGame.removeAllTips()
            (System.currentTimeMillis() - startTime).let {
                LogUtils.dTag(TAG, "Fast3MainFragment load costMills2:$it")
            }
            (System.currentTimeMillis() - startTime).let {
                LogUtils.dTag(TAG, "Fast3MainFragment load costMills3:$it")
            }
        }
    }

    override fun lazyLoadData() {
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        //获取当前余额
        mBinding.txtCurrentMoney.text = "¥ ${mViewModel.currentMoney.formatRealMoney()}"
        mBinding.txtHomeTime.text = mViewModel.homeTimeSeconds.value.toString()
        lifecycleScope.launchWhenResumed {
            //开始下注
            LogUtils.dTag(TAG, "initData startBetting")
            delay(200)
            updateGameStage()
            updateAgainDoubleUi()
        }
    }

    private fun setNavigationBar() {
        mBinding.apply {
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
            //                         bottomLayout.layoutParams = layoutParams
        }
    }

    /**Ï
     * 刷新游戏状态
     */
    private fun updateGameStage() {
        gameAboutModel.currentStage.value?.let {
            LogUtils.dTag(
                TAG,
                "updateGameStage-->${it},countDown:${mViewModel.countDown},localGameStage:${mViewModel.localGameStage}"
            )
            if (mViewModel.localGameStage == it) return@let
            mBinding.viewPagerNew.isEnabled = it == GameStage.NEW
            mBinding.txtHomeTime.isVisible = it == GameStage.NEW
            mBinding.txtHomeUnit.isVisible = it == GameStage.NEW
            mViewModel.localGameStage = it
            when (it) {
                GameStage.NEW -> {
                    onStartBetting()
                }

                GameStage.DEAL -> {
                    onStartDrawing()
                }

                GameStage.SETTLE -> {
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
                ToastHelper.instance.showHostToast(mBinding.viewPagerNew, getString(R.string.g_home_betting_begin))
            }
            mBinding.apply {
                launch {
                    playAlphaAnimTogether(
                        arrayOf(txtHomeStatic, txtHomeTime, txtHomeUnit),
                        floatArrayOf(0f, 1f)
                    )
                    txtHomeStatic.text = resources.getString(R.string.g_home_txt_please)
                }
            }
            LogUtils.dTag(TAG, "onStartBetting, isCountDownStart:${mViewModel.isCountDownStart}")
            //重置注区筹码
            notifyMoneyOkView(null)
            //下注筹码向上升起动画
            startBetteRecyclerShowOrHideAnim(isShow = true, onStart = {
                //筹码
                mBinding.clChips.isVisible = true
                mBinding.betteAgainLayout.isVisible = true

                //开奖结果x
                mBinding.fragmentDrawResult.isVisible = false
                mBinding.ivHomeBg.isVisible = false
                mBinding.ivHomeBgCenter.isVisible = false
                mBinding.resultBgTop.isVisible = false
            }, duration = if (mViewModel.isCountDownStart) 250 else 0)

            //暂时解决筹码栏被隐藏问题
            delay(500)
            if (mViewModel.gameState == GameStage.NEW && !mBinding.clChips.isVisible) {
                resetBetteRecyclerVisible()
            }
        }
    }

    private fun onStartSetting() { //开始结算
        lifecycleScope.launch {
            mBinding.apply {
                //Fast3ToastHelper.showToastNormal(getString(R.string.g_home_setting_begin), 1000)
                run {
                    playAlphaAnimTogether(arrayOf(txtHomeStatic), floatArrayOf(0f, 1f))
                    txtHomeStatic.text = resources.getString(R.string.g_f3_setting)
                }
                mBinding.clChips.isInvisible = true
                mBinding.betteAgainLayout.isVisible = false

                run {
                    //开奖结果注区动画闪烁
                    FlowBus.with<Boolean>(EventKey.PLAY_DRAW_HISTORY_ANIM).post(lifecycleScope,true)
                    //中奖区域金额刷新(移除未中奖的筹码)
                    LogUtils.dTag(TAG, "中奖注区筹码监听--->${gameAboutModel.userLotteryResult}")
                    notifyMoneyOkView(gameAboutModel.userLotteryResult)
                }
                with(childFragmentManager.findFragmentByTag(WinningAnimationFragment.TAG) as WinningAnimationFragment) {
                    //中奖动画
                    startWinLottieAnim(gameAboutModel.netIncome, endCallBack = {})
                }
            }
        }
    }

    /**
     * 并行播放透明度动画
     * @param views 目标view(多个)
     * @param alphas 透明度数组
     * @param d 持续时间
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
            cancelBetteFlyAnim()
            cancelTemBetting()
            //开奖时取消临时下注的
            mBinding.apply {
                //标题栏
                run {
                    playAlphaAnimTogether(arrayOf(txtHomeStatic), floatArrayOf(0f, 1f))
                    txtHomeStatic.text = getString(R.string.g_f3_dealing)
                }

                //隐藏筹码牌动画
                startBetteRecyclerShowOrHideAnim(isShow = false, onEnd = {
                    //注区
                    clChips.isInvisible = true
                    betteAgainLayout.isVisible = false

                    //开奖结果
                    ivHomeBg.isVisible = true
                    ivHomeBgCenter.isVisible = true
                    resultBgTop.isVisible = true
                })
            }
        }
    }

    override fun createObserver() {
        //监听历史变化来播放开奖动画
        gameAboutModel.historyRounds.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                //播放开奖动画(骰子扇形+数字伸缩)
                with((childFragmentManager.findFragmentByTag(DrawResultFragment.TAG) as DrawResultFragment)) {
                    delay(200)
                    //避免当前不是开奖状态还播放开奖状态
                    if(gameAboutModel.currentStage.value != GameStage.DEAL) {
                        LogUtils.eTag(TAG,"currentStage:${gameAboutModel.currentStage.value} != Deal,ignore playing result anim")
                        return@with
                    }
                    LogUtils.dTag(TAG,"currentSettleResult:${gameAboutModel.currentSettleResult}")
                    mBinding.fragmentDrawResult.isVisible = true
                    gameAboutModel.currentSettleResult?.let { setDrawResult(it) }
                    playResultAnim(mBinding.viewPagerNew.getCenterPoint(), doEnd = {})
                }
            }
        }

        FlowBus.with<List<GameAreaView>>(EventKey.UPDATE_ALL_AREA_VIEW)
            .register(viewLifecycleOwner) { list ->
                list.forEach {
                    allGameAreaMap[it.areaCode] = it
                    "add code=${it.areaCode},${it.id}".loge("UPDATE_ALL_AREA_VIEW")
                }
            }

        mViewModel.game.observe(viewLifecycleOwner) {
            loadFragment(it)
        }
        //总余额监听
        gameAboutModel.balance.observe(viewLifecycleOwner) {
            LogUtils.eTag(TAG, "收到的总余额：${it},old:${mViewModel.currentMoney}, new:$it")
            if (it > mViewModel.currentMoney) {
                val start = mViewModel.currentMoney
                val end = it
                mBinding.txtCurrentMoney.postDelayed({
                    AnimHelper.doNumberAnim(
                        mBinding.txtCurrentMoney,
                        startNum = start,
                        endNumber = end,
                        duration1 = 600
                    )
                }, 600)
            } else {
                mBinding.txtCurrentMoney.text = "¥ ${it.formatRealMoney()}"
            }
            mViewModel.currentMoney = it
        }

//        //临时金额变化，用于刷新筹码可用
        gameAboutModel.tempBalance.observe(viewLifecycleOwner) {
            LogUtils.eTag(TAG, "收到当前可用金额：${it}")
//            notifyBetteBean(it)
        }

        gameAboutModel.countDownSecondsLD.observe(viewLifecycleOwner) { seconds ->
            //Log.d(TAG,"countdown: seconds:$seconds")
            if (mViewModel.gameState == GameStage.NEW && seconds in 1..5) {
                if (gameAboutModel.fast3MainFloatVisible.value == false)
                    PromptSoundPlay.countdownGameTip()
            }
            if (seconds == 0) {
                if (mViewModel.gameState == GameStage.NEW) {
                    ToastHelper.instance.showHostToast(mBinding.viewPagerNew, getString(R.string.g_home_betting_end))
                    //防止断网状态
                    if (mViewModel.gameState != GameStage.DEAL) {
                        gameAboutModel.changeStage(GameStage.DEAL)
                    }
                }
            } else {
                mBinding.txtHomeTime.text = seconds.toString()
            }
        }
        gameViewModel.moneyAnimCallback = object : GameViewModel.MoneyAnimCallback {
            override fun startAnim(
                x: Float,
                y: Float,
                speed: Long,
                areaView: GameAreaView,
                betteBean: ChipBean,
                endCallBack: (() -> Unit)?
            ) {
                tryMoneyAnimation(x, y, speed, areaView, betteBean, endCallBack)
            }
        }
        FlowBus.with<Pair<GameAreaView, ViewGroup>>(EventKey.AddMoneyOkView).register(viewLifecycleOwner)  {
            LogUtils.dTag(TAG,"received AddMoneyOkView:${it.first.areaCode}")
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
        FlowBus.with<GameAreaView>(EventKey.UpdateMoneyView).register(viewLifecycleOwner){
            LogUtils.dTag(TAG,"received UpdateMoneyView :${it.areaCode}")
            currentBetteAreaMap[it.areaCode] = it

        }
        FlowBus.with<Int>(EventKey.RemoveMoneyView).register(viewLifecycleOwner){
            LogUtils.dTag(TAG,"received RemoveMoneyView :$it")
            currentBetteAreaMap.remove(it)
            allGameAreaMap.remove(it)
        }

        gameViewModel.betOkClick.observe(this) {
            gameMassageManager
                ?.commitBetting { bettingState, areaLimit ->
                    bettingState.isCanGoOn(mBinding.viewPagerNew, areaLimit) {
                        hiddenAnchorTop()
                    }
                }
        }

        gameViewModel.betDeleteClick.observe(this) {
            cancelBetteFlyAnim()
            cancelTemBetting()
        }

        //游戏状态监听
        gameAboutModel.currentStage.observe(viewLifecycleOwner) { _ ->
            updateGameStage()
        }

        //续压、加倍状态监听
        gameAboutModel.currentAgainDoubleState.observe(viewLifecycleOwner) {
            LogUtils.eTag(TAG,"续压加倍状态监听--->${it}")
            updateAgainDoubleUi()
        }

        //下注结果
        gameAboutModel.isBettingSuccess.observe(viewLifecycleOwner) { response ->
            LogUtils.eTag(TAG, "下注结果监听--->${response}")
            if (!response.isSuccess) {
                //失败时显示delete ok按钮
                ToastHelper.instance.showHostToast(mBinding.viewPagerNew, "网络连接失败")
                showAnchorTop()
            } else {
                ToastHelper.instance.showHostToast(mBinding.viewPagerNew, getString(
                    R.string.bet_success_prompt,
                    response.money.formatRealMoney()
                ))
            }
        }

        //error
        gameAboutModel.toastErrorMessage.observe(viewLifecycleOwner) { msg ->
            ToastHelper.instance.showHostToast(mBinding.viewPagerNew, msg)
        }
    }

    private fun updateAgainDoubleUi() {
        mBinding.apply {
            when (gameAboutModel.currentAgainDoubleState.value) {
                null, AgainDoubleState.NUll, AgainDoubleState.AGAIN_CAN_NOT_50 -> {
                    ivXuya.isVisible = true
                    ivXuya.setImageResource(R.drawable.game_sdk_icon_xuya_gray)
                    ivMultiple2.isVisible = false
                }

                AgainDoubleState.AGAIN -> {
                    ivXuya.isVisible = true
                    ivXuya.setImageResource(R.drawable.game_sdk_icon_xuya)
                    ivMultiple2.isVisible = false
                }

                AgainDoubleState.DOUBLE -> {
                    ivXuya.isVisible = false
                    ivMultiple2.isVisible = true
                    ivMultiple2.setImageResource(R.drawable.game_sdk_icon_multiple2)
                }

                AgainDoubleState.DOUBLE_CAN_NOT,
                AgainDoubleState.DOUBLE_CAN_NOT_50 -> {
                    ivXuya.isVisible = false
                    ivMultiple2.isVisible = true
                    ivMultiple2.setImageResource(R.drawable.game_sdk_icon_multiple2_gray)
                }
            }
        }
    }

    /**
     * 取消临时下注
     */
    private fun cancelTemBetting() {
        hiddenAnchorTop()
        childFragmentManager.findFragmentByTag(ChipsFragment.TAG)?.let {
            (it as ChipsViewImp).cancelBet()
        }
        gameMassageManager?.cancelBetting { result ->
            notifyMoneyOkView(result)
        }
    }

    private fun cancelBetteFlyAnim() {
        betteFlyAnimList.forEach {
            it.value.forEach { bfd ->
                bfd.animator.cancel()
                bfd.isRunning = false
            }
        }
        betteFlyAnimList.clear()
    }

    /**
     * 刷新页面上注区里moneyView显示(移除未中奖的筹码)
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

    private fun setChipsView() {
        val f = ChipsFragment()
        childFragmentManager.beginTransaction().replace(mBinding.flChips.id, f, ChipsFragment.TAG).commit()
    }

    // 設置底部當局開獎結果Fragment
    private fun setDrawResultView() {
        childFragmentManager.findFragmentByTag(DrawResultFragment.TAG)
                as? DrawResultFragment ?: DrawResultFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentDrawResult.id, it, DrawResultFragment.TAG)
                .commitNow()
        }
    }

    // 設置底部開獎紀錄Fragment
    private fun setHistoryView() {
        val frag = childFragmentManager.findFragmentByTag(DrawHistoryFragment.TAG)
                as? DrawHistoryFragment ?: DrawHistoryFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentHistory.id, it, DrawHistoryFragment.TAG)
                .commitNow()
        }

        frag.setDrawHistoryHeightListener { height ->
            setHistoryHeight(height)
        }

        setHistoryHeight(frag.getCurrentHeight())
    }

    // 設置底部開獎紀錄展開高度
    private fun setHistoryHeight(height: Int) {
        mBinding.ivHistoryBg.layoutParams =
            (mBinding.ivHistoryBg.layoutParams as ConstraintLayout.LayoutParams)
                .apply {
                    this.height = height
                }
    }

    // 設置中獎動畫
    private fun setWinningAnimationView() {
        childFragmentManager.findFragmentByTag(WinningAnimationFragment.TAG)
                as? WinningAnimationFragment ?: WinningAnimationFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentWinning.id, it, WinningAnimationFragment.TAG)
                .commitNow()
        }
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
        mBinding.apply {
            val recyclerAnim = ObjectAnimator.ofFloat(
                flChips,
                "translationY",
                if (isShow) 0f else flChips.measuredHeight.toFloat()
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
        mBinding.apply {
            clChips.isVisible = true
            mBinding.betteAgainLayout.isVisible = true

            //开奖结果x
            fragmentDrawResult.isVisible = false
            ivHomeBg.isVisible = false
            ivHomeBgCenter.isVisible = false
            resultBgTop.isVisible = false

            flChips.translationY = 0f
            betteAgainLayout.translationX = 0f
        }
    }

    private fun setClick() {
        mBinding.apply {
            //余额点击跳转充值
            frrl.clickNoRepeat(interval = 100){
                appListener?.onInsufficientBalance()
            }
            //点击更多弹出框
            llHomeMore.setOnClickListener { view ->
                PromptSoundPlay.btnPlayMedia()
                MoreListPopup.create(requireContext(), object :
                    MoreListPopup.OnMoreListPopupListener {
                    override fun bindView(): View {
                        return mBinding.llHomeMore
                    }

                    override fun getSecondPopHeight(): Int {
                        return mBinding.root.height
                    }

                    override fun getFragmentManager(): FragmentManager {
                        return childFragmentManager
                    }
                }, view.getCenterPoint())
            }
            //加倍
            ivMultiple2.setOnClickListener {
                if (gameAboutModel.currentAgainDoubleState.value == AgainDoubleState.DOUBLE_CAN_NOT_50) {
                    ToastHelper.instance.showHostToast(mBinding.viewPagerNew, getString(R.string.money_insufficient_50))
                    return@setOnClickListener
                }
                if (gameAboutModel.currentAgainDoubleState.value == AgainDoubleState.DOUBLE_CAN_NOT) {
                    return@setOnClickListener
                }
                if (gameAboutModel.currentAgainDoubleState.value == AgainDoubleState.DOUBLE) {
                    if (gameAboutModel.currentAgainDoubleState.value == AgainDoubleState.DOUBLE) {
                        PromptSoundPlay.playGoldCoinAudio()
                        AnimHelper.doScaleAnimRecovery(ivMultiple2)
                    }
                    gameMassageManager
                        ?.doubleBetting { bettingState, map, areaLimit ->
                            bettingState.isCanGoOn(mBinding.viewPagerNew, areaLimit) {
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
                if (gameAboutModel.currentAgainDoubleState.value == AgainDoubleState.AGAIN_CAN_NOT_50) {
                    ToastHelper.instance.showHostToast(mBinding.viewPagerNew, getString(R.string.money_insufficient_50))
                    return@setOnClickListener
                }
                if (gameAboutModel.currentAgainDoubleState.value != AgainDoubleState.AGAIN) {
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
                            areaView.moneyView.hiddenTop()
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
        LogUtils.dTag(TAG, "onDetach~~~~~~~~~~~~~~")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.dTag(TAG, "onDestroyView~~~~~~~~~~~~~~")
    }

    /**
     * 关闭页面
     */
    override fun onDestroy() {
        LogUtils.dTag(TAG, "onDestroy~~~~~~~~~~~~~~")
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
        betteBean: ChipBean,
        endCallBack: (() -> Unit)? = null
    ) {
        PromptSoundPlay.playGoldCoinAudio()
        if (anchorMoneyView != null && areaView.moneyView != anchorMoneyView) {
            hiddenAnchorTop()
        }
        val isFirstAdd = !currentBetteAreaMap.containsKey(areaView.areaCode)
        updateAnchorView(areaView)
        childFragmentManager.findFragmentByTag(ChipsFragment.TAG)?.let {
            (it as ChipsViewImp).onBetAreaClick { view ->
                startMoneyAnimation(
                    x,
                    y,
                    speed,
                    areaView,
                    view,
                    betteBean,
                    isFirstAdd,
                    endCallBack
                )
            }
        }

    }

    private var betteViewGroup: ViewGroup? = null

    private fun startMoneyAnimation(
        x: Float,
        y: Float,
        speed: Long,
        areaView: GameAreaView,
        jettonView: View,
        betteBean: ChipBean,
        isFirstAdd: Boolean,
        endCallBack: (() -> Unit)?
    ) {
        //贝塞尔曲线中间过程的点的坐标
        val viewPagerLocation = mBinding.viewPagerNew.locationOnScreen
        val jettonViewLocation = jettonView.locationOnScreen
        val targetLocation = areaView.betteView.ivShowBg.locationOnScreen
        val betteSize = jettonView.measuredWidth
        val targetSize = 32.dp2px
        val scale = targetSize.toFloat() / betteSize.toFloat()
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线))
        val betImageView = ImageView(requireContext()).apply {
            val id = betteBean.chip.selectedRes
            setImageResource(id)
            scaleX = scale
            scaleY = scale
        }

        val params = FrameLayout.LayoutParams(betteSize, betteSize)
        params.topMargin = jettonViewLocation[1] - viewPagerLocation[1]
        params.leftMargin = jettonViewLocation[0] - viewPagerLocation[0]
        betImageView.layoutParams = params
        if (betteViewGroup == null) {
            betteViewGroup = areaView.parent.parent as ViewGroup
        }
        betteViewGroup?.addView(betImageView)

        val xOffset = 12.dp2px
        val yOffset = 5.dp2px
        val animator: ViewPropertyAnimator = betImageView.animate()
            .setInterpolator(LinearInterpolator())
            .translationX((targetLocation[0] - jettonViewLocation[0]).toFloat() - xOffset)
            .translationY((targetLocation[1] - jettonViewLocation[1]).toFloat() - yOffset)
            .setDuration(200)

        animator.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
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
    }

    private fun updateAnchorView(areaView: GameAreaView) {
        hiddenAnchorTop()
        anchorMoneyView = areaView.moneyView
        currentBetteAreaMap[areaView.areaCode] = areaView
        mBinding.tempTouch.setAnchorMoneyView(areaView.moneyView)
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
            if (mBinding.viewPagerNew.currentItem != index) {
                mBinding.viewPagerNew.currentItem = index
            }
        }
    }
}
