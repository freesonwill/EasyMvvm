package com.cn.game.sdk2.ui.fast3

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.databinding.FragmentLeopardBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.LeopardVm
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.measureView
import com.cn.game.sdk2.websocket.bean.BOOM
import com.cn.game.sdk2.websocket.bean.BOOM_1
import com.cn.game.sdk2.websocket.bean.BOOM_2
import com.cn.game.sdk2.websocket.bean.BOOM_3
import com.cn.game.sdk2.websocket.bean.BOOM_4
import com.cn.game.sdk2.websocket.bean.BOOM_5
import com.cn.game.sdk2.websocket.bean.BOOM_6
import kotlinx.coroutines.launch
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * 豹子
 */
class LeopardFragment(private val fast3VM:Fast3ViewModel):BaseFast3Fragment<LeopardVm,FragmentLeopardBinding>() {
    private var areaViewList: MutableList<GameAreaView> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.apply {
            gavLeopardOne.areaInfo = mViewModel.bettingArray[1]
            gavLeopardTwo.areaInfo =  mViewModel.bettingArray[2]
            gavLeopardThree.areaInfo =  mViewModel.bettingArray[3]
            gavLeopardFour.areaInfo =  mViewModel.bettingArray[3]
            gavLeopardFive.areaInfo =  mViewModel.bettingArray[4]
            gavLeopardSix.areaInfo =  mViewModel.bettingArray[5]

            areaViewList.add(gavLeopardOne)
            areaViewList.add(gavLeopardTwo)
            areaViewList.add(gavLeopardThree)
            areaViewList.add(gavLeopardFour)
            areaViewList.add(gavLeopardFive)
            areaViewList.add(gavLeopardSix)

            for (areaView in areaViewList) {
                areaView.moneyView.setMoneyOKClickListener(object :
                    MoneyOKView.OnMoneyOKClickListener {
                    override fun onConfirm() {
                        fast3VM.betOkClick.value = true;
                    }

                    override fun onDelete() {
                        fast3VM.betDeleteClick.value = true;
                    }
                })

                areaView.setOnLocationClickListener(object : GameAreaView.LocationClickListener {
                    override fun onLocationClick(x: Float, y: Float, rawX: Float, rawY: Float) {
                        if (!areaView.moneyView.isAdd()) {
                            addMoneyOkView(areaView)
                        } else {
                            emitMoneyAnim(areaView, areaView.moneyView)
                        }
                    }
                })
            }
        }
    }

    override fun initData() {
        super.initData()
        mDatabind.model = mViewModel
    }

    override fun createObserver() {
        super.createObserver()
        fast3VM.userLotteryResultLiveData.observe(viewLifecycleOwner) { resultList ->
            setLotteryResult(resultList, areaViewList, fast3VM.prizeAnimTime / 5, 5)
        }
//        fast3VM.historyResultBeanLD.observe(viewLifecycleOwner) { bean ->
//            lifecycleScope.launch {
//                val views = mutableListOf<View>().also {
//                    when (bean.resultLeopard) {
//                        1 -> it.add(mDatabind.ivLeopardOne)
//                        2 -> it.add(mDatabind.ivLeopardTwo)
//                        3 -> it.add(mDatabind.ivLeopardThree)
//                        4 -> it.add(mDatabind.ivLeopardFour)
//                        5 -> it.add(mDatabind.ivLeopardFive)
//                        6 -> it.add(mDatabind.ivLeopardSix)
//                    }
//                }
//                playAlphaAnimTogether(views, fast3VM.prizeAnimTime / 5, 5)
//            }
//        }
    }

    private fun addMoneyOkView(areaView: GameAreaView) {
        areaView.moneyView.let {
            val viewTreeObserver = it.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // 确保只监听一次
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    emitMoneyAnim(areaView, it, isNewAdd = true)
                }
            })

            //先添加view再计算位置执行动画
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.CENTER
            areaView.addView(it, params)
        }
    }

    private fun emitMoneyAnim(
        areaView: GameAreaView,
        moneyOKView: MoneyOKView,
        isNewAdd: Boolean = false
    ) {
        val location = moneyOKView.locationOnScreen
        val rax = location[0].toFloat()
        val ray = location[1].toFloat() + moneyOKView.measuredHeight / 2
        if (isNewAdd) {
            moneyOKView.isVisible = false
        }
        fast3VM.emitMoneyAnim(rax, ray, areaView = areaView, endCallBack = {
            moneyOKView.isVisible = true
//            显示点击在Fragment的位置用于动画结束后显示
//            val location1 = IntArray(2)
//            moneyOKView.getLocationInWindow(location1)
//            moneyOKView.viewXYTemporary[0] = moneyOKView.left
//            moneyOKView.viewXYTemporary[1] = moneyOKView.top
        })
    }
}