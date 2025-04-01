package com.walisport.module_setting.fragment

import android.os.Bundle
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module_setting.data.NoticeViewModel
import com.walisport.module_setting.databinding.FragmentNoticeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 通知设置
 */

class NoticeFragment : BaseFragment<NoticeViewModel, FragmentNoticeBinding>() {

    override val mBinding: FragmentNoticeBinding by viewBind()
    override val mViewModel: NoticeViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {
    }
}