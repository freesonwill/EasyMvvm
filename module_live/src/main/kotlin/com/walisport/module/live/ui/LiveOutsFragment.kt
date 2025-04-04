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
import com.walisport.module.live.databinding.FragmentLiveLineupBinding
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.viewmodel.LiveLineupViewModel
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class LiveOutsFragment : BaseFragment<LiveOutsViewModel,FragmentLiveOutsBinding>() {
    override val vbClass: KClass<FragmentLiveOutsBinding> = FragmentLiveOutsBinding::class
    override val vmClass: KClass<LiveOutsViewModel> = LiveOutsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}