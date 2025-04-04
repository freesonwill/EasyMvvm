package com.walisport.module.live.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveBetOnBinding
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

//投注
class LiveBetOnFragment : BaseFragment<LiveBetOnViewModel,FragmentLiveBetOnBinding>() {
    override val vbClass: KClass<FragmentLiveBetOnBinding> = FragmentLiveBetOnBinding::class
    override val vmClass: KClass<LiveBetOnViewModel> = LiveBetOnViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}