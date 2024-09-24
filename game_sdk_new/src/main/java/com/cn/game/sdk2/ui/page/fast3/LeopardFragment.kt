package com.cn.game.sdk2.ui.page.fast3

import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.databinding.FragmentLeopardBinding
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * 豹子
 */
class LeopardFragment: BaseFast3Fragment<Fast3ViewModel, FragmentLeopardBinding>() {

    override val mBinding: FragmentLeopardBinding by viewBind()
    override val mViewModel: Fast3ViewModel  by sharedViewModel()

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