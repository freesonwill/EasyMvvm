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
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.HistoryResultBean
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
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.GameSocketManager
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.bean.MutablePair
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Fast3MainFragment : BaseVmDbFragment<Fast3ViewModel, FragFast3HomeBinding>() {
    private val TAG = "Fast3MainFragment"
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
    private var savedMoneyMap: MutableMap<Int, MutablePair<Int, MoneyOKView>> = mutableMapOf()
    private var tempMoneyMap: MutableMap<Int, MutablePair<Int, MoneyOKView>> = mutableMapOf()
    //==================================== Method ===============================================//
    override fun initView(savedInstanceState: Bundle?) {
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
        //点击更多弹出框
        mDatabind.llHomeMore.clickNoRepeat {
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
    }

    override fun lazyLoadData() {

    }

    override fun initData() {
        //获取当前余额
        mDatabind.txtCurrentMoney.text = mViewModel.currentMoney.value.toString()
        lifecycleScope.launchWhenResumed {
            //开始下注
            Log.d(TAG, "initData startBetting")
            mViewModel.startBetting()
        }
    }

    private fun onStartBetting() {
        lifecycleScope.launch {
            showLoading(getString(R.string.g_home_betting_begin), 2000)
            //开始语音
            PromptSoundPlay.startGameTip(requireContext())
            mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_txt_please)
            //下注闪动动画
            suspendCoroutine { continuation ->
                val childAlphaAnimator = ObjectAnimator.ofFloat(mDatabind.llShowBetList, "alpha", 0f, 1f)
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
                        mDatabind.ivHomeBgCenter.visibility = View.GONE
                        hiddenView(true)
                        continuation.resume("")
                    }
                })
                animatorSet.start()
            }
            //倒计时
            mViewModel.startCountDown(mViewModel.bettingCountDownTime)
        }
    }

    private fun onStartSetting() { //开始结算
        lifecycleScope.launch {
            showLoading(getString(R.string.g_home_setting_begin), 1000)
            //关闭
            PromptSoundPlay.endGameTip(requireContext())
            mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_balance)
            //结算的时候要把每个模块中奖的信息显示出来 默认
            /*homeDefaultFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())
            singleDiceFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())
            pairsDiceFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())
            leopardFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())

            sumTotalFragment.flicker(ArrayList<InPrizeBean>(), ArrayList<InPrizeBean>())*/
            suspendCoroutine { continuation ->
                val childAlphaAnimator = ObjectAnimator.ofFloat(mDatabind.llShowBetList, "alpha", 1f, 0f)
                childAlphaAnimator.duration = 200 // 设置渐隐动画持续时间
                val animatorSet = AnimatorSet()
                animatorSet.play(childAlphaAnimator)
                animatorSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        //注区
                        mDatabind.llShowBetList.visibility = View.GONE
                        //显示开奖结果
                        mDatabind.rlShowResult.visibility = View.VISIBLE
                        mDatabind.ivHomeBg.visibility = View.VISIBLE
                        mDatabind.ivHomeBgCenter.visibility = View.VISIBLE
                        hiddenView()
                        continuation.resume("finish")
                    }
                })
                animatorSet.start()
            }
            mViewModel.startCountDown(mViewModel.settingCountDownTime)
        }

    }

    private fun onStartDrawing() { //开始开奖
        lifecycleScope.launch {
            showLoading(getString(R.string.g_home_drawing_begin), 1000)
            mDatabind.txtHomeStatic.text = resources.getString(R.string.g_home_drawing_being)
        }
    }
    private fun onDrawFinish(){
        lifecycleScope.launch {
            delay(mViewModel.prizeAnimTime + 2000)
            //播放开奖动画
            mViewModel.startBetting()
        }
    }

    override fun createObserver() {
        Log.i(TAG, "createObserver------------>")
        mViewModel.onGameAreaLocationClick.observe(viewLifecycleOwner) {

        }

        mViewModel.homeTimeVisibility.observe(requireActivity()) {
            mDatabind.txtHomeTime.visibility = it
            mDatabind.txtHomeUnit.visibility = it
        }
        mViewModel.gameStateLV.observe(requireActivity()) {
            Log.i(TAG, "gameStateLV changed:${it}")
            mViewModel.isClickOperation = it == GameState.Betting
            when (it) {
                GameState.Betting -> {
                    onStartBetting()
                }

                GameState.Settling -> {
                    onStartSetting()
                }

                GameState.Drawing -> {
                    onStartDrawing()
                }

                GameState.DrawFinish -> {
                    onDrawFinish()
                }

                else -> {}
            }
        }
        mViewModel.homeTime.observe(requireActivity()) { seconds ->
            mDatabind.txtHomeTime.text = seconds.toString()
            if (mViewModel.gameState == GameState.Betting && seconds != 0 && seconds <= 5) {
                PromptSoundPlay.countdownGameTip(requireContext())
            }
        }
        mViewModel.historyResultBeanLD.observe(requireActivity()) { bean ->
            val adapter = mDatabind.rvHomeHistory.bindingAdapter
            adapter.addModels(mutableListOf(bean),false)
            //mViewModel.historyResultBeans.add(bean)
            Log.d(TAG, "onDrawingResult run on $isMainThread result:$bean," + mViewModel.historyResultBeans.size)
            if (adapter.models!!.isNotEmpty()) {
                mDatabind.rvHomeHistory.scrollToPosition(adapter.models!!.size - 1)
            }
            mDatabind.rlClickHide.isVisible = adapter.models!!.isNotEmpty()
        }
        mViewModel.moneyAnimCallback = object : Fast3ViewModel.MoneyAnimCallback {
            override fun startAnim(x: Float, y: Float, isCentered: Boolean, speed: Long, areaView: GameAreaView, endCallBack: (() -> Unit)?) {
                tryMoneyAnimation(x, y, speed, areaView, endCallBack)
            }
        }
        mViewModel.betOkClick.observe(this) {
            hiddenAnchorTop()
            //todo:bet失败处理
            GameSocketManager.getInstance()?.getGameService()?.commitBetting()
            /*var tempMoney: Int
            //将tempMap中的数据更新至savedMap
            for (key in mViewModel.tempMoneyMap.keys) {
                tempMoney = mViewModel.tempMoneyMap[key]?.first ?: 0
                //保存新数据saved+temp
                if (mViewModel.savedMoneyMap.containsKey(key)) {
                    mViewModel.savedMoneyMap[key]?.apply { first += tempMoney }
                    //创建saved数据
                } else {
                    mViewModel.tempMoneyMap[key]?.let {
                        mViewModel.savedMoneyMap[key] = MutablePair(tempMoney, (it.second))
                    }
                }
            }*/
        }

        mViewModel.betDeleteClick.observe(this) {
            hiddenAnchorTop()
            GameSocketManager.getInstance()?.getGameService()?.cancelBetting { result ->
                if(null == result){
                    mViewModel.tempBetRecordMap.forEach{
                        it.value.second.get()?.let { moneyView ->
                            if (moneyView.isAdd()) {
                                val parent = moneyView.parent as ViewGroup
                                parent.removeView(moneyView)
                            }
                        }
                    }
                }else {
                    result.forEach {
                        if (mViewModel.tempBetRecordMap.containsKey(it.bettingArea.number)) {
                            mViewModel.tempBetRecordMap[it.bettingArea.number]?.first = it
                            mViewModel.tempBetRecordMap[it.bettingArea.number]?.apply {
                                second.get()?.setShowMoney(first.money)
                            }
                        } else {
                            mViewModel.tempBetRecordMap[it.bettingArea.number]?.second?.get()
                                ?.let { moneyView ->
                                    if (moneyView.isAdd()) {
                                        val parent = moneyView.parent as ViewGroup
                                        parent.removeView(moneyView)
                                    }
                                }
                            mViewModel.tempBetRecordMap.remove(it.bettingArea.number)
                        }
                    }
                }
            }

            /*for (key in mViewModel.tempMoneyMap.keys) {
                //还原savedmap中的数据
                if (mViewModel.savedMoneyMap.containsKey(key)) {
                    mViewModel.savedMoneyMap[key]?.apply {
                        second.get()?.apply {
                            setShowMoney(first)
                        }
                    }
                    //直接移除MoneyView
                } else {
                    mViewModel.tempMoneyMap[key]?.second?.get()?.let {
                        if(it.isAdd()) {
                            val parent = it.parent as ViewGroup
                            parent.removeView(it)
                        }
                    }
                }
            }
            mViewModel.tempMoneyMap.clear()*/
        }
    }


    /**
     * 开奖结果隐藏不要的控件  。如果是投注的话就不应酬
     */
    fun hiddenView(isBetting: Boolean = false) {
        if (mViewModel.isShowResult && !isBetting) {
            resultAnimation()
        }
    }


    /**
     * 开奖结果显示或者隐藏动画
     */
    @SuppressLint("ObjectAnimatorBinding")
    fun resultAnimation() {
        mDatabind.apply {
            if (rvHomeHistory.models.isNullOrEmpty()) return
            resultAnim?.cancel()
            mViewModel.isShowResult = !mViewModel.isShowResult
            if (resultRvHeight == -1) {
                resultRvHeight = rvHomeHistory.height
            }
            if (resultAnimMoveHeight == -1) {
                val manager = rvHomeHistory.layoutManager as LinearLayoutManager
                val firstPosition = manager.findFirstVisibleItemPosition()
                val viewHolder = rvHomeHistory.findViewHolderForLayoutPosition(firstPosition) ?: return@apply
                val llShowDice = viewHolder.itemView.findViewById<LinearLayout>(R.id.llShowDice)
                resultAnimMoveHeight = llShowDice.height
            }
            ivHomeRotation.rotation = if (mViewModel.isShowResult) 0f else 180f
            val startHeight = flRvHistory.height.toFloat()
            val endHeight = if (mViewModel.isShowResult) resultRvHeight else resultRvHeight -resultAnimMoveHeight

            resultAnim =
                ValueAnimator.ofFloat(startHeight, endHeight.toFloat()).apply {
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
        mDatabind.llShowBetList.setup {
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
        mDatabind.rvHomeHistory.dividerSpace(requireContext().dp2px(2),DividerOrientation.HORIZONTAL)
        mDatabind.rvHomeHistory.setup {
            addType<HistoryResultBean>(R.layout.item_bet_history)
            onBind {
                when (itemViewType) {
                    R.layout.item_bet_history -> {
                        val binding = getBinding<ItemBetHistoryBinding>()
                        val mainTxtBean = _data as HistoryResultBean
                        mainTxtBean.result.forEachIndexed { index, item ->
                            val child = binding.llShowDice.getChildAt(index) as ImageView
                            val id = resources.getIdentifier(
                                "icon_dice_" + item.toPinyin(),
                                "drawable",
                                requireContext().packageName
                            )
                            child.setImageResource(id)
                        }
                        binding.txtBetNum.text = mainTxtBean.resultSum.toString()
                        binding.txtBetSize.text = if (mainTxtBean.resultSize == "big") getString(R.string.g_home_txt_big)
                            else getString(R.string.g_home_txt_small)
                        binding.txtBetOdd.text = if (mainTxtBean.resultSingle == "single") getString(R.string.g_home_txt_single)
                            else getString(R.string.g_home_txt_double)
                    }
                }
            }
        }.models = mViewModel.historyResultBeans
        lifecycleScope.launchWhenResumed {
            if (mDatabind.rvHomeHistory.models!!.isNotEmpty()) {
                mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size - 1)
            }
            mDatabind.rlClickHide.isVisible = mDatabind.rvHomeHistory.models!!.isNotEmpty()
        }
    }

    private fun setClick() {
        mDatabind.rlClickHide.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia(requireContext())
            resultAnimation()
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"onDetach~~~~~~~~~~~~~~")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"onDestroyView~~~~~~~~~~~~~~")
    }

    /**
     * 关闭页面
     */
    override fun onDestroy() {
        Log.d(TAG,"onDestroy~~~~~~~~~~~~~~")
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
        val betMoney = betList[selectedPosition].money
        //mViewModel.betMoney = betList[selectedPosition].money
        val layoutManager = mDatabind.llShowBetList.layoutManager as LinearLayoutManager
        var finallyView = layoutManager.findViewByPosition(selectedPosition)

        //判断选择的筹码是不是在屏幕外面
        if (finallyView != null) {
            startMoneyAnimation(x, y, speed, areaView, finallyView, betMoney, endCallBack)
        } else {
            scrollToItemAndPerformAction(mDatabind.llShowBetList, selectedPosition) {
                finallyView = layoutManager.findViewByPosition(selectedPosition)
                finallyView?.let {
                    startMoneyAnimation(x, y, speed, areaView, it, betMoney, endCallBack)
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
        betMoney: Int,
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
        //gameArea点击区域坐标(用于计算动画结束后的坐标)  动画结束的位置
        val endLoc = IntArray(2)
        endLoc[0] = x.toInt() + requireContext().dp2px(20)
        endLoc[1] = y.toInt() + requireContext().dp2px(20)
        //正式开始计算动画开始/结束的坐标
        val startX: Float = viewX.toFloat() + requireContext().dp2px(12)
        val startY: Float = viewY.toFloat() - requireContext().dp2px(24)

        //掉落后的终点坐标
        val toX: Float = endLoc[0].toFloat()
        val toY = endLoc[1].toFloat() - requireContext().dp2px(32)

        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(toX, toY)
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
                updateAnchorView(areaView.moneyView)
                addTempMoney(areaView)
            })
        }

        valueAnimator.duration = speed
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.start()
    }

    private fun addTempMoney(areaView: GameAreaView) {
        mViewModel.currentBettingRecordBean?.let {
            if(areaView.areaCode == it.bettingArea.number){
                areaView.moneyView.setShowMoney(mViewModel.currentBettingRecordBean!!.money)
            }
        }
    }

    fun updateAnchorView(anchor: MoneyOKView){
        hiddenAnchorTop()
        anchor.showTop()
        anchorMoneyView = anchor
        mDatabind.tempTouch.setAnchorMoneyView(anchor)
    }

    fun hiddenAnchorTop(){
        anchorMoneyView?.hiddenTop()
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
