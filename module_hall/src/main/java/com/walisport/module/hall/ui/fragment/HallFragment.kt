package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.databinding.FragmentHallBinding
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.reflect.KClass

/**
 * 游戏大厅界面
 */

class HallFragment : BaseFragment<HallViewModel, FragmentHallBinding>() {

    override val vbClass: KClass<FragmentHallBinding> = FragmentHallBinding::class
    override val vmClass: KClass<HallViewModel> = HallViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.root.touchBackPressed()

        mBinding.balanceView.init(childFragmentManager)

        var barHeight = ViewUtils.getStatusBarHeight(requireContext())
        val params = mBinding.guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guideBegin = barHeight
        mBinding.guideline.layoutParams = params

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
//        mBinding.ivLogo.post {
//            var barHeight = ViewUtils.getStatusBarHeight(requireContext())
//            var toBarHeight = mBinding.ivLogo.height
//
//            val paramsLin = mBinding.homeBarIcon.layoutParams as LayoutParams
//            paramsLin.height = barHeight+toBarHeight
//            mBinding.homeBarIcon.layoutParams = paramsLin
//        }
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoPadding = false, autoIsNavigation = true)
        setStatusBar(StatusBarConfig, mBinding.clMain)
        super.onStart()
    }



}