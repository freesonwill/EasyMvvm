package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderDateCustomBinding
import arch.cayenne.module.order.ui.viewmodel.OrderDateCustomViewModel
import kotlin.reflect.KClass
import androidx.core.view.isVisible
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class OrderDateCustomFragment: BaseFragment<OrderDateCustomViewModel, FragmentOrderDateCustomBinding>(), OrderDataPage {

    override val vbClass: KClass<FragmentOrderDateCustomBinding> = FragmentOrderDateCustomBinding::class
    override val vmClass: KClass<OrderDateCustomViewModel> = OrderDateCustomViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.tvYesterday.setOnClickListener {
            setSelected(it)
            hideWheelView()
        }
        mBinding.tvLastWeek.setOnClickListener {
            setSelected(it)
            hideWheelView()
        }
        mBinding.tvLastMonth.setOnClickListener {
            setSelected(it)
            hideWheelView()
        }
        mBinding.tvStartTime.setOnClickListener {
            setSelected(it)
            showWheelView()
        }
        mBinding.tvEndTime.setOnClickListener {
            setSelected(it)
            showWheelView()
        }
    }
    
    private fun showWheelView() {
        if (mBinding.layoutWheel.root.isVisible) return
        
        // 先測量視圖的目標高度
        mBinding.layoutWheel.root.measure(
            View.MeasureSpec.makeMeasureSpec(mBinding.root.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = mBinding.layoutWheel.root.measuredHeight
        
        // 設置初始高度為 0
        mBinding.layoutWheel.root.layoutParams.height = 31.dp2px
        mBinding.layoutWheel.root.visibility = View.VISIBLE
        
        // 執行高度展開動畫
        val animator = android.animation.ValueAnimator.ofInt(31.dp2px, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = mBinding.layoutWheel.root.layoutParams
            layoutParams.height = value
            mBinding.layoutWheel.root.layoutParams = layoutParams

            // 通知父 Dialog 更新高度
            findUpdateCustomViewInterface(parentFragment)
        }
        animator.duration = 300
        animator.start()
    }

    private fun findUpdateCustomViewInterface(parentFragment: Fragment?) {
        if (parentFragment !is UpdateCustomViewInterface) {
            findUpdateCustomViewInterface(parentFragment?.parentFragment)
        } else {
            parentFragment.updateViewPagerHeight()
        }
    }
    
    private fun hideWheelView() {
        if (mBinding.layoutWheel.root.isGone) return
        
        val currentHeight = mBinding.layoutWheel.root.height
        
        // 執行高度收起動畫
        val animator = android.animation.ValueAnimator.ofInt(currentHeight, 31.dp2px)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = mBinding.layoutWheel.root.layoutParams
            layoutParams.height = value
            mBinding.layoutWheel.root.layoutParams = layoutParams

            // 通知父 Dialog 更新高度
            findUpdateCustomViewInterface(parentFragment)
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                mBinding.layoutWheel.root.visibility = View.GONE
            }
        })
        animator.duration = 300
        animator.start()
    }

    override suspend fun createObserver() {
        mViewModel.onStartTimeListener.observe(viewLifecycleOwner) { date ->
            mBinding.tvStartTime.text = date
        }
        
        mViewModel.onEndTimeListener.observe(viewLifecycleOwner) { date ->
            mBinding.tvEndTime.text = date
        }
    }

    override fun getResult(): LongArray {
        return if (mBinding.tvYesterday.isSelected) {
            mViewModel.getYesterdayTimeRange()
        } else if (mBinding.tvLastWeek.isSelected) {
            mViewModel.getLastWeekTimeRange()
        } else if (mBinding.tvLastMonth.isSelected) {
            mViewModel.getLastMonthTimeRange()
        } else {
            mViewModel.getCustomTimeRange()
        }
    }

    private fun setSelected(v: View) {
        mBinding.tvYesterday.isSelected = v == mBinding.tvYesterday
        mBinding.tvLastWeek.isSelected = v == mBinding.tvLastWeek
        mBinding.tvLastMonth.isSelected = v == mBinding.tvLastMonth
        mBinding.tvStartTime.isSelected = v == mBinding.tvStartTime
        mBinding.tvEndTime.isSelected = v == mBinding.tvEndTime
    }
}

interface UpdateCustomViewInterface {
    fun updateViewPagerHeight()
}