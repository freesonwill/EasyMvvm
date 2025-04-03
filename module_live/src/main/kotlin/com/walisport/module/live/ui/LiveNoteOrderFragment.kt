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
import com.walisport.module.live.databinding.FragmentLiveNoteOrderBinding
import com.walisport.module.live.ui.viewmodel.LiveNoteOrderViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

//注单
class LiveNoteOrderFragment : BaseFragment<LiveNoteOrderViewModel,FragmentLiveNoteOrderBinding>() {
    override val mBinding: FragmentLiveNoteOrderBinding by viewBind()
    override val mViewModel: LiveNoteOrderViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}