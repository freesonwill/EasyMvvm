package com.cn.game.sdk2.ui.page.fast3

import android.view.ViewTreeObserver
import com.cn.game.sdk2.databinding.FragmentLeopardBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel

/**
 * 豹子
 */
class LeopardFragment: BaseFast3Fragment<Fast3ViewModel, FragmentLeopardBinding>() {

    override fun initAreaViewList() {
        mDatabind.model = mViewModel
        mDatabind.apply {
            areaViewList.add(gavLeopardOne.also { it.flickerView = ivLeopardOne })
            areaViewList.add(gavLeopardTwo.also { it.flickerView = ivLeopardTwo })
            areaViewList.add(gavLeopardThree.also { it.flickerView = ivLeopardThree })
            areaViewList.add(gavLeopardFour.also { it.flickerView = ivLeopardFour })
            areaViewList.add(gavLeopardFive.also { it.flickerView = ivLeopardFive })
            areaViewList.add(gavLeopardSix.also { it.flickerView = ivLeopardSix })
        }

        for (i in areaViewList.indices) {
            areaViewList[i].areaInfo = mViewModel.leopardBettingArray[i + 1]
            areaViewList[i].moneyView.pageIndex = 4
        }
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

            mViewModel.addMoneyOkViewLiveData.value = Pair(areaView, mDatabind.flRoot)
        }
    }
}