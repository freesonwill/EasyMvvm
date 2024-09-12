package com.cn.game.sdk2.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk2.databinding.FragmentChipsBinding
import com.cn.game.sdk2.ui.adapter.ChipsAdapter
import com.cn.game.sdk2.ui.view.CenterLayoutManager
import com.cn.game.sdk2.ui.view.CommonLinearLayoutItemDecoration
import com.cn.game.sdk2.ui.viewmodel.ChipsViewModel
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/***
 * 主頁底部籌碼模塊
 */
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
        mViewModel.chipsList.observe(viewLifecycleOwner) {
            chipsAdapter.submitList(it)
            notifyDataSetChangedSafe {
                scrollSelectPosition2Center(true)
            }
        }
    }

    private fun setChipsView() {
        mBinding.rvChips.apply {
            itemAnimator = null
            layoutManager = CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                }
            }
            OverScrollDecoratorHelper.setUpOverScroll(this,OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
        }
    }

    private fun notifyDataSetChangedSafe(action: () -> Unit) {
        chipsAdapter.notifyItemRangeChanged(0, chipsAdapter.itemCount)
        if (mBinding.rvChips.isComputingLayout) {
            mBinding.rvChips.post(action)
        } else {
            action.invoke()
        }
    }

    private fun safeChipFly(position: Int, action: (View?) -> Unit) {
        if (!isChipItemVisible(position)) {
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
            val selectedIndex = mViewModel.currentChipIndex
            if (!isChipItemVisible(selectedIndex)) {
                rvChips.scrollToPosition(selectedIndex)
            }
            rvChips.post {
                rvChips.layoutManager?.findViewByPosition(selectedIndex)?.let { chipView ->
                    val chipsLocation = chipView.locationOnScreen
                    val targetX: Int = chipView.rootView.measuredWidth / 2
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

    private fun isChipItemVisible(position: Int): Boolean {
        val layoutManager = mBinding.rvChips.layoutManager as LinearLayoutManager
        return position in layoutManager.findFirstCompletelyVisibleItemPosition()..layoutManager.findLastCompletelyVisibleItemPosition()
    }

    override fun onDestroy() {
        mViewModel.reset()
        super.onDestroy()
    }

    override fun onRefreshChips() {
        mViewModel.refresh()
    }

    override fun onBetAreaClick(onClickChip: (chipView: View) -> Unit) {
        val selectedPosition = mViewModel.currentChipIndex
        scrollSelectPosition2Center(false) {
            onRefreshChips()
            safeChipFly(selectedPosition) { chipView ->
                chipView?.let {
                    onClickChip.invoke(it)
                }
            }
        }
    }

}

/***
 * 與外部通信接口
 */
interface ChipsViewImp {
    fun onRefreshChips()

    /***
     * 點擊注區須回調當前籌碼view，用於籌碼飛行動畫
     */
    fun onBetAreaClick(onClickChip: (chipView: View) -> Unit)
}