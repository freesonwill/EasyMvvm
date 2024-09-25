package com.cn.game.sdk2.ui.page.fast3

import com.cn.game.sdk2.databinding.FragmentPairsDiceBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.PairsDiceViewModel
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 对子
 */
class PairsDiceFragment: BaseGameFragment<PairsDiceViewModel, FragmentPairsDiceBinding>() {

    override val mBinding: FragmentPairsDiceBinding by viewBind()
    override val mViewModel: PairsDiceViewModel  by viewModel()

    override fun initAreaViewList() {
        mBinding.model = mViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.apply {
            areaViewList = mutableListOf(
                gavPairsOne.also { it.flickerView = ivPairsOne },
                gavPairsTwo.also { it.flickerView = ivPairsTwo },
                gavPairsThree.also { it.flickerView = ivPairsThree },
                gavPairsFour.also { it.flickerView = ivPairsFour },
                gavPairsFive.also { it.flickerView = ivPairsFive },
                gavPairsSix.also { it.flickerView = ivPairsSix },
            )

            for (i in areaViewList.indices) {
                areaViewList[i].areaInfo = mViewModel.pairsDiceBettingArray[i + 1]
                areaViewList[i].moneyView.pageIndex = 3
            }
        }
    }
}