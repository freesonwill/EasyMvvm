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
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel,FragmentLiveChatBinding>() {
    override val mBinding: FragmentLiveChatBinding by viewBind()
    override val mViewModel: LiveChatViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}