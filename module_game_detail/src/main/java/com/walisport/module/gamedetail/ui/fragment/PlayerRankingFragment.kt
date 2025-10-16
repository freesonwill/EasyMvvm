package com.walisport.module.gamedetail.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.gamedetail.data.model.PlayerRankingBean
import com.walisport.module.gamedetail.databinding.FragmentPlayerRankingBinding
import com.walisport.module.gamedetail.ui.adapter.PlayerRankingAdapter
import com.walisport.module.gamedetail.ui.viewmodel.PlayerRankingViewModel
import kotlin.reflect.KClass

class PlayerRankingFragment: BaseFragment<PlayerRankingViewModel, FragmentPlayerRankingBinding>() {
    override val vbClass: KClass<FragmentPlayerRankingBinding> = FragmentPlayerRankingBinding::class
    override val vmClass: KClass<PlayerRankingViewModel> = PlayerRankingViewModel::class

    enum class RankingType(val type: Int) { BIGGEST(1), LUCKIEST(2) }

    private val rankingAdapter by lazy {
        PlayerRankingAdapter()
    }

    private var marginBottom: Int = 0
    private var touchThroughViews: List<View> = emptyList()
    private var rankingDatas: List<PlayerRankingBean> = emptyList()
    private var type: RankingType = RankingType.BIGGEST

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding.viewBg) {
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                bottomMargin = marginBottom
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            clRoot.apply {
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val rawX = event.rawX
                        val rawY = event.rawY

                        val isOutside = !isPointInsideView(viewBg, rawX, rawY)
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

            recyclerView.apply {
                adapter = rankingAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rankingDatas.takeIf { it.isNotEmpty() }?.let {
                    rankingAdapter.submitList(it)
                }
            }
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    private fun isPointInsideView(view: View, rawX: Float, rawY: Float): Boolean {
        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)
        return viewRect.contains(rawX.toInt(), rawY.toInt())
    }

    private fun dismiss() {
        if (parentFragment != null) {
            parentFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .remove(this)
                .commitAllowingStateLoss()
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

    fun getType(): RankingType {
        return type
    }

    class Builder {
        private var marginBottom: Int = 0
        private var touchThroughViews: List<View> = emptyList()
        private var rankingDatas: List<PlayerRankingBean> = emptyList()
        private var fragTag: String = ""
        private var type: RankingType = RankingType.BIGGEST

        fun setMarginBottom(value: Int) {
            marginBottom = value
        }

        fun setTouchThroughViews(views: List<View>) {
            touchThroughViews = views
        }

        fun setRankingDatas(datas: List<PlayerRankingBean>) {
            rankingDatas = datas
        }

        fun setRankingType(rankingType: RankingType) {
            type = rankingType
        }

        fun build(): PlayerRankingFragment {
            return PlayerRankingFragment().apply {
                this.marginBottom = this@Builder.marginBottom
                this.touchThroughViews = this@Builder.touchThroughViews
                this.rankingDatas = this@Builder.rankingDatas
                this.type = this@Builder.type
            }
        }
    }

    companion object {
        const val TAG = "PlayerRankingFragment"
    }
}