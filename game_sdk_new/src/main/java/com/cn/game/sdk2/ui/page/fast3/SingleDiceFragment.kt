package com.cn.game.sdk2.ui.page.fast3

import com.cn.game.sdk2.databinding.FragmentSingleDiceBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.SingleDiceViewModel
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 默认
 */
class SingleDiceFragment: BaseGameFragment<SingleDiceViewModel, FragmentSingleDiceBinding>() {

    override val mBinding: FragmentSingleDiceBinding by viewBind()
    override val mViewModel: SingleDiceViewModel  by viewModel()

    override fun initAreaViewList() {
        mBinding.apply {
            model = mViewModel
            mBinding.lifecycleOwner = viewLifecycleOwner
            areaViewList = mutableListOf(
                gavDiceOne.also { it.flickerView = ivSingleOne },
                gavDiceTwo.also { it.flickerView = ivSingleTwo },
                gavDiceThree.also { it.flickerView = ivSingleThree },
                gavDiceFour.also { it.flickerView = ivSingleFour },
                gavDiceFive.also { it.flickerView = ivSingleFive },
                gavDiceSix.also { it.flickerView = ivSingleSix },
            )

            for (i in areaViewList.indices) {
                areaViewList[i].areaInfo = mViewModel.singleDiceBettingArray[i + 1]
                areaViewList[i].moneyView.pageIndex = 1
            }
        }
    }
}
