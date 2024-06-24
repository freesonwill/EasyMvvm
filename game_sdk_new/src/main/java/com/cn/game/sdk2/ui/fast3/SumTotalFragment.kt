package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.databinding.FragmentSumTotalBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SumTotalVm
import kotlinx.coroutines.launch

/**
 * 总和
 */
class SumTotalFragment(private val fast3VM: Fast3ViewModel) : BaseFast3Fragment<SumTotalVm, FragmentSumTotalBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.model = mViewModel
    }

    override fun createObserver() {
        super.createObserver()
        fast3VM.historyResultBeanLD.observe(viewLifecycleOwner) { bean ->
            lifecycleScope.launch {
                val views = mutableListOf<View>().also {
                    when (bean.resultSum) {
                        4 -> it.add(mDatabind.ivSumFlashFour)
                        5 -> it.add(mDatabind.ivSumFlashFive)
                        6 -> it.add(mDatabind.ivSumFlashSix)
                        7 -> it.add(mDatabind.ivSumFlashSeven)
                        8 -> it.add(mDatabind.ivSumFlashEight)
                        9 -> it.add(mDatabind.ivSumFlashNine)
                        10 -> it.add(mDatabind.ivSumFlashTen)
                        11 -> it.add(mDatabind.ivSumFlashEleven)
                        12 -> it.add(mDatabind.ivSumFlashTwelve)
                        13 -> it.add(mDatabind.ivSumFlashThirteen)
                        14 -> it.add(mDatabind.ivSumFlashFourteen)
                        15 -> it.add(mDatabind.ivSumFlashFifteen)
                        16 -> it.add(mDatabind.ivSumFlashSixteen)
                        17 -> it.add(mDatabind.ivSumFlashSeventeen)
                    }
                }
                playAlphaAnimTogether(views, fast3VM.prizeAnimTime / 5, 5)
            }
        }
    }
}