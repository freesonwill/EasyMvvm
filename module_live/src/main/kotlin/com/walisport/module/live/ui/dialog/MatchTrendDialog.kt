package com.walisport.module.live.ui.dialog

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.bumptech.glide.Glide
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.databinding.DialogMatchTrendBinding
import kotlin.reflect.KClass

class MatchTrendDialog : BaseBottomSheetFragment<EmptyViewModel, DialogMatchTrendBinding>() {

    override val vbClass: KClass<DialogMatchTrendBinding>
        get() = DialogMatchTrendBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class

    val homeName = "homeName"
    val homeLogo = "homeLogo"
    val awayName = "awayName"
    val awayLogo = "awayLogo"
    val matchTrend = "matchTrend"
    private lateinit var data: MatchTrendData


    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            val homeStr = it.getString(homeName) ?: ""
            mBinding.tvDialogHome.text = homeStr
            val awayStr = it.getString(awayName) ?: ""
            mBinding.tvDialogAway.text = awayStr
            val logoHome = it.getString(homeLogo) ?: ""
            Glide.with(requireContext()).load(logoHome).into(mBinding.ivHomeLogo)
            val logoAway = it.getString(awayLogo) ?: ""
            Glide.with(requireContext()).load(logoAway).into(mBinding.ivAwayLogo)
            data = it.getSerializable(matchTrend) as MatchTrendData
        }
        mBinding.viewGoalTrend.setData(data)
        mBinding.btnKnow.setOnClickListener {
            dismiss()
        }
    }

    override fun initListener() {
    }
}