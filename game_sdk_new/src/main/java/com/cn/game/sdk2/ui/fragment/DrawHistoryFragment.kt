package com.cn.game.sdk2.ui.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.animation.addListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.databinding.FragmentDrawHistoryBinding
import com.cn.game.sdk2.ui.adapter.DrawHistoryAdapter
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.view.ClickRecyclerView
import com.cn.game.sdk2.ui.viewmodel.DrawHistoryViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 主頁底下開獎紀錄模塊
 */
class DrawHistoryFragment: BaseFragment<DrawHistoryViewModel, FragmentDrawHistoryBinding>() {
    override val mBinding: FragmentDrawHistoryBinding by viewBind()
    override val mViewModel: DrawHistoryViewModel by viewModel()

    // View高度變動的Listener，回傳現在的高度
    private var onHeightListener: ((Int) -> Unit)? = null
    // 閃爍動畫
    private var animator: ObjectAnimator? = null
    // 展開、收合的動畫
    private var expandAnim: ValueAnimator? = null
    private val itemDecoration = object : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(0, 0, 2.dp2px, 0)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvDrawHistory.apply {
                itemAnimator = null
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                OverScrollDecoratorHelper.setUpOverScroll(this,OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL)
                adapter = DrawHistoryAdapter()
                addItemDecoration(itemDecoration)

                lifecycleScope.launchWhenResumed {
                    ((adapter as DrawHistoryAdapter).itemCount).let {
                        scrollToPosition(if (it > 0) it - 1 else 0)
                    }
                }

                setOnRecycleClickListener(
                    object : ClickRecyclerView.RecyclerClickListener {
                        override fun onRecyclerClick() {
                            PromptSoundPlay.btnPlayMedia()
                            playExpandAnim()
                        }
                    })
            }

            root.setOnClickListener {
                PromptSoundPlay.btnPlayMedia()
                playExpandAnim()
            }
        }

        measureHistoryRvHeight()
    }

    override fun lazyLoadData() {
        gameAboutModel.historyRounds.value?.let { setDrawHistories(it) }
    }

    override fun createObserver() {
        mViewModel.drawHistories.observe(viewLifecycleOwner) {
            (mBinding.rvDrawHistory.adapter as DrawHistoryAdapter).submitList(it)
        }

        /**
         * 播放完结算飞行动画在播放结果闪烁
         */
        FlowBus.with<Boolean>(EventKey.PLAY_DRAW_HISTORY_ANIM).register(viewLifecycleOwner){
            lifecycleScope.launch {
                val historyRounds = gameAboutModel.historyRounds.value!!
                Log.e(Fast3MainFragment.TAG, "开奖历史结果--->$it")
                setDrawHistories(historyRounds)
                delay(50) //延迟等待recycleView刷新
                while (mBinding.rvDrawHistory.isComputingLayout) delay(1)
                playAnim(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
        expandAnim?.cancel()
    }

    // 計算展開動畫相關高度
    private fun measureHistoryRvHeight() {
        with(mBinding) {
            with(mViewModel) {
                resultRvHeight = rvDrawHistory.height.takeIf { it != 0 } ?: 122.dp2px
                resultAnimMoveHeight = resultRvHeight - mViewModel.currentHeight
            }
        }
    }

    // 執行展開、收合動畫
    private fun playExpandAnim() {
        with(mViewModel) {
            mBinding.apply {
                expandAnim?.cancel()
                isExpand = !isExpand
                ivHomeRotation.rotation = if (isExpand) 0f else 180f
                val startHeight = currentHeight
                val endHeight =
                    if (isExpand) resultRvHeight
                    else resultRvHeight - resultAnimMoveHeight

                expandAnim = (expandAnim ?: ValueAnimator.ofInt(startHeight, endHeight)
                    .apply {
                        duration = 150
                        addUpdateListener {
                            val height = it.animatedValue as Int
                            currentHeight = height
                            onHeightListener?.invoke(height)
                        }
                    }).apply {
                    setIntValues(startHeight, endHeight)
                    start()
                }
            }
        }
    }

    /**
     * 取得當前View的高度
     */
    fun getCurrentHeight(): Int {
        return mViewModel.currentHeight
    }

    /**
     * 設定View高度變動的Listener
     */
    fun setDrawHistoryHeightListener(listener: (Int) -> Unit) {
        this.onHeightListener = listener
    }

    /**
     * 設定開獎歷史資料
     *
     * @param histories 開獎歷史資料
     */
    fun setDrawHistories(histories: List<RoundInfoBean>) {
        mViewModel.setDrawHistories(histories)
    }

    /**
     * 播放閃爍動畫
     *
     * @param isPlay 是否播放
     * @param duration 持續時間，預設為500L
     */
    fun playAnim(isPlay: Boolean, duration: Long = 500L) {
        with(mBinding.rvDrawHistory) {
            scrollToPosition((adapter as DrawHistoryAdapter).currentList.size - 1)

            val position = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val view = (layoutManager as LinearLayoutManager).findViewByPosition(position) ?: return

            animator = if (isPlay) {
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
                    .apply {
                        this.duration = duration
                        repeatCount = 5
                        repeatMode = ObjectAnimator.REVERSE
                        addListener(
                            onCancel = { animator = null },
                            onEnd = { animator = null }
                        )
                    }.also {
                        it.start()
                    }
            } else {
                view.alpha = 1f
                null
            }
        }
    }

    companion object {
        const val TAG = "DrawHistoryFragment"
    }
}