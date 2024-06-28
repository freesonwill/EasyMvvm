package com.cn.game.sdk2.ui.fast3

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.DXDSVm
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.DEFAULT_BIG
import com.cn.game.sdk2.websocket.bean.DEFAULT_DOUBLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SINGLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SMALL
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * 默认
 */
class DXDSFragment(fast3VM: Fast3ViewModel) : BaseFast3Fragment<DXDSVm, FragDxdsBinding>(fast3VM) {
    private var moneyViewList: SparseArray<Pair<TextView, TextView>> = SparseArray()
    private val numAnimators by lazy { mutableListOf<Animator>() }
    private val numAnimSet by lazy { AnimatorSet() }

    override fun initAreaViewList() {
        mDatabind.model = mViewModel
        mDatabind.apply {
            areaViewList.add(bigView)
            areaViewList.add(smallView)
            areaViewList.add(singleView)
            areaViewList.add(doubleView)
            areaViewList.add(leopardView)

            for (i in areaViewList.indices) {
                areaViewList[i].areaInfo = mViewModel.bettingArray[i + 1]
                areaViewList[i].moneyView.pageIndex = 0
            }
            moneyViewList[mViewModel.bettingArray[1].number] = txtBigMoney to txtBigNum
            moneyViewList[mViewModel.bettingArray[2].number] = txtSmallMoney to txtSmallNum
            moneyViewList[mViewModel.bettingArray[3].number] = txtSingleMoney to txtSingleNum
            moneyViewList[mViewModel.bettingArray[4].number] = txtDoubleMoney to txtDoubleNum

            bigView.flickerView = ivFlickerRightTop
            smallView.flickerView = ivFlickerLeftTop
            doubleView.flickerView = ivFlickerRightBelow
            singleView.flickerView = ivFlickerLeftBelow
            leopardView.flickerView = ivFlickerCenter

        }
    }

    override fun initData() {
        super.initData()
        updateAreaBetInfo(mViewModel.syncAreaBetInfoLD.value)
        //testUpdateAareaBetInfo()
    }

    private fun testUpdateAareaBetInfo() {
        lifecycleScope.launch {
            delay(1000)
            val ld = mViewModel.syncAreaBetInfoLD as MutableLiveData
            ld.value = mutableListOf<AreaBetBean>().also {
                it.add(AreaBetBean(DEFAULT_BIG(), 10090, 300))
                it.add(AreaBetBean(DEFAULT_SMALL(), 20012, 200))
                it.add(AreaBetBean(DEFAULT_SINGLE(), 30034, 100))
                it.add(AreaBetBean(DEFAULT_DOUBLE(), 3320034, 100))
            }
        }
    }

    private fun updateAreaBetInfo(list: List<AreaBetBean>?) {
        if (list == null) return
        Log.d(TAG, "updateAreaBetInfo--->" + list)
        numAnimators.clear()
        list.forEach { item ->
            val pair = moneyViewList.get(item.areaCode.number)
            if (pair != null) {
                pair.second.text = item.userCount.toString()
                val startNum = pair.first.text.toString().toFloatOrNull() ?: 0f
                val endNumber = item.betScore / 100f
                numAnimators.add(doNumberAnim(pair.first, startNum, endNumber))
            }
        }
        if (numAnimSet.isRunning) {
            Log.d(TAG, "updateAreaBetInfo--->running")
        } else {
            numAnimSet.playTogether(numAnimators)
            numAnimSet.start()
        }
    }

    /**
     * 默认玩法数字变化动画
     */
    private fun doNumberAnim(
        targetView: TextView,
        startNum: Float,
        endNumber: Float,
    ): ValueAnimator {
        return ValueAnimator.ofFloat(startNum, endNumber).apply {
            addUpdateListener {
                duration = 500
                targetView.text = (it.animatedValue as Float).toInt().toString()

            }
//            addListener(doOnEnd {
//                targetView.text = DecimalFormat("#.##").format(endNumber).toString()
//            })
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.syncAreaBetInfoLD.observe(viewLifecycleOwner) { list ->
            updateAreaBetInfo(list)
        }
        fast3VM.betOkClick.observe(viewLifecycleOwner) {

        }
        fast3VM.betDeleteClick.observe(viewLifecycleOwner) {

        }
        fast3VM.onGameAreaLocationClick.observe(viewLifecycleOwner) {

        }
    }

    /**
     * 添加moneyView 计算偏移
     */
    override fun addMoneyOkView(
        areaView: GameAreaView,
        x: Float,
        y: Float,
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

            //先添加view再计算位置执行动画
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.translationX = 0f
            it.translationY = 0f
            mDatabind.rlHomeRoot.addView(it, params)
        }
    }
}
