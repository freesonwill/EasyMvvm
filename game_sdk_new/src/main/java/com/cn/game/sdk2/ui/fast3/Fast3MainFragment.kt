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
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.CustomBubbleAttachPopup
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ToastUtil
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
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
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("SetTextI18n")
class Fast3MainFragment : BaseVmDbFragment<Fast3ViewModel, FragFast3HomeBinding>() {
    companion object{
        const val TAG = "Fast3MainFragment"
    }
    private var mFragList = ArrayList<Fragment>()

    //是否执行关闭动画
    var isExecuteClose: Boolean = true

    // 定义属性动画常量
    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.4f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.4f, 1.0f)

    private var homeMorePop: BasePopupView? = null
    private var resultAnim: ValueAnimator? = null
    private var resultRvHeight = -1
    private var resultAnimMoveHeight = -1
    private var anchorMoneyView: MoneyOKView? = null

    //<areaCode,<money,View>>
    private val moneyOkViewMap by lazy { mutableMapOf<Int, MoneyOKView>() }
    private val allGameAreaMap by lazy { mutableMapOf<Int, GameAreaView>() }

    //==================================== Method ===============================================//
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.model = mViewModel
        //viewpager
        mFragList.add(DXDSFragment(mViewModel))
        mFragList.add(SingleDiceFragment(mViewModel))
        mFragList.add(SumTotalFragment(mViewModel))
        mFragList.add(PairsDiceFragment(mViewModel))
        mFragList.add(LeopardFragment(mViewModel))

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
            ), scrollEnable = true
        )
        mDatabind.viewPagerNew.offscreenPageLimit = mFragList.size

        setBetAdapter()
        setClick()
    }

    override fun lazyLoadData() {
    }

    override fun initData() {
        //获取当前余额
        mDatabind.txtCurrentMoney.text = "¥ ${mViewModel.currentMoney}"
        mDatabind.txtHomeTime.text = mViewModel.homeTime.value.toString()
        lifecycleScope.launchWhenResumed {
            //开始下注
            Log.d(TAG, "initData startBetting")
            delay(200)
            updateGameStage()
            //mViewModel.startBetting()
        }
    }

    /**
     * 刷新游戏状态
     */
    private fun updateGameStage(){
        gameAboutModel.currentStage.value?.let {
            when(it){
                GameAboutModel.Stage.NEW->{
                    onStartBetting()
                }
                GameAboutModel.Stage.DEAL->{
                    onStartDrawing()
                }
                GameAboutModel.Stage.SETTLE->{
                    onStartSetting()
                }
            }
            //if(!mViewModel.isCountDownInit) mViewModel.countDown = gameAboutModel.countDown * 1L
            Log.d(TAG,"updateGameStage-->${it},countDown:${mViewModel.countDown}")
            mViewModel.startCountDown(mViewModel.countDown)
        }
    }

    private fun onStartBetting() {
        lifecycleScope.launch {
            //开始语音
            mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_txt_please)
            if(mViewModel.isCountDownStart) {
                ToastUtil.showToastNormal(getString(R.string.g_home_betting_begin), 2000)
                PromptSoundPlay.startGameTip(requireContext())
                //下注闪动动画
                suspendCoroutine { continuation ->
                    val childAlphaAnimator = ObjectAnimator.ofFloat(mDatabind.llShowBetList, "alpha", 0f, 1f)
                    childAlphaAnimator.duration = 0 // 设置渐隐动画持续时间
                    val animatorSet = AnimatorSet()
                    animatorSet.play(childAlphaAnimator)
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)

                            continuation.resume(Unit)
                        }
                    })
                    animatorSet.start()
                }
            }
            //注区
            mDatabind.llShowBetList.visibility = View.VISIBLE
            //显示开奖结果
            mDatabind.rlShowResult.visibility = View.GONE
            mDatabind.ivHomeBg.visibility = View.GONE
            mDatabind.ivHomeBgCenter.visibility = View.GONE
            //hiddenView(true)
            //重置注区筹码
            notifyMoneyOkView(null)
            //倒计时
            //mViewModel.startCountDown(mViewModel.countDown)
        }
    }

    private fun onStartSetting() { //开始结算
        val roundInfo: RoundInfoBean? = gameAboutModel.currentSettleResult
        lifecycleScope.launch {
            mDatabind.apply {
                ToastUtil.showToastNormal(getString(R.string.g_home_setting_begin), 1000)
                mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_balance)
                //隐藏筹码牌
                suspendCoroutine { continuation ->
                    val childAlphaAnimator = ObjectAnimator.ofFloat(mDatabind.llShowBetList, "alpha", 1f, 0f)
                    childAlphaAnimator.duration = 0 // 设置渐隐动画持续时间
                    val animatorSet = AnimatorSet()
                    animatorSet.play(childAlphaAnimator)
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            //注区
                            llShowBetList.visibility = View.INVISIBLE
                            //显示开奖结果
                            rlShowResult.isVisible = true
                            ivHomeBg.isVisible = true
                            ivHomeBgCenter.isVisible = true
                            //hiddenView()
                            continuation.resume(Unit)
                        }
                    })
                    animatorSet.start()
                }

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
                //中奖动画
                startWinLottieAnim(endCallBack = {
                    //开奖结果注区动画闪烁
                    Log.e(TAG,"中奖注区结果监听--->${gameAboutModel.lotteryResultList}")
                    mViewModel.userLotteryResultLiveData.value = gameAboutModel.lotteryResultList

                    //中奖区域金额刷新
                    Log.e(TAG,"中奖注区筹码监听--->${gameAboutModel.userLotteryResult}")
                    notifyMoneyOkView(gameAboutModel.userLotteryResult)
                })
            }
        }
    }

    /**
     * 开奖中
     */
    private fun onStartDrawing() {
        lifecycleScope.launch {//关闭
            if(mViewModel.isCountDownStart) {
                PromptSoundPlay.endGameTip(requireContext())
                ToastUtil.showToastNormal(getString(R.string.g_home_drawing_begin), 1000)
            }
            cancelTemBetting()
            //开奖时取消临时下注的
            mDatabind.apply {
                txtHomeStatic.text = getString(R.string.g_home_drawing_being)
            }
        }
    }

    /**
     * 播放中奖lottie动画
     */
    @SuppressLint("SetTextI18n")
    private fun startWinLottieAnim(endCallBack: (() -> Unit)?) {
        mDatabind.apply {
            val winMoney = gameAboutModel.netIncome
            if (winMoney <= 0) {
                endCallBack?.invoke()
                return
            }
            groupWinLottie.isVisible = true
            txtWinMoney.text = "$${winMoney.formatRealMoney()}"
            lottieAnimView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
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

    private fun onDrawFinish() {
        lifecycleScope.launch {
            delay(mViewModel.prizeAnimTime + 2000)
            //播放开奖动画
            //mViewModel.startBetting()
        }
    }

    override fun createObserver() {
        Log.i(TAG, "createObserver------------>")
        FlowBus.with<List<GameAreaView>>(EventConst.UPDATE_ALL_AREA_VIEW)
            .register(viewLifecycleOwner) { list ->
                list.forEach {
                    allGameAreaMap[it.areaCode] = it
                }
            }
        GameSocketManager.getInstance()?.getGameService()?.observeAgainDoubleState(this)

        mViewModel.currentMoneyLD.observe(viewLifecycleOwner) { balance ->
            Log.e(TAG,"收到的总余额：${balance}")
            mDatabind.txtCurrentMoney.text = "¥ $balance"
        }
        mViewModel.homeTimeVisibility.observe(viewLifecycleOwner) {
            mDatabind.txtHomeTime.visibility = it
            mDatabind.txtHomeUnit.visibility = it
        }
        mViewModel.homeTime.observe(viewLifecycleOwner) { seconds ->
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
            GameSocketManager.getInstance()?.getGameService()?.commitBetting()
        }

        mViewModel.betDeleteClick.observe(this) {
            cancelTemBetting()
        }

        //游戏状态监听
        gameAboutModel.currentStage.observe(viewLifecycleOwner) { stage ->
            //mViewModel.countDown = gameAboutModel.countDown * 1L
            stage?.run {
                mViewModel.startCountDown(mViewModel.countDown)
                mViewModel.isClickOperation = stage == GameAboutModel.Stage.NEW
                when (this) {
                    GameAboutModel.Stage.NEW -> {//下注
                        onStartBetting()
                    }

                    GameAboutModel.Stage.DEAL -> {//开奖
                        onStartDrawing()
                    }

                    GameAboutModel.Stage.SETTLE -> {//结算
                        onStartSetting()
                    }
                }
            }
        }

        //续压、加倍状态监听
        gameAboutModel.currentAgainDoubleState.observe(viewLifecycleOwner){
            Log.e(TAG,"续压加倍状态监听--->${it}")
            mDatabind.ivXuya.isVisible = it == GameAboutModel.AgainDoubleState.AGAIN
            mDatabind.ivMultiple2.isVisible = it == GameAboutModel.AgainDoubleState.DOUBLE
        }

        //开奖历史记录
        gameAboutModel.historyRounds.observe(viewLifecycleOwner) {
            Log.e(TAG, "开奖历史结果--->$it")
            lifecycleScope.launch {
                val adapter = mDatabind.rvHomeHistory.bindingAdapter
                adapter.models = it
                mDatabind.rlClickHide.isVisible = adapter.models!!.isNotEmpty()
                if (it.isEmpty()) {
                    //重置result动画高度
                    resultAnimMoveHeight = -1
                    mDatabind.ivHomeRotation.rotation = 180f
                    val params = mDatabind.flRvHistory.layoutParams
                    params?.height = 0
                    mDatabind.flRvHistory.layoutParams = params
                }
            }
        }

        //下注结果
        gameAboutModel.isBettingSuccess.observe(viewLifecycleOwner) { isSuccess ->
            Log.e(TAG,"下注结果监听--->${isSuccess}")
            if (!isSuccess) {
                //失败时显示delete ok按钮
                ToastUtil.showToastNormal("网络连接失败")
                anchorMoneyView?.showTop()
            }
        }
        mViewModel.playAlphaAnimationLD.observe(viewLifecycleOwner,object : Observer<Boolean> {
            var animator:ObjectAnimator? = null
            override fun onChanged(play: Boolean) {
                mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size-1)
                val layoutManager = mDatabind.rvHomeHistory.layoutManager as LinearLayoutManager
                val position = layoutManager.findLastVisibleItemPosition()
                val view = layoutManager.findViewByPosition(position)
                Log.d(TAG,"receive playAlphaAnimationLD:$play,view:$view")
                if(view == null) return
                if(play) {
                    animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f).apply {
                        duration = mViewModel.prizeAnimTime // 设置动画持续时间
                        repeatCount = mViewModel.prizeAnimCount // 设置无限循环
                        repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    }
                    Log.d(TAG,"receive playAlphaAnimationLD:${animator}")
                    animator?.start()
                } else {
                    Log.d(TAG,"receive playAlphaAnimationLD:${animator}")
                    animator?.cancel()
                    view.alpha = 1f
                }
            }
        })

        //error
        gameAboutModel.toastErrorMessage.observe(viewLifecycleOwner){msg->
            ToastUtil.showToastNormal(msg)
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

    /**
     * 刷新页面上注区里moneyView显示
     * 取消下注、结算时刷新中奖区域金额
     */
    private fun notifyMoneyOkView(list: List<BettingRecordBean>?) {
        if (list.isNullOrEmpty()) {
            moneyOkViewMap.forEach {
                it.value.let { moneyView ->
                    if (moneyView.isAdd()) {
                        val parent = moneyView.parent as ViewGroup
                        parent.removeView(moneyView)
                    }
                }
            }
            moneyOkViewMap.clear()
        } else {
            val iterator = moneyOkViewMap.iterator()
            var hasFlag: Boolean
            while (iterator.hasNext()) {
                hasFlag = false
                val entry = iterator.next()
                run beanEach@{
                    list.forEach { bettingRecordBean ->
                        if (bettingRecordBean.bettingArea.number == entry.key) {
                            entry.value.setShowMoney(bettingRecordBean.money)
                            hasFlag = true
                            return@beanEach
                        }
                    }
                }
                if (!hasFlag) {
                    if (entry.value.isAdd()) {
                        val parent = entry.value.parent as ViewGroup
                        parent.removeView(entry.value)
                    }
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
            if (rvHomeHistory.models.isNullOrEmpty()) return@apply
            resultAnim?.cancel()
            mViewModel.isShowResult = isShowResult
            if (resultRvHeight == -1) {
                rvHomeHistory.measureView()
                resultRvHeight = rvHomeHistory.measuredHeight
                Log.d(TAG, "resultRvHeight_rvHomeHistory: $resultRvHeight")
            }
            ivHomeRotation.rotation = if (isShowResult) 0f else 180f
            val startHeight = flRvHistory.height.toFloat()
            val endHeight =
                if (isShowResult) resultRvHeight else resultRvHeight - resultAnimMoveHeight
            resultAnim = ValueAnimator.ofFloat(startHeight, endHeight.toFloat()).apply {
                duration = 400
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mDatabind.llShowBetList.dividerSpace(
            requireContext().dp2px(10),
            DividerOrientation.HORIZONTAL
        ).setup {
            addType<SelectAnnotationBean>(R.layout.item_annotation_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_annotation_list -> {
                        val binding = getBinding<ItemAnnotationListBinding>()
                        val bean = _data as SelectAnnotationBean
                        val temporaryCurrentMoney = mViewModel.temporaryCurrentMoney
                        val id = if (temporaryCurrentMoney < bean.money) {
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
                            binding.ivShowBg.scaleX = 1.1f
                            binding.ivShowBg.scaleY = 1.1f
                        } else {
                            binding.ivShowBg.scaleX = 1f
                            binding.ivShowBg.scaleY = 1f
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
                            if (resultAnimMoveHeight == -1) {
                                llShowDice.measureView()
                                resultAnimMoveHeight = llShowDice.measuredHeight
                                llBetResult.measureView()
                                resultRvHeight = resultAnimMoveHeight + llBetResult.measuredHeight
                                Log.d(TAG, "resultAnimMoveHeight: $resultAnimMoveHeight")
                                Log.d(TAG, "llBetResult: ${resultRvHeight}}")
                                val params = mDatabind.flRvHistory.layoutParams
                                params?.height = llBetResult.measuredHeight
                                mDatabind.flRvHistory.layoutParams = params
                            }

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
        }.models = gameAboutModel.historyRounds.value
        lifecycleScope.launchWhenResumed {
            if (!mDatabind.rvHomeHistory.models.isNullOrEmpty()) {
                mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size - 1)
            }
            mDatabind.rlClickHide.isVisible = !mDatabind.rvHomeHistory.models.isNullOrEmpty()
        }
    }

    private fun setClick() {
        mDatabind.apply {
            rlClickHide.clickNoRepeat {
                PromptSoundPlay.btnPlayMedia(requireContext())
                resultAnimation(!mViewModel.isShowResult)
            }

            //点击更多弹出框
            llHomeMore.clickNoRepeat {
                if (homeMorePop == null) {
                    val bubbleAttach = CustomBubbleAttachPopup(requireContext())
                    bubbleAttach.customBubbleAttachListener =
                        object : CustomBubbleAttachPopup.CustomBubbleAttachListener {
                            override fun switchGame() {
                                homeMorePop!!.dismiss()
                            }
                        }
                    homeMorePop = XPopup.Builder(requireContext())
                        .hasShadowBg(false)
                        .isTouchThrough(true)
                        .atView(mDatabind.llHomeMore)
                        .hasShadowBg(false) // 去掉半透明背景
                        .asCustom(bubbleAttach)
                }
                PromptSoundPlay.btnPlayMedia(requireContext())
                homeMorePop!!.show()
            }
            //加倍
            ivMultiple2.clickNoRepeat {
                GameSocketManager.getInstance()?.getGameService()
                    ?.doubleBetting { isMoneyEnough, map ->
                        if (isMoneyEnough) {
                            if (!map.isNullOrEmpty()) {
                                map.forEach {
                                    it.value.let { record ->
                                        if (moneyOkViewMap.containsKey(record.bettingArea.number)) {
                                            moneyOkViewMap[record.bettingArea.number]?.setShowMoney(
                                                record.money
                                            )
                                        } else {
                                            //addview
                                        }
                                    }
                                }
                                anchorMoneyView?.showTop()
                            }
                        } else {
                            //余额不足
                        }
                    }
            }
            //续压
            ivXuya.clickNoRepeat {
                val map = GameSocketManager.getInstance()?.getGameService()?.againBetting()
                if (!map.isNullOrEmpty()) {
                    map.forEach {
                        allGameAreaMap[it.key.number]?.let { areaView ->
                            if (!areaView.moneyView.isAdd()) {
                                val params = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                params.gravity = areaView.okViewGravity
                                areaView.moneyView.let { moneyView ->
                                    moneyView.parentView?.addView(moneyView,params)
                                    moneyView.translationX = it.value.viewXYTemporary[0]
                                    moneyView.translationY = it.value.viewXYTemporary[1]
                                    moneyOkViewMap[it.key.number] = moneyView
                                    moneyView.setShowMoney(it.value.money)
                                }
                            }
                        }
                    }
                    //updateanchor
                    showAnchorTop()
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
        if (areaView.moneyView != anchorMoneyView) {
            hiddenAnchorTop()
        }
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
        //贝塞尔曲线中间过程的点的坐标
        val mCurrentPosition = FloatArray(2)
        val location = IntArray(2)
        jettonView.getLocationInWindow(location)
        val viewX = location[0] + jettonView.width / 2 - requireContext().dp2px(25)
        val viewY = location[1]

        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线))
        val betImageView = ImageView(requireContext())
        betImageView.setImageDrawable(jettonView.findViewById<ImageView>(R.id.ivShowBg).drawable)
        val params = RelativeLayout.LayoutParams(
            requireContext().dp2px(32),
            requireContext().dp2px(32)
        )
        mDatabind.rlRoot.addView(betImageView, params)

        //正式开始计算动画开始/结束的坐标
        val startX: Float = viewX.toFloat() + requireContext().dp2px(12)
        val startY: Float = viewY.toFloat() - requireContext().dp2px(24)

        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(x, y)
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
                    areaView.moneyView.ivShowBg,
                    SCALE_X,
                    SCALE_Y
                )
                animator.duration = 200
                animator.start()

                //筹码栈处理
                updateAnchorView(areaView)
//                addTempMoney(areaView)
            })
        }

        valueAnimator.duration = speed
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.start()
    }

//    private fun addTempMoney(areaView: GameAreaView) {
//        mViewModel.currentBettingRecordBean?.let {
//            if (areaView.areaCode == it.first.bettingArea.number) {
//                areaView.moneyView.setShowMoney(it.first.money)
//            }
//        }
//    }

    private fun updateAnchorView(areaView: GameAreaView) {
        hiddenAnchorTop()
        areaView.moneyView.showTop()
        anchorMoneyView = areaView.moneyView
        moneyOkViewMap[areaView.areaCode] = areaView.moneyView
        mDatabind.tempTouch.setAnchorMoneyView(areaView.moneyView)
    }

    private fun hiddenAnchorTop() {
        anchorMoneyView?.hiddenTop()
    }

    private fun showAnchorTop(){
        anchorMoneyView?.showTop()
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
