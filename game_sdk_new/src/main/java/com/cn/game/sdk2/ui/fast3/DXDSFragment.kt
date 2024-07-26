package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.Log
import android.util.SparseArray
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.util.forEach
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.xcjh.base_lib2.utils.LogUtils

/**
 * 默认
 */
class DXDSFragment() : BaseFast3Fragment<Fast3ViewModel, FragDxdsBinding>() {
    private var moneyViewList: SparseArray<Pair<TextView, TextView>> = SparseArray()
    private val numAnimators by lazy { mutableListOf<Animator?>() }
    private var numAnimSet: AnimatorSet? = null
    private val txtValueAnimMap by lazy { mutableMapOf<TextView, ValueAnimator>() }

    override fun initAreaViewList() {
        mDatabind.model = mViewModel
        mDatabind.apply {
            areaViewList.add(bigView.also { it.flickerView = ivFlickerRightTop })
            areaViewList.add(smallView.also { it.flickerView = ivFlickerLeftTop })
            areaViewList.add(singleView.also { it.flickerView = ivFlickerLeftBelow })
            areaViewList.add(doubleView.also { it.flickerView = ivFlickerRightBelow })
            areaViewList.add(leopardView.also { it.flickerView = ivFlickerCenter })

            for (i in areaViewList.indices) {
                areaViewList[i].areaInfo = mViewModel.dXDSBettingArray[i + 1]
                areaViewList[i].moneyView.pageIndex = 0
            }

            moneyViewList[mViewModel.dXDSBettingArray[1].number] = txtBigMoney to txtBigNum
            moneyViewList[mViewModel.dXDSBettingArray[2].number] = txtSmallMoney to txtSmallNum
            moneyViewList[mViewModel.dXDSBettingArray[3].number] = txtSingleMoney to txtSingleNum
            moneyViewList[mViewModel.dXDSBettingArray[4].number] = txtDoubleMoney to txtDoubleNum
        }
    }

    override fun initData() {
        super.initData()
        updateAreaBetInfo(gameAboutModel.syncAreaBetInfo.value)
    }

    private fun updateAreaBetInfo(list: List<AreaBetBean>?) {
        if (list == null) return
        LogUtils.dTag(TAG, "updateAreaBetInfo--->$list")
        if (numAnimSet?.isRunning == true) {
            LogUtils.dTag(TAG, "updateAreaBetInfo--->running")
            return
        }
        numAnimators.clear()
        list.forEach { item ->
            moneyViewList.get(item.areaCode.number)?.apply {
                numAnimators.add(
                    doNumberAnim(
                        first,
                        startNum = first.text.toString().toFloatOrNull() ?: 0f,
                        endNumber = item.betScore / 100f
                    )
                )
                numAnimators.add(
                    doNumberAnim(
                        second,
                        startNum = second.text.toString().toFloatOrNull() ?: 0f,
                        endNumber = item.userCount.toFloat()
                    )
                )
            }
        }
        numAnimSet = AnimatorSet().apply {
            playTogether(numAnimators)
            start()
        }
    }

    /**
     * 默认玩法数字变化动画
     */
    private fun doNumberAnim(
        targetView: TextView,
        startNum: Float,
        endNumber: Float,
    ): ValueAnimator? {
        if (txtValueAnimMap.containsKey(targetView)) {
            return txtValueAnimMap[targetView]?.apply { setFloatValues(startNum, endNumber) }
        } else {
            val anim = ValueAnimator.ofFloat(startNum, endNumber).apply {
                addUpdateListener {
                    duration = 500
                    targetView.text = (it.animatedValue as Float).toInt().toString()
                }
            }
            txtValueAnimMap[targetView] = anim
            return anim
        }
    }

    override fun createObserver() {
        super.createObserver()
        gameAboutModel.syncAreaBetInfo.observe(viewLifecycleOwner) { list ->
            updateAreaBetInfo(list)
        }
        gameAboutModel.currentStage.observe(viewLifecycleOwner) { stage ->
            when (stage) {
                //开局将下注人数置为0
                GameAboutModel.Stage.NEW -> {
                    moneyViewList.forEach { _, v ->
                        v.first.text = 0.toString()
                        v.second.text = 0.toString()
                    }
                }

                else -> {}
            }
        }
    }

    /**
     * 添加moneyView 计算偏移
     */
    override fun addMoneyOkView(
        areaView: GameAreaView,
        rawX: Float,
        rawY: Float,
        emitAnimCallBack: () -> Unit
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

            mViewModel.addMoneyOkViewLiveData.value = Pair(areaView, mDatabind.flRoot)
        }
    }
}
