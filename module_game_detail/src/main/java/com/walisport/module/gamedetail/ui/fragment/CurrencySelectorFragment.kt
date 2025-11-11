package com.walisport.module.gamedetail.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.FadeAnimation
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.gamedetail.R
import com.walisport.module.gamedetail.data.model.CurrencyInfoBean
import com.walisport.module.gamedetail.databinding.FragmentCurrencySelectorBinding
import com.walisport.module.gamedetail.ui.adapter.CurrencySelectorAdapter
import com.walisport.module.gamedetail.ui.viewmodel.CurrencySelectorViewModel
import kotlin.reflect.KClass

class CurrencySelectorFragment: BaseFragment<CurrencySelectorViewModel, FragmentCurrencySelectorBinding>() {
    override val vbClass: KClass<FragmentCurrencySelectorBinding> = FragmentCurrencySelectorBinding::class
    override val vmClass: KClass<CurrencySelectorViewModel> = CurrencySelectorViewModel::class

    private val currencyAdapter by lazy {
        CurrencySelectorAdapter()
    }

    private var marginBottom: Int = 0
    private var touchThroughViews: List<View> = emptyList()
    private var currencyDatas: List<CurrencyInfoBean> = emptyList()
    private var selectedCurrencyId: Int = -1
    private var onCurrencySelectedListener: ((currencyId: Int) -> Unit)? = null
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
            adapter = currencyAdapter.apply {
                setSelected(selectedCurrencyId)
                setOnCurrencySelectListener(onCurrencySelectedListener)
            }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            currencyAdapter.submitList(
                mutableListOf<CurrencyInfoBean?>().apply {
                    add(CurrencyInfoBean(-1, true, 1.0, null, R.string.current_currency.getString()))
                    addAll(currencyDatas)
                }
            )
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
            FadeAnimation.fadeOut(view){
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this@CurrencySelectorFragment)
                    .commitAllowingStateLoss()
            }
        }
    }

    fun show(manager: FragmentManager, containerId: Int) {
        val lastFragment = manager.findFragmentByTag(CurrencySelectorFragment.TAG)
        if (lastFragment == null || !lastFragment.isAdded) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, this, CurrencySelectorFragment.TAG)
                .commit()
        }
    }

    fun close() {
        dismiss()
    }

    class Builder {
        private var marginBottom: Int = 0
        private var touchThroughViews: List<View> = emptyList()
        private var currencyDatas: List<CurrencyInfoBean> = emptyList()
        private var selectedCurrencyId: Int = -1
        private var onCurrencySelectedListener: ((currencyId: Int) -> Unit)? = null
        private var onDismissListener: (() -> Unit)? = null

        fun setMarginBottom(value: Int) {
            marginBottom = value
        }

        fun setTouchThroughViews(views: List<View>) {
            touchThroughViews = views
        }

        fun setCurrencyDatas(value: List<CurrencyInfoBean>) {
            currencyDatas = value
        }

        fun setSelectedCurrencyId(value: Int) {
            selectedCurrencyId = value
        }

        fun setOnCurrencySelectedListener(listener: (currencyId: Int) -> Unit) {
            onCurrencySelectedListener = listener
        }

        fun setOnDismissListener(listener: (() -> Unit)?) {
            onDismissListener = listener
        }

        fun build(): CurrencySelectorFragment {
            return CurrencySelectorFragment().apply {
                this.marginBottom = this@Builder.marginBottom
                this.touchThroughViews = this@Builder.touchThroughViews
                this.currencyDatas = this@Builder.currencyDatas
                this.selectedCurrencyId = this@Builder.selectedCurrencyId
                this.onDismissListener = this@Builder.onDismissListener
            }
        }
    }

    companion object {
        const val TAG = "CurrencySelectorFragment"
    }
}