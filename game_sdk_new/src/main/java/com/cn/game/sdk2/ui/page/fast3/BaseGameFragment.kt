package com.cn.game.sdk2.ui.page.fast3

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.data.enums.ChipBean
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment.Companion
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.view.game.MoneyOKView
import com.cn.game.sdk2.ui.viewmodel.ChipsViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.GameViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ext.CommonExt.isCanGoOn
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.gameMassageManager
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.utils.LogUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/21 18:14
 **/
abstract class BaseGameFragment<VM : ViewModel, VB : ViewBinding> :
    BaseFragment<VM, VB>() {
    protected val TAG = this::class.java.simpleName
    private val chipViewModel: ChipsViewModel by sharedViewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()
    protected var areaViewList: MutableList<GameAreaView> = mutableListOf()
    private var areaFlickAnimatorSet: AnimatorSet? = null

    override fun initView(savedInstanceState: Bundle?) {/*lifecycle.addObserver(object : DefaultLifecycleObserver {
            var startTime:Long = 0
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                startTime = System.currentTimeMillis()
            }
            override fun onStart(owner: LifecycleOwner) {
                super.onResume(owner)
                (System.currentTimeMillis() - startTime).let{ LogUtils.dTag(TAG,"${this@BaseFast3Fragment.javaClass.simpleName} load costMills:$it") }
            }
        })*/
        initAreaViewList()
        if (gameAboutModel.isisAllowedBet.value != false) {
            for (areaView in areaViewList) {
                setMoneyOKClickListener(areaView)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val isCurrentRound =
            gameAboutModel.roundId.isNotEmpty() &&
                    gameAboutModel.previousRoundId.isNotEmpty() &&
                    gameAboutModel.roundId != "0" &&
                    gameAboutModel.previousRoundId != "0" &&
                    gameAboutModel.roundId == gameAboutModel.previousRoundId
        if (isCurrentRound && gameAboutModel.tempMap.isNotEmpty() && gameAboutModel.currentStage.value == GameStage.NEW) {
            for (areaView in areaViewList) {
                gameAboutModel.tempMap.forEach {
                    if (areaView.areaCode == it.key.number) {
                        areaView.againAdd(it)
                        areaView.setShowMoney(it.value.money)
                        areaView.moneyView.hiddenTop()
                        FlowBus.with<GameAreaView>(EventKey.UpdateMoneyView).post(lifecycleScope,areaView)
                    }
                }
            }
        }
    }

    abstract fun initAreaViewList()

    override fun createObserver() {
        gameAboutModel.currentStage.observe(viewLifecycleOwner) { stage: GameStage ->
            when (stage) {
                GameStage.NEW -> areaFlickAnimatorSet?.cancel()
                GameStage.DEAL -> {}
                GameStage.SETTLE -> {

                }
            }
        }
        FlowBus.with<Boolean>(EventKey.PLAY_DRAW_HISTORY_ANIM).register(viewLifecycleOwner){
            LogUtils.dTag(Fast3MainFragment.TAG, "中奖注区结果监听--->${gameAboutModel.lotteryResultList}")
            gameAboutModel.lotteryResultList?.let { result ->
                setLotteryResult(result, gameViewModel.prizeAnimTime, gameViewModel.prizeAnimCount)
            }
        }
    }

    override fun lazyLoadData() {
        FlowBus.with<List<GameAreaView>>(EventKey.UPDATE_ALL_AREA_VIEW)
            .post(mViewModel.viewModelScope, areaViewList)
    }

    fun finishFragClick() {
        parentFragmentManager.popBackStack()
    }

    open fun finishTopClick(view: View?) {
        activity?.finish()
    }

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    override fun lazyLoadTime(): Long {
        return 300
    }

    /**
     * 泛型的高级特性 泛型实例化
     * 跳转
     */
    inline fun <reified T> startNewActivity(block: Intent.() -> Unit = {}) {
        val intent = Intent(this.activity, T::class.java)
        //把intent实例 传入block 函数类型参数
        intent.block()
        startActivity(intent)
    }

    private fun setLotteryResult(
        resultList: ArrayList<Betting>, duration: Long, count: Int
    ) {
        val views = mutableListOf<View>()
        for (areaView in areaViewList) {
            if (resultList.contains(areaView.areaInfo)) {
                views.add(areaView.flickerView)
            }
        }
        if (views.isNotEmpty()) {
            playAlphaAnimTogether(views, duration, count)
        }
    }

    /**
     * 播放透明度动画
     */
    private fun playAlphaAnimTogether(dic: List<View>, duration: Long, count: Int) {
        //LogUtils.dTag(TAG,"playAlphaAnimTogether begin:${mViewModel.playAlphaAnimationLD.value},${javaClass.simpleName}")
        areaFlickAnimatorSet?.cancel()
        val animators = (dic.map { maskView ->
            val animator = ObjectAnimator.ofFloat(maskView, "alpha", 0f, 1f).apply {
                this.duration = duration // 设置动画持续时间
                this.repeatCount = ValueAnimator.INFINITE // 设置无限循环
                this.repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                this.addListener(onStart = {
                    maskView.isVisible = true
                }, onEnd = {
                    maskView.isVisible = false
                })
            }
            animator
        })
        areaFlickAnimatorSet = AnimatorSet().apply {
            playTogether(animators)
            addListener(onEnd = {
                //LogUtils.dTag(TAG,"playAlphaAnimTogether end:${mViewModel.playAlphaAnimationLD.value},${javaClass.simpleName}")
            })
            start()
        }
    }

    private fun setMoneyOKClickListener(areaView: GameAreaView) {
        areaView.moneyView.setMoneyOKClickListener(object : MoneyOKView.OnMoneyOKClickListener {
            override fun onConfirm() {
                gameViewModel.betOkClick.value = true;
            }

            override fun onDelete() {
                gameViewModel.betDeleteClick.value = true;
            }
        })

        areaView.setOnLocationClickListener(object : GameAreaView.LocationClickListener {
            override fun onLocationClick(rawX: Float, rawY: Float) {
                addBetting(areaView, rawX, rawY)
            }
        })
    }

    private fun addBetting(areaView: GameAreaView, rawX: Float, rawY: Float) {
        //先判断余额是否够这次 并且扣取钱
        if (gameAboutModel.currentStage.value == GameStage.NEW) {
            chipViewModel.currentChip?.let { betteBean ->
                areaView.areaInfo?.apply {
                    val isFirstAdd = !areaView.moneyView.isAdd()
                    val roundId = gameAboutModel.roundId
                    val bettingBean = BettingRecordBean(roundId, this, money = betteBean.chip.money)
                    gameMassageManager?.addBetting(bettingBean, isFirstAdd) { bettingState, result, areaLimit ->
                        bettingState.isCanGoOn(mBinding.root, areaLimit) {
                            result?.let {
                                areaView.setShowMoney(result.money, false)
                                if (isFirstAdd) {
                                    addMoneyOkView(areaView, rawX, rawY) {
                                        emitMoneyAnim(result, areaView, betteBean, isNewAdd = true)
                                    }
                                } else {
                                    emitMoneyAnim(result, areaView, betteBean)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addMoneyOkView(
        areaView: GameAreaView, rawX: Float, rawY: Float, emitAnimCallBack: () -> Unit
    ) {
        areaView.moneyView.let {
            val viewTreeObserver = it.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // 确保只监听一次
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    handleViewTranslation(it, areaView, rawX, rawY)
                    emitAnimCallBack.invoke()
                }
            })
            FlowBus.with<Pair<GameAreaView, ViewGroup>>(EventKey.AddMoneyOkView).post(lifecycleScope,Pair(areaView,mBinding.root as ViewGroup))
        }
    }

    protected fun handleViewTranslation(
        it: MoneyOKView,
        areaView: GameAreaView,
        rawX: Float,
        rawY: Float,
    ) {
        val betteView = it.findViewById<ImageView>(R.id.ivShowBg)
        val betteLocation = betteView.locationOnScreen
        val betteX = betteLocation[0]
        val betteY = betteLocation[1]

        val moneyLocation = it.locationOnScreen
        val moneyX = moneyLocation[0]

        val areaLocation = areaView.locationOnScreen
        val areaX = areaLocation[0]
        val areaY = areaLocation[1]

        //筹码最终偏移的位置
        val endX = rawX - betteView.measuredWidth / 2
        val endY = rawY - betteView.measuredHeight / 2
        //偏移距离
        val dx = endX - betteX
        val dy = endY - betteY

        //是否左右边界
        val isLeftStart =
            areaView.id == R.id.small_view || areaView.id == R.id.single_view || areaView.id == R.id.gavDiceOne || areaView.id == R.id.gavDiceSix || areaView.id == R.id.gavSumFour || areaView.id == R.id.gavSumNine || areaView.id == R.id.gavSumFourteen || areaView.id == R.id.gavPairsOne || areaView.id == R.id.gavPairsSix || areaView.id == R.id.gavLeopardOne || areaView.id == R.id.gavLeopardSix
        val isRightEnd =
            areaView.id == R.id.big_view || areaView.id == R.id.double_view || areaView.id == R.id.gavDiceTwo || areaView.id == R.id.gavDiceFive || areaView.id == R.id.gavSumEight || areaView.id == R.id.gavSumThirteen || areaView.id == R.id.gavPairsTwo || areaView.id == R.id.gavPairsFive || areaView.id == R.id.gavLeopardTwo || areaView.id == R.id.gavLeopardFive

        val limitLeft = if (areaView.id == R.id.double_view) {
            val leopardLocation = IntArray(2)
            (mBinding as FragDxdsBinding).leopardView.getLocationOnScreen(leopardLocation)
            val leopardX = leopardLocation[0]
            leopardX + (mBinding as FragDxdsBinding).leopardView.measuredWidth
        } else {
            areaX + (if (isLeftStart) 0 else if (areaView.id == R.id.big_view) 6.dp2px else 4.dp2px)
        }
        val limitRight = if (areaView.id == R.id.single_view) {
            val leopardLocation = IntArray(2)
            (mBinding as FragDxdsBinding).leopardView.getLocationOnScreen(leopardLocation)
            val leopardX = leopardLocation[0]
            leopardX
        } else {
            areaX + areaView.measuredWidth - (if (isRightEnd) 0 else 4.dp2px)
        }
        val limitBottom = when (areaView.id) {
            R.id.big_view -> {
                val smallLocation = IntArray(2)
                (mBinding as FragDxdsBinding).ivSmall.getLocationOnScreen(smallLocation)

                val leopardLocation = (mBinding as FragDxdsBinding).leopardView.locationOnScreen
                if (endX + betteView.measuredWidth <= leopardLocation[0] + (mBinding as FragDxdsBinding).leopardView.measuredWidth) {
                    leopardLocation[1] - 4.dp2px
                } else {
                    smallLocation[1]
                }
            }

            R.id.small_view -> {
                val smallLocation = IntArray(2)
                (mBinding as FragDxdsBinding).ivSmall.getLocationOnScreen(smallLocation)

                val leopardLocation = (mBinding as FragDxdsBinding).leopardView.locationOnScreen
                if (endX + betteView.measuredWidth >= leopardLocation[0]) {
                    leopardLocation[1] - 4.dp2px
                } else {
                    smallLocation[1]
                }
            }

            R.id.single_view, R.id.double_view -> {
                val smallLocation = IntArray(2)
                (mBinding as FragDxdsBinding).ivSingle.getLocationOnScreen(smallLocation)
                smallLocation[1]
            }

            R.id.leopard_view -> {
                areaY + areaView.measuredHeight - 18.dp2px
            }

            else -> {
                areaY + areaView.measuredHeight - 4.dp2px
            }
        }
        val limitTop = areaY + 6.dp2px

        it.translationX = when {
            isLeftStart -> {
                when {
                    dx >= 0 && endX + betteView.measuredWidth <= limitRight -> dx
                    endX + betteView.measuredWidth > limitRight -> dx + limitRight - endX - betteView.measuredWidth
                    else -> 0f
                }
            }

            isRightEnd -> {
                when {
                    endX >= limitLeft && moneyX + dx + it.measuredWidth <= limitRight -> dx
                    endX < limitLeft -> dx + limitLeft - endX
                    moneyX + dx + it.measuredWidth > limitRight -> (limitRight - moneyX - it.measuredWidth).toFloat()
                    else -> 0f
                }
            }

            else -> {
                when {
                    endX >= limitLeft && endX + betteView.measuredWidth <= limitRight -> dx
                    endX < limitLeft -> dx + limitLeft - endX
                    endX + betteView.measuredWidth > limitRight -> dx + limitRight - endX - betteView.measuredWidth
                    else -> 0f
                }
            }
        }

        it.translationY = when {
//            endY + betteView.measuredHeight > limitBottom -> (limitBottom - betteY - betteView.measuredHeight).toFloat()
            endY + betteView.measuredHeight > limitBottom -> dy - (endY + betteView.measuredHeight - limitBottom)
            endY < limitTop -> (limitTop - betteY).toFloat()
            else -> dy
        }

        areaView.betteView.translationX = it.translationX
        areaView.betteView.translationY = it.translationY
    }

    private fun emitMoneyAnim(
        recordBean: BettingRecordBean,
        areaView: GameAreaView,
        betteBean: ChipBean,
        isNewAdd: Boolean = false
    ) {
        val moneyOKView = areaView.moneyView
        val betteView = areaView.betteView

        recordBean.viewXYTemporary[0] = moneyOKView.translationX
        recordBean.viewXYTemporary[1] = moneyOKView.translationY
        Log.e(
            Fast3MainFragment.TAG,
            "坐标信息--->${moneyOKView.translationX} ${moneyOKView.translationY}"
        )
        val location = moneyOKView.ivShowBg.locationOnScreen
        val rax = location[0].toFloat()
        val ray = location[1].toFloat()
        if (isNewAdd) {
            moneyOKView.ivShowBg.isInvisible = true
            moneyOKView.parentView = moneyOKView.parent as ViewGroup
            betteView.parentView = betteView.parent as ViewGroup
            betteView.isVisible = false
        }
        gameViewModel.emitMoneyAnim(
            rax,
            ray,
            areaView = areaView,
            betteBean = betteBean,
            endCallBack = {
                betteView.isVisible = true
                areaView.updateBetteIcon(recordBean.money)
            })
    }

    override fun onDestroy() {
        areaFlickAnimatorSet?.cancel()

        // 銷毀時通知main移除注區view，避免洩漏
        for (areaView in areaViewList) {
            // 需使用activity lifecycleScope, 預防fragment destroy時lifecycleScope被銷毀導致無法post
            FlowBus.with<Int>(EventKey.RemoveMoneyView).post(requireActivity().lifecycleScope, areaView.areaCode)
        }
        super.onDestroy()
    }
}