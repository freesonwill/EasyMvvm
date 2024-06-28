package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.databinding.FragmentPairsDiceBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.PairsDiceVm
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import kotlinx.coroutines.launch

/**
 * 对子
 */
class PairsDiceFragment(fast3VM: Fast3ViewModel) :
    BaseFast3Fragment<PairsDiceVm, FragmentPairsDiceBinding>(fast3VM) {

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

            for (i in areaViewList.indices){
                areaViewList[i].areaInfo = mViewModel.bettingArray[i+1]
                areaViewList[i].pageIndex = 3
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
    }

    override fun addMoneyOkView(
        areaView: GameAreaView,
        x: Float,
        y: Float,
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
                    emitAnimCallBack.invoke()
                }
            })

            //先添加view再计算位置执行动画
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = areaView.okViewGravity
            areaView.addView(it, params)
        }
    }
}