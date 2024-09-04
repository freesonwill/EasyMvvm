package com.cn.game.sdk2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.databinding.FragmentChipsBinding
import com.cn.game.sdk2.ui.adapter.ChipsAdapter
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.view.CenterLayoutManager
import com.cn.game.sdk2.ui.view.CommonLinearLayoutItemDecoration
import com.cn.game.sdk2.ui.viewmodel.ChipsViewModel
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import com.xcjh.base_lib2.utils.LogUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChipsFragment : BaseFragment<ChipsViewModel, FragmentChipsBinding>(), ChipsViewImp {

    companion object {
        const val TAG = "ChipsFragment"
    }

    override val mBinding: FragmentChipsBinding by viewBind()
    override val mViewModel: ChipsViewModel by sharedViewModel()
    private lateinit var chipsAdapter: ChipsAdapter

    override fun initView(savedInstanceState: Bundle?) {
        setChipsView()
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }

    private fun setChipsView() {
        mBinding.rvChips.apply {
            itemAnimator = null
            layoutManager =
                CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    CommonLinearLayoutItemDecoration(
                        spacingV = requireContext().dp2px(10),
                        start = requireContext().dp2px(8),
                        end = requireContext().dp2px(40)
                    )
                )
            }
            chipsAdapter = ChipsAdapter()
            adapter = chipsAdapter
            chipsAdapter.onItemClickListener = { item ->
                if (!item.select && item.money <= (gameAboutModel.tempBalance.value ?: 0)) {
                    PromptSoundPlay.btnPlayMedia()
                    mViewModel.setSelectedChip(item)
                    notifyDataSetChangedSafe {
                        scrollSelectPosition2Center(true)
                    }
                }
            }
            chipsAdapter.submitList(mViewModel.chipsList)
        }
    }

    private fun notifyDataSetChangedSafe(action: () -> Unit) {
        chipsAdapter.notifyItemRangeChanged(
            0,
            chipsAdapter.currentList.lastIndex
        )
        if (mBinding.rvChips.isComputingLayout) {
            LogUtils.eTag(Fast3MainFragment.TAG, "isComputingLayout")
            mBinding.rvChips.post(action)
        } else {
            action.invoke()
        }
    }

    /**
     * 取消下注筹码判断是否需要选中用户最近一次手选筹码
     */
    private fun backUserLastSelectBette(betteBean: SelectAnnotationBean?, money: Long): Int {
        val index = -1
        if (betteBean == mViewModel.currentChip || betteBean == null) return index
        if (mViewModel.currentChip.money > money) return index
        mViewModel.setSelectedChip(betteBean.money)
        return mViewModel.currentChipIndex
    }

    private fun safeBetteFly(position: Int, action: (View?) -> Unit) {
        if (!isBetteItemVisible(position)) {
            mBinding.rvChips.post {
                val betteView =
                    mBinding.rvChips.layoutManager?.findViewByPosition(position)
                action.invoke(betteView)
            }
        } else {
            val betteView = mBinding.rvChips.layoutManager?.findViewByPosition(position)
            action.invoke(betteView)
        }
    }

    private fun scrollSelectPosition2Center(
        isSmooth: Boolean = true,
        action: (() -> Unit)? = null
    ) {
        mBinding.apply {
            val selectedIndex = mViewModel.chipsList.indexOfFirst { it.select }
            if (!isBetteItemVisible(selectedIndex)) {
                rvChips.scrollToPosition(selectedIndex)
            }
            rvChips.post {
                rvChips.layoutManager?.findViewByPosition(selectedIndex)?.let { chipView ->
                    val chipsLocation = chipView.locationOnScreen
                    val targetX: Int = chipView.getRootView().measuredWidth / 2
                    val chipsX: Int = chipsLocation[0] + (chipView.width / 2)
                    if (chipsX != targetX) {
                        if (chipsX > targetX && !rvChips.canScrollHorizontally(1)) {
                            action?.invoke()
                            return@post
                        }
                        if (chipsX < targetX && !rvChips.canScrollHorizontally(-1)) {
                            action?.invoke()
                            return@post
                        }
                        if (isSmooth) {
                            (rvChips.layoutManager as CenterLayoutManager).smoothScrollToPosition(
                                rvChips,
                                null,
                                selectedIndex
                            )
                        } else {
                            rvChips.scrollBy(chipsX - targetX - 1.dp2px, 0)

                        }
                    }
                    action?.invoke()
                }
            }
        }
    }

    private fun isBetteItemVisible(position: Int): Boolean {
        mBinding.apply {
            val layoutManager = rvChips.layoutManager as LinearLayoutManager
            return position in layoutManager.findFirstCompletelyVisibleItemPosition()..layoutManager.findLastCompletelyVisibleItemPosition()
        }
    }

    override fun onDestroy() {
        mViewModel.reset()
        super.onDestroy()
    }

    override fun onRefreshChips() {
        val money = gameAboutModel.tempBalance.value ?: 0
        var scrollIndex = -1
        mBinding.apply {
            val selectBean = mViewModel.chipsList.firstOrNull { it.select }
            if (selectBean != null) {
                if (selectBean.money > money) { //当前筹码不足
                    for (i in mViewModel.chipsList.lastIndex downTo 0) {
                        mViewModel.chipsList[i].select = false
                        if (scrollIndex == -1) {
                            if (mViewModel.chipsList[i].money <= money) {
                                scrollIndex = i
                                mViewModel.chipsList[i].select = true
                            }
                        }
                    }
                } else {
                    scrollIndex = backUserLastSelectBette(selectBean, money)
                }
            } else {
                if (mViewModel.currentChip.money <= money) {
                    scrollIndex = backUserLastSelectBette(null, money)
                } else {
                    if (mViewModel.chipsList.first().money <= money) {
                        mViewModel.reset()
                        scrollIndex = 0
                    }
                }
            }

            notifyDataSetChangedSafe {
                scrollSelectPosition2Center(true)
            }
        }
    }

    override fun onBetAreaClick(onClickChip: (chipView: View) -> Unit) {
        val selectedPosition = mViewModel.currentChipIndex
        scrollSelectPosition2Center(false) {
            onRefreshChips()
            safeBetteFly(selectedPosition) { chipView ->
                Log.d("test", "++++ $chipView")
                chipView?.let {
                    onClickChip.invoke(it)
                }
            }
        }
    }

}

interface ChipsViewImp {
    fun onRefreshChips()
    fun onBetAreaClick(onClickChip: (chipView: View) -> Unit)
}