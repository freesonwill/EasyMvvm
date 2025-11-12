package com.walisport.module.gamedetail.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.FadeAnimation
import com.walisport.module.gamedetail.data.model.PlayerRankingBean
import com.walisport.module.gamedetail.databinding.FragmentPlayerRankingBinding
import com.walisport.module.gamedetail.ui.adapter.PlayerRankingAdapter
import com.walisport.module.gamedetail.ui.viewmodel.PlayerRankingViewModel
import kotlin.reflect.KClass

class PlayerRankingFragment : BaseFragment<PlayerRankingViewModel, FragmentPlayerRankingBinding>() {
    override val vbClass: KClass<FragmentPlayerRankingBinding> = FragmentPlayerRankingBinding::class
    override val vmClass: KClass<PlayerRankingViewModel> = PlayerRankingViewModel::class


    private val rankingAdapter by lazy {
        PlayerRankingAdapter()
    }

    private var marginBottom: Int = 0
    private var touchThroughViews: List<View> = emptyList()
    private var rankingDatas: List<PlayerRankingBean> = emptyList()
    private var onDismissListener: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
        // 淡入动画
        FadeAnimation.fadeIn(view)
        with(mBinding.viewBg) {
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                bottomMargin = marginBottom
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerView.apply {
            adapter = rankingAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rankingDatas.takeIf { it.isNotEmpty() }?.let {
                rankingAdapter.submitList(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        mBinding.clRoot.apply {
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val rawX = event.rawX
                    val rawY = event.rawY

                    val isOutside = !isPointInsideView(mBinding.viewBg, rawX, rawY)
                    val canThrough = touchThroughViews.any { isPointInsideView(it, rawX, rawY) }

                    // 點擊在禪窗內部，不攔截
                    if (!isOutside) {
                        return@setOnTouchListener false
                    }

                    when {
                        // 點擊在禪窗外部，但點擊位置在可穿透 View 上，不攔截
                        canThrough -> return@setOnTouchListener false

                        // 點擊在禪窗外部，且點擊位置不在可穿透 View 上，關閉禪窗並攔截事件
                        else -> {
                            dismiss()
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
        }
    }

    override suspend fun createObserver() = Unit

    private fun isPointInsideView(view: View, rawX: Float, rawY: Float): Boolean {
        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)
        return viewRect.contains(rawX.toInt(), rawY.toInt())
    }

    private fun dismiss() {
        if (parentFragment != null || parentFragmentManager.fragments.contains(this)) {
            onDismissListener?.invoke()
            // 淡出动画
            FadeAnimation.fadeOut(view) {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this@PlayerRankingFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    fun show(manager: FragmentManager, containerId: Int) {
        val lastFragment = manager.findFragmentByTag(Companion.TAG)
        if (lastFragment == null || !lastFragment.isAdded) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, this, Companion.TAG)
                .commit()
        }
    }

    fun close() {
        dismiss()
    }

    class Builder {
        private var marginBottom: Int = 0
        private var touchThroughViews: List<View> = emptyList()
        private var rankingDatas: List<PlayerRankingBean> = emptyList()
        private var onDismissListener: (() -> Unit)? = null

        fun setMarginBottom(value: Int) {
            marginBottom = value
        }

        fun setTouchThroughViews(views: List<View>) {
            touchThroughViews = views
        }

        fun setRankingDatas(datas: List<PlayerRankingBean>) {
            rankingDatas = datas
        }


        fun setOnDismissListener(listener: (() -> Unit)?) {
            onDismissListener = listener
        }

        fun build(): PlayerRankingFragment {
            return PlayerRankingFragment().apply {
                this.marginBottom = this@Builder.marginBottom
                this.touchThroughViews = this@Builder.touchThroughViews
                this.rankingDatas = this@Builder.rankingDatas
                this.onDismissListener = this@Builder.onDismissListener
            }
        }
    }

    companion object {
        const val TAG = "PlayerRankingFragment"
    }
}