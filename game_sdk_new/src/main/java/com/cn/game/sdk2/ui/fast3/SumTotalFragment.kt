package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.databinding.FragmentSumTotalBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SumTotalVm
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.measureView
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import kotlinx.coroutines.launch

/**
 * 总和
 */
class SumTotalFragment(fast3VM: Fast3ViewModel) :
    BaseFast3Fragment<SumTotalVm, FragmentSumTotalBinding>(fast3VM) {

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
            areaViewList[i].areaInfo = mViewModel.bettingArray[i + 1]
            areaViewList[i].moneyView.pageIndex = 2
        }
    }

    override fun createObserver() {
        super.createObserver()
    }

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
            it.translationY = 0f
            it.translationX = 0f
            mDatabind.rlHomeRoot.addView(it, params)
        }
    }
}