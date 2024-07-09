package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.FragmentLeopardBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.LeopardVm
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import kotlinx.coroutines.launch

/**
 * 豹子
 */
class LeopardFragment(fast3VM: Fast3ViewModel) :
    BaseFast3Fragment<LeopardVm, FragmentLeopardBinding>(fast3VM) {

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

        for (i in areaViewList.indices){
            areaViewList[i].areaInfo = mViewModel.bettingArray[i+1]
            areaViewList[i].moneyView.pageIndex = 4
        }
    }

    override fun initData() {
        super.initData()
        mDatabind.model = mViewModel
    }

    override fun createObserver() {
        super.createObserver()
//        fast3VM.userLotteryResultLiveData.observe(viewLifecycleOwner) { resultList ->
//            setLotteryResult(resultList, areaViewList, fast3VM.prizeAnimTime / 5, 5)
//        }
    }

    override fun addMoneyOkView(
        areaView: GameAreaView,
        rawX:Float,
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

            fast3VM.addMoneyOkViewLiveData.value =Pair(areaView,mDatabind.flRoot)
        }
    }
}