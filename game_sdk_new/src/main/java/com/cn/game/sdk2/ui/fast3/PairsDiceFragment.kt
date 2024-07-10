package com.cn.game.sdk2.ui.fast3

import android.view.ViewTreeObserver
import com.cn.game.sdk2.databinding.FragmentPairsDiceBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel

/**
 * 对子
 */
class PairsDiceFragment() : BaseFast3Fragment<Fast3ViewModel,FragmentPairsDiceBinding>() {

    override fun initAreaViewList() {
        mDatabind.model = mViewModel
        mDatabind.apply {
            areaViewList = mutableListOf(
                gavPairsOne.also { it.flickerView = ivPairsOne },
                gavPairsTwo.also { it.flickerView = ivPairsTwo },
                gavPairsThree.also { it.flickerView = ivPairsThree },
                gavPairsFour.also { it.flickerView = ivPairsFour },
                gavPairsFive.also { it.flickerView = ivPairsFive },
                gavPairsSix.also { it.flickerView = ivPairsSix },
            )

            for (i in areaViewList.indices) {
                areaViewList[i].areaInfo = mViewModel.pairsDiceBettingArray[i + 1]
                areaViewList[i].moneyView.pageIndex = 3
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
    }

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
            mViewModel.addMoneyOkViewLiveData.value =Pair(areaView,mDatabind.flRoot)
        }
    }
}