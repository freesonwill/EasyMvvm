package com.walisport.module.live.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.databinding.FragmentLiveStandingsBinding
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

//积分榜
class LiveStandingsFragment : BaseFragment<LiveStandingsViewModel,FragmentLiveStandingsBinding>() {
    override val vbClass: KClass<FragmentLiveStandingsBinding> = FragmentLiveStandingsBinding::class
    override val vmClass: KClass<LiveStandingsViewModel> = LiveStandingsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}