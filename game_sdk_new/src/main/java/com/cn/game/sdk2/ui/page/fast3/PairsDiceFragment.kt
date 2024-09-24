package com.cn.game.sdk2.ui.page.fast3

import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.databinding.FragmentPairsDiceBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * 对子
 */
class PairsDiceFragment: BaseFast3Fragment<Fast3ViewModel, FragmentPairsDiceBinding>() {

    override val mBinding: FragmentPairsDiceBinding by viewBind()
    override val mViewModel: Fast3ViewModel  by sharedViewModel()

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

    override fun createObserver() {
        super.createObserver()
    }

    override fun addMoneyOkView(
        areaView: GameAreaView,
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
            FlowBus.with<Pair<GameAreaView, ViewGroup>>(EventKey.AddMoneyOkView).post(lifecycleScope,Pair(areaView,mBinding.flRoot))
        }
    }
}