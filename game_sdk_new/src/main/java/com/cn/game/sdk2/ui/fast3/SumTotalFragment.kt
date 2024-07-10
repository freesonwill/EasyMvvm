package com.cn.game.sdk2.ui.fast3

import android.view.ViewTreeObserver
import com.cn.game.sdk2.databinding.FragmentSumTotalBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel

/**
 * 总和
 */
class SumTotalFragment() : BaseFast3Fragment<Fast3ViewModel,FragmentSumTotalBinding>() {
    override fun initAreaViewList() {
        mDatabind.model = mViewModel
        mDatabind.apply {
            areaViewList = mutableListOf(
                gavSumFour.also { it.flickerView = ivSumFlashFour },
                gavSumFive.also { it.flickerView = ivSumFlashFive },
                gavSumSix.also { it.flickerView = ivSumFlashSix },
                gavSumSeven.also { it.flickerView = ivSumFlashSeven },
                gavSumEight.also { it.flickerView = ivSumFlashEight },
                gavSumNine.also { it.flickerView = ivSumFlashNine },
                gavSumTen.also { it.flickerView = ivSumFlashTen },
                gavSumEleven.also { it.flickerView = ivSumFlashEleven },
                gavSumTwelve.also { it.flickerView = ivSumFlashTwelve },
                gavSumThirteen.also { it.flickerView = ivSumFlashThirteen },
                gavSumFourteen.also { it.flickerView = ivSumFlashFourteen },
                gavSumFifteen.also { it.flickerView = ivSumFlashFifteen },
                gavSumSixteen.also { it.flickerView = ivSumFlashSixteen },
                gavSumSeventeen.also { it.flickerView = ivSumFlashSeventeen },
            )
        }

        for (i in areaViewList.indices) {
            areaViewList[i].areaInfo = mViewModel.sumTotalBettingArray[i + 4]
            areaViewList[i].moneyView.pageIndex = 2
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