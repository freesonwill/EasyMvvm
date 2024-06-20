package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.databinding.FragmentPairsDiceBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.PairsDiceVm
import kotlinx.coroutines.launch

/**
 * 对子
 */
class PairsDiceFragment(private val fast3VM: Fast3ViewModel):BaseGameFragment<PairsDiceVm,FragmentPairsDiceBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createObserver() {
        super.createObserver()
        fast3VM.historyResultBeanLD.observe(viewLifecycleOwner) { bean ->
            lifecycleScope.launch {
                val views = mutableListOf<View>().also {
                    when (bean.resultPair) {
                        1 -> it.add(mDatabind.ivPairsOne)
                        2 -> it.add(mDatabind.ivPairsTwo)
                        3 -> it.add(mDatabind.ivPairsThree)
                        4 -> it.add(mDatabind.ivPairsFour)
                        5 -> it.add(mDatabind.ivPairsFive)
                        6 -> it.add(mDatabind.ivPairsSix)
                    }
                }
                playAlphaAnimTogether(views,fast3VM.prizeAnimTime/5,5)
            }
        }
    }
}