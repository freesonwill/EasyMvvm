package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.data.enums.NOTES_ENUM
import com.cn.game.sdk2.databinding.FragmentSingleDiceBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SingleDiceVm
import kotlinx.coroutines.launch


/**
 * 默认
 */
class SingleDiceFragment(private var fast3VM: Fast3ViewModel) : BaseFast3Fragment<SingleDiceVm, FragmentSingleDiceBinding>() {
    private lateinit var areaViewList: MutableList<GameAreaView>

    override fun initView(savedInstanceState: Bundle?) {
        areaViewList = mutableListOf(
            mDatabind.gavDiceOne.also { it.areaCode = NOTES_ENUM.QTSingle1.num},
            mDatabind.gavDiceTwo.also { it.areaCode = NOTES_ENUM.QTSingle2.num},
            mDatabind.gavDiceThree.also { it.areaCode = NOTES_ENUM.QTSingle3.num},
            mDatabind.gavDiceFour.also { it.areaCode = NOTES_ENUM.QTSingle4.num},
            mDatabind.gavDiceFive.also { it.areaCode = NOTES_ENUM.QTSingle5.num},
            mDatabind.gavDiceSix.also { it.areaCode = NOTES_ENUM.QTSingle6.num},
        )

    }

    override fun createObserver() {
        super.createObserver()
        fast3VM.historyResultBeanLD.observe(viewLifecycleOwner) { bean ->
            lifecycleScope.launch {
                val views = mutableListOf<View>().also {
                    if(bean.result.contains(1)) it.add(mDatabind.ivSingleOne)
                    if(bean.result.contains(2)) it.add(mDatabind.ivSingleTwo)
                    if(bean.result.contains(3)) it.add(mDatabind.ivSingleThree)
                    if(bean.result.contains(4)) it.add(mDatabind.ivSingleFour)
                    if(bean.result.contains(5)) it.add(mDatabind.ivSingleFive)
                    if(bean.result.contains(6)) it.add(mDatabind.ivSingleSix)
                }
                playAlphaAnimTogether(views,fast3VM.prizeAnimTime/5,5)
            }
        }
    }

}
