package com.walisport.module.topup.ui.fragment

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.topup.R
import com.walisport.module.topup.data.Config
import com.walisport.module.topup.data.DateFilterEnum
import com.walisport.module.topup.data.WithdrawShowTypeEnum
import com.walisport.module.topup.databinding.FragmentWithdrawRecordsBinding
import com.walisport.module.topup.ui.viewmodel.WithdrawRecordsViewModel
import kotlin.reflect.KClass

/**
 * 提现记录列表页
 */

class WithdrawRecordsFragment : BaseFragment<WithdrawRecordsViewModel, FragmentWithdrawRecordsBinding>() {

    override val vbClass: KClass<FragmentWithdrawRecordsBinding> = FragmentWithdrawRecordsBinding::class
    override val vmClass: KClass<WithdrawRecordsViewModel> = WithdrawRecordsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.withdrawal_record.getString(), {
                findNavController().navigateUp()
            })
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        mViewModel.onDateFilter.observe(viewLifecycleOwner) {
            mBinding.tvDateFilter.text = it.title
        }
        mViewModel.onWithdrawFilter.observe(viewLifecycleOwner) { list ->
            val selectedNames = list.map { it.txName }
            val displayText = when (selectedNames.size) {
                1 -> selectedNames.first()
                else -> selectedNames.joinToString("/")
            }
            mBinding.tvSportFilter.text = displayText
        }
        mViewModel.onShowTypeListener.observeEvent(viewLifecycleOwner, this) {
            when (it) {
                WithdrawShowTypeEnum.DATE -> showDateFilter()
                WithdrawShowTypeEnum.WITHDRAW -> showSportFilter()
                WithdrawShowTypeEnum.NONE -> {
                    hideSportFilter()
                }
            }
            mBinding.clSportFilter.visibility = if (it == WithdrawShowTypeEnum.WITHDRAW || it == WithdrawShowTypeEnum.NONE) View.VISIBLE else View.INVISIBLE
            mBinding.clDateFilter.visibility = if (it == WithdrawShowTypeEnum.DATE || it == WithdrawShowTypeEnum.NONE) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun showDateFilter() {
        mViewModel.onDateFilter.value?.let {
            setFilterText(mBinding.tvDateFilter, mBinding.ivDateFilter, true)
            childFragmentManager.setFragmentResultListener(
                Config.KEY_RESULT,
                viewLifecycleOwner
            ) { _, bundle ->
                mViewModel.setShowType(WithdrawShowTypeEnum.NONE)
                if (bundle.containsKey(Config.VALUE_SELECTED_DATE)) {
                    bundle.getString(Config.VALUE_SELECTED_DATE)?.let { result ->
                        val date = DateFilterEnum.valueOf(result)
                        if (date == DateFilterEnum.CUSTOM) {
                            val time = bundle.getLong(Config.VALUE_SELECTED_MILLISECOND)
                            mViewModel.setDateFilter(time)
                            //betSlipFilterViewModel.setDateTime(
                            //    startTime = null,
                            //    endTime = time
                            //)
                        } else {
                            mViewModel.setDateFilter(date)
                            //betSlipFilterViewModel.setDateTime(
                            //    startTime = date.startTime(),
                            //    endTime = date.endTime()
                            //)
                        }
                    }
                }
                setFilterText(mBinding.tvDateFilter, mBinding.ivDateFilter, false)
            }
            val time =
                if (it.date == DateFilterEnum.CUSTOM && mViewModel.customTime != null) {
                    mViewModel.customTime
                } else {
                    null
                }
            //DatePickerFragment.find(this, it.date, time).customShow()
        }
    }

    private fun showSportFilter() {

    }

    private fun hideSportFilter() {

    }

    private fun setFilterText(tv: TextView, iv: ImageView, isSelected: Boolean) {
        if (tv.isSelected == isSelected) return
        tv.isSelected = isSelected
        childFragmentManager.clearFragmentResult(Config.KEY_RESULT)
        val animatorSet = AnimatorSet()

        val startColor = tv.currentTextColor
        val endColor = if (isSelected) {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.green_for_white_bg)
        } else {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.main_text)
        }
        val textColorAnimator = ObjectAnimator.ofObject(
            tv,
            "textColor",
            ArgbEvaluator(),
            startColor,
            endColor
        )
        val currentRotation = iv.rotation
        // 假設 0 度是朝下，180 度是朝上
        val targetRotation = if (isSelected) 180f else 0f

        val rotationAnimator = ObjectAnimator.ofFloat(iv, "rotation", currentRotation, targetRotation)

        val arrowStartColor = if (isSelected) {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.secondary_text)
        } else {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.green_for_white_bg)
        }
        val arrowEndColor = if (isSelected) {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.green_for_white_bg)
        } else {
            SkinnableResourceManager.getColor(requireContext(), arch.cayenne.lib.common.R.color.secondary_text)
        }

        // --- c. ImageView 箭頭顏色漸變動畫 ---
        val arrowColorAnimator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            arrowStartColor,
            arrowEndColor
        )
        arrowColorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            iv.drawable?.let { drawable ->
                // 確保 drawable 是可變的，這樣 tint 不會影響其他地方使用此 drawable 的 View
                val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
                DrawableCompat.setTint(wrappedDrawable, animatedColor)
                iv.setImageDrawable(wrappedDrawable) // 更新 ImageView
            }
        }
        animatorSet.playTogether(textColorAnimator, rotationAnimator, arrowColorAnimator)
        animatorSet.duration = 100
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.start()
    }
}