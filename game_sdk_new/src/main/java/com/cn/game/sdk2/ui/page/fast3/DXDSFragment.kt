package com.cn.game.sdk2.ui.page.fast3

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.SparseArray
import android.widget.TextView
import androidx.core.util.forEach
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.DXDSViewModel
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.base.fragment.viewBind
import com.xcjh.base_lib2.utils.LogUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 默认
 */
class DXDSFragment : BaseGameFragment<DXDSViewModel, FragDxdsBinding>() {
    override val mBinding: FragDxdsBinding by viewBind()
    private var moneyViewList: SparseArray<Pair<TextView, TextView>> = SparseArray()
    private val numAnimators by lazy { mutableListOf<Animator?>() }
    private var numAnimSet: AnimatorSet? = null
    private val txtValueAnimMap by lazy { mutableMapOf<TextView, ValueAnimator>() }
    override val mViewModel: DXDSViewModel by viewModel()

    override fun initAreaViewList() {
        mBinding.model = mViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.apply {
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
                GameStage.NEW -> {
                    moneyViewList.forEach { _, v ->
                        v.first.text = 0.toString()
                        v.second.text = 0.toString()
                    }
                }

                else -> {}
            }
        }
    }

    override fun onDestroy() {
        numAnimSet?.cancel()
        super.onDestroy()
    }
}
