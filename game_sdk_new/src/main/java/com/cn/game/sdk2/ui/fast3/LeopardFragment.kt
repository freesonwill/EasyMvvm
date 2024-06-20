package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.databinding.FragmentLeopardBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.LeopardVm
import kotlinx.coroutines.launch

/**
 * 豹子
 */
class LeopardFragment(private val fast3VM:Fast3ViewModel):BaseGameFragment<LeopardVm,FragmentLeopardBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createObserver() {
        super.createObserver()
        fast3VM.historyResultBeanLD.observe(viewLifecycleOwner) { bean ->
            lifecycleScope.launch {
                val views = mutableListOf<View>().also {
                    when (bean.resultLeopard) {
                        1 -> it.add(mDatabind.ivLeopardOne)
                        2 -> it.add(mDatabind.ivLeopardTwo)
                        3 -> it.add(mDatabind.ivLeopardThree)
                        4 -> it.add(mDatabind.ivLeopardFour)
                        5 -> it.add(mDatabind.ivLeopardFive)
                        6 -> it.add(mDatabind.ivLeopardSix)
                    }
                }
                playAlphaAnimTogether(views,fast3VM.prizeAnimTime/5,5)
            }
        }
    }
}