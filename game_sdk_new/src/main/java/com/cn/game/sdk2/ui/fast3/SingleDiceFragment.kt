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
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.data.enums.NOTES_ENUM
import com.cn.game.sdk2.databinding.FragmentSingleDiceBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SingleDiceVm
import com.cn.game.sdk2.utils.ToastUtil
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.areaMap
import com.cn.game.sdk2.websocket.gameMassageManager
import kotlinx.coroutines.launch


/**
 * 默认
 */
class SingleDiceFragment(fast3VM: Fast3ViewModel) :
    BaseFast3Fragment<SingleDiceVm, FragmentSingleDiceBinding>(fast3VM) {

    override fun initAreaViewList() {
        mDatabind.apply {
            model = mViewModel
            areaViewList = mutableListOf(
                gavDiceOne.also { it.flickerView = ivSingleOne },
                gavDiceTwo.also { it.flickerView = ivSingleTwo },
                gavDiceThree.also { it.flickerView = ivSingleThree },
                gavDiceFour.also { it.flickerView = ivSingleFour },
                gavDiceFive.also { it.flickerView = ivSingleFive },
                gavDiceSix.also { it.flickerView = ivSingleSix },
            )

            for (i in areaViewList.indices) {
                areaViewList[i].areaInfo = mViewModel.bettingArray[i + 1]
                areaViewList[i].moneyView.pageIndex = 1
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
