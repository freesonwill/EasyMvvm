package com.cn.game.sdk2.ui.page.fast3

import com.cn.game.sdk2.databinding.FragmentLeopardBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.LeopardViewModel
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 豹子
 */
class LeopardFragment: BaseGameFragment<LeopardViewModel, FragmentLeopardBinding>() {

    override val mBinding: FragmentLeopardBinding by viewBind()
    override val mViewModel: LeopardViewModel  by viewModel()

    override fun initAreaViewList() {
        mBinding.model = mViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.apply {
            areaViewList.add(gavLeopardOne.also { it.flickerView = ivLeopardOne })
            areaViewList.add(gavLeopardTwo.also { it.flickerView = ivLeopardTwo })
            areaViewList.add(gavLeopardThree.also { it.flickerView = ivLeopardThree })
            areaViewList.add(gavLeopardFour.also { it.flickerView = ivLeopardFour })
            areaViewList.add(gavLeopardFive.also { it.flickerView = ivLeopardFive })
            areaViewList.add(gavLeopardSix.also { it.flickerView = ivLeopardSix })
        }

        for (i in areaViewList.indices) {
            areaViewList[i].areaInfo = mViewModel.leopardBettingArray[i + 1]
            areaViewList[i].moneyView.pageIndex = 4
        }
    }

}