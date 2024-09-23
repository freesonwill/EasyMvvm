package com.cn.game.sdk2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentChipsBinding
import com.cn.game.sdk2.ui.adapter.ChipsAdapter
import com.cn.game.sdk2.ui.helper.ToastHelper
import com.cn.game.sdk2.ui.view.CenterLayoutManager
import com.cn.game.sdk2.ui.view.CommonLinearLayoutItemDecoration
import com.cn.game.sdk2.ui.viewmodel.ChipsViewModel
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.appListener
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
        //临时金额变化时需要刷新筹码的可用状态
        gameAboutModel.tempBalance.observe(viewLifecycleOwner) {
            Log.d("abcd", "tempBalance: $it")
            mViewModel.refresh()
        }
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
                if (!item.isSelected) {
                    if (item.chip.money <= (gameAboutModel.tempBalance.value ?: 0)) {
                        PromptSoundPlay.btnPlayMedia()
                        mViewModel.setUserSelectChip(item)
                    } else {
                        ToastHelper.instance.showWindowToast(
                            context = context,
                            msg = resources.getString(R.string.error_bet_money_insufficient),
                            context.resources.displayMetrics.heightPixels/2,
                        )
                        appListener?.onInsufficientBalance()
                    }
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

    private fun safeChipFly(action: (View?) -> Unit) {
        val position = mViewModel.currentChipIndex
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

    override fun cancelBet() {
        mViewModel.cancelBet()
    }

    override fun onBetAreaClick(onClickChip: (chipView: View) -> Unit) {
        scrollSelectPosition2Center(false) {
            safeChipFly { chipView ->
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
    /***
     * 取消下注的回調，用於回復使用者最後選擇的籌碼
     */
    fun cancelBet()

    /***
     * 點擊注區須回調當前籌碼view，用於籌碼飛行動畫
     */
    fun onBetAreaClick(onClickChip: (chipView: View) -> Unit)
}