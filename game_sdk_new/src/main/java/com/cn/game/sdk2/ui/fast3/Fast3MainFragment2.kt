package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.databinding.FragFast3Home2Binding
import com.cn.game.sdk2.databinding.ItemAnnotationListBinding
import com.cn.game.sdk2.databinding.ItemBetHistoryBinding
import com.cn.game.sdk2.manager.GameManager
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.ui.view.CustomBubbleAttachPopup
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.Fast3ViewModel
import com.cn.game.sdk2.utils.Ext.toPinyin
import com.cn.game.sdk2.utils.MyGameManager
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.delay

class Fast3MainFragment2 : BaseVmDbFragment<Fast3ViewModel, FragFast3Home2Binding>() {
    private val TAG = "Fast3MainFragment2"
    private var mFragList = ArrayList<Fragment>()

    // 定义属性动画常量
    private val SCALE_X = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.4f, 1.0f)
    private val SCALE_Y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.4f, 1.0f)
    var betView: View? = null
    var betMoney: Int = 0

    /**
     * 是否显示骰子的结果组合
     */
    private var isShowResult: Boolean = true
    var homeMorePop: BasePopupView? = null
    private val initialUpperLayoutHeightMap = mutableMapOf<Int, Int>()
    private val resultAnimatorList by lazy { mutableListOf<Animator>() }
    private val resultAnimatorSet by lazy { AnimatorSet() }
    private var resultAnimMoveHeight = 0
    private var isAdd: Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        GameManager.instance.gameState = GameState.Betting
        mDatabind.model = mViewModel
        lifecycleScope.launchWhenResumed {
            mViewModel.startCounterDown(10)
        }
        //viewpager
        mFragList.add(DXDSFragment2(mViewModel))
        mDatabind.viewPagerNew.initGameViewPager(
            childFragmentManager, mFragList, arrayListOf(
                getString(R.string.g_home_txt_default),
                getString(R.string.g_home_tab_single),
                getString(R.string.g_home_tab_double),
                getString(R.string.g_home_tab_leopard),
                getString(R.string.g_home_tab_sum)
            )
        )
        mDatabind.magicIndicator.bindViewPagerNewGame(
            mDatabind.viewPagerNew, arrayListOf(
                getString(R.string.g_home_txt_default),
                getString(R.string.g_home_tab_single),
                getString(R.string.g_home_tab_double),
                getString(R.string.g_home_tab_leopard),
                getString(R.string.g_home_tab_sum)
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

    @SuppressLint("NotifyDataSetChanged")
    override fun createObserver() {
        mViewModel.timeLiveData.observe(requireActivity()) { seconds ->
            if (seconds != 0 && seconds <= 5 && GameManager.instance.gameState == GameState.Betting) {
                PromptSoundPlay.countdownGameTip(requireContext())
            }
            //mDatabind.txtHomeTime.text = seconds.toString()
        }

        mViewModel.historyResultBeans.observe(requireActivity()) {
            val adapter = mDatabind.rvHomeHistory.adapter as BindingAdapter
            adapter._data?.clear()
            adapter._data?.addAll(it)
            adapter.notifyDataSetChanged()
        }
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
    private fun resultAnimation() {
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
    private fun setBetAdapter() {
        mDatabind.llShowBetList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )
        mDatabind.llShowBetList.setup {
            addModels(MyGameManager.noteList)
            addType<SelectAnnotationBean>(R.layout.item_annotation_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_annotation_list -> {
                        if (betView == null) {
                            betView = itemView
                            betMoney = MyGameManager.noteList[0].money
                        }
                        val binding = getBinding<ItemAnnotationListBinding>()
                        val bean = _data as SelectAnnotationBean
                        val temporaryCurrentMoney = MyGameManager.temporaryCurrentMoney
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
                        Log.d(TAG, "onBind-->${layoutPosition},bean:${bean}")
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
                val models: List<SelectAnnotationBean> = models as List<SelectAnnotationBean>
                val positions = mutableListOf<Int>(layoutPosition)
                //选择新的筹码
                for (i in models.indices) {
                    if (models[i].select) positions.add(i)
                    models[i].select = false
                    MyGameManager.noteList[i].select = false
                }
                bean.select = true
                positions.forEach { notifyItemChanged(it) }

                MyGameManager.noteList[modelPosition].select = true
                betMoney = MyGameManager.noteList[modelPosition].money
                betView = itemView
            }
        }

        val historyBeans = mViewModel.historyResultBeans.value
        //历史结果
        mDatabind.rvHomeHistory.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL, false
        )
        mDatabind.rvHomeHistory.setup {
            addModels(historyBeans)
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
                        binding.txtBetSize.text =
                            if (mainTxtBean.resultSize == "big") getString(R.string.g_home_txt_big)
                            else getString(R.string.g_home_txt_small)
                        binding.txtBetOdd.text =
                            if (mainTxtBean.resultOdd == "odd") getString(R.string.g_home_txt_single)
                            else getString(R.string.g_home_txt_double)
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
        }
        //滚动到最后一条
        lifecycleScope.launchWhenResumed {
            delay(200)
            mDatabind.rvHomeHistory.scrollToPosition(mDatabind.rvHomeHistory.models!!.size - 1)
        }
    }

    private fun setClick() {
        mDatabind.rlClickHide.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia(requireContext())
            if (mViewModel.isClickOperation.value == true) {
                resultAnimation()
            }
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
        //MyGameManager.mTimer!!.stop()
        MyGameManager.countDownTimer!!.cancel()
        MyGameManager.countDownTimer = null
        //关闭动画
        resultAnimatorSet.cancel()
        resultAnimatorList.clear()
        super.onDestroy()
    }


    /**
     * 执行动画  isCentered如果是true就是可以超出父类的
     */
    fun tryMoneyAnimation(
        x: Float,
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
            startMoneyAnimation(x, y, isCentered, speed, areaView, finallyView)
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
                finallyView?.let { startMoneyAnimation(x, y, isCentered, speed, areaView, it) }
            }
        }

    }

    private fun startMoneyAnimation(
        x: Float,
        y: Float,
        isCentered: Boolean = false,
        speed: Long = 300,
        areaView: GameAreaView,
        jettonView: View
    ) {
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

