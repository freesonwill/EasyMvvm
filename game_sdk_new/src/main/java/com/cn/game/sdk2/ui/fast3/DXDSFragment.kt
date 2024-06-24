package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.view.game.IGameView
import com.cn.game.sdk2.ui.viewmodel.fast3.DXDSVm
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.utils.tool.measureView
import kotlinx.coroutines.launch
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.bean.BOOM_ALL
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.DEFAULT_BIG
import com.cn.game.sdk2.websocket.bean.DEFAULT_DOUBLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SINGLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SMALL


/**
 * 默认
 */
class DXDSFragment(var fast3VM: Fast3ViewModel) : BaseFast3Fragment<DXDSVm, FragDxdsBinding>() {
    private var areaViewList: MutableList<GameAreaView> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.model = mViewModel
        mDatabind.apply {
            bigView.areaInfo = mViewModel.bettingArray[1]
            smallView.areaInfo = mViewModel.bettingArray[2]
            singleView.areaInfo = mViewModel.bettingArray[3]
            doubleView.areaInfo = mViewModel.bettingArray[4]
            leopardView.areaInfo = mViewModel.bettingArray[5]

            areaViewList.add(bigView)
            areaViewList.add(smallView)
            areaViewList.add(singleView)
            areaViewList.add(doubleView)
            areaViewList.add(leopardView)
        }

        val dic = mapOf(
            mDatabind.bigView to mDatabind.txtOddsBig,
            mDatabind.smallView to mDatabind.txtOddsSmall,
            mDatabind.singleView to mDatabind.txtOddsSingle,
            mDatabind.doubleView to mDatabind.txtOddsDouble,
            mDatabind.leopardView to mDatabind.txtOddsLeopard,
        )
        dic.forEach {
            it.value.text = it.key.areaInfo!!.multiplier.toString()
            it.key.gameCallback = object : IGameView {
                override fun winFlash() {
                    it.key.tvOdds = it.value
                }

                override fun bindView() {
                }

            }
        }

        for (areaView in areaViewList) {
            areaView.moneyView.setMoneyOKClickListener(object : MoneyOKView.OnMoneyOKClickListener {
                override fun onConfirm() {
                    fast3VM.betOkClick.value = true;
                }

                override fun onDelete() {
                    fast3VM.betDeleteClick.value = true;
                }
            })
            areaView.setOnLocationClickListener(object : GameAreaView.LocationClickListener {
                override fun onLocationClick(x: Float, y: Float, rawX: Float, rawY: Float) {
                    //处理点击事件
                    //先判断余额是否够这次 并且扣取钱
                    //if( homeXPopupDialog.isCanBetting()&&MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                    //todo:整个流程转移至FastMainFragment
                    if (fast3VM.isClickOperation && PromptSoundPlay.handleClick()) {
                        GameSocketManager.getInstance()?.getGameService()?.apply {
                            addBetting(BettingRecordBean(areaView.areaInfo!!, money = fast3VM.betMoney)){ isMoneyEnough, result ->
                                if(isMoneyEnough){
                                    if (result != null) {
                                        fast3VM.currentBettingRecordBean = Pair(result,areaView.moneyView)
                                        if (!areaView.moneyView.isAdd()) {
                                            addMoneyOkView(areaView, x, y, rawY)
                                        }else{
                                            emitMoneyAnim(areaView, areaView.moneyView)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    override fun createObserver() {
        super.createObserver()
        fast3VM.betOkClick.observe(viewLifecycleOwner) {

        }
        fast3VM.betDeleteClick.observe(viewLifecycleOwner){

        }
        fast3VM.onGameAreaLocationClick.observe(viewLifecycleOwner) {

        }
        fast3VM.historyResultBeanLD.observe(viewLifecycleOwner) { bean ->
            lifecycleScope.launch {
                val views = mutableListOf<View>().also {
                    if(bean.resultSingle == "single") it.add(mDatabind.ivFlickerLeftBelow) else it.add(mDatabind.ivFlickerRightBelow)
                    if(bean.resultSize == "big") it.add(mDatabind.ivFlickerRightTop) else it.add(mDatabind.ivFlickerLeftTop)
                    if(bean.resultLeopard != null) it.add(mDatabind.ivFlickerCenter)
                }
                playAlphaAnimTogether(views,fast3VM.prizeAnimTime/5,5)
            }
        }
    }

    /**
     * 添加moneyView 计算偏移
     */
    private fun addMoneyOkView(areaView: GameAreaView, x: Float, y: Float, rawY: Float) {
        areaView.moneyView.let {
            val viewTreeObserver = it.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // 确保只监听一次
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    //默认偏移控件中心点
                    val dx = x - it.measuredWidth / 2
                    val dy = y - it.measuredHeight / 2

                    //处理水平偏移
                    val areaWidth = areaView.width
                    when (areaView.id) {
                        R.id.small_view, R.id.big_view -> {
                            it.translationX = when {
                                //不超出左右边界
                                dx > 0 && dx + it.measuredWidth <= areaWidth -> dx
                                dx + it.measuredWidth > areaWidth -> (areaWidth - it.measuredWidth).toFloat()
                                else -> 0f
                            }
                        }

                        R.id.single_view -> {
                            //单 处理有边界
                            val leopardLocation = IntArray(2)
                            mDatabind.leopardView.getLocationOnScreen(leopardLocation)
                            val leopardX = leopardLocation[0]
                            it.translationX = when {
                                //不超出左右边界
                                dx > 0 && dx + it.measuredWidth <= leopardX -> dx
                                dx + it.measuredWidth > leopardX -> (leopardX - it.measuredWidth).toFloat()
                                else -> 0f
                            }
                        }

                        R.id.double_view -> {
                            //双 处理左边界
                            val leopardLocation = IntArray(2)
                            mDatabind.leopardView.getLocationOnScreen(leopardLocation)
                            val leopardX = leopardLocation[0]
                            val leftX =
                                leopardX + mDatabind.leopardView.width - areaWidth
                            it.translationX = when {
                                //不超出左右边界
                                dx > leftX && dx + it.measuredWidth <= areaWidth -> dx
                                dx + it.measuredWidth > areaWidth -> (areaWidth - it.measuredWidth).toFloat()
                                else -> leftX.toFloat()
                            }
                        }
                    }

                    //处理垂直偏移
                    val areaLocation = IntArray(2)
                    areaView.getLocationOnScreen(areaLocation)
                    val areaY = areaLocation[1]
                    when (areaView.id) {
                        // 小 大
                        R.id.small_view, R.id.big_view -> {
                            val smallLocation = IntArray(2)
                            mDatabind.txtSmallMoney.getLocationOnScreen(smallLocation)
                            val textY = smallLocation[1]
                            it.translationY = when {
                                rawY + it.measuredHeight / 2 > textY -> (textY - areaY - areaView.height).toFloat()
                                else -> dy
                            }
                        }

                        //单双
                        R.id.single_view, R.id.double_view -> {
                            val singLocation = IntArray(2)
                            mDatabind.txtSingleMoney.getLocationOnScreen(singLocation)
                            val textY = singLocation[1]
                            it.translationY = when {
                                rawY + it.measuredHeight / 2 > textY -> (textY - areaY - areaView.height).toFloat()
                                else -> dy
                            }
                        }
                    }

                    emitMoneyAnim(areaView, it, isNewAdd = true)
                }
            })

            //先添加view再计算位置执行动画
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            // 将新按钮设置为居底部，方便向上偏移
            // 豹子暂时居中，不做偏移
            params.gravity =
                if (areaView.id == R.id.leopard_view) Gravity.CENTER else Gravity.BOTTOM
            areaView.addView(it, params)
        }
    }

    private fun emitMoneyAnim(areaView: GameAreaView, moneyOKView: MoneyOKView,isNewAdd:Boolean = false) {
        val location = moneyOKView.locationOnScreen
        val rax = location[0].toFloat()
        val ray = location[1].toFloat() + moneyOKView.measuredHeight / 2
        if(isNewAdd){
            moneyOKView.isVisible = false
        }
        fast3VM.emitMoneyAnim(rax, ray, areaView = areaView, endCallBack = {
            moneyOKView.isVisible = true
//            显示点击在Fragment的位置用于动画结束后显示
//            val location1 = IntArray(2)
//            moneyOKView.getLocationInWindow(location1)
//            moneyOKView.viewXYTemporary[0] = moneyOKView.left
//            moneyOKView.viewXYTemporary[1] = moneyOKView.top
        })
    }

}
