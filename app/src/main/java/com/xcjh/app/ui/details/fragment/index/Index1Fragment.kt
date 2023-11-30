package com.xcjh.app.ui.details.fragment.index

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.NoticeBean
import com.xcjh.app.bean.OddsDetailBean
import com.xcjh.app.databinding.FragmentDetailTabIndexTab1Binding
import com.xcjh.app.ui.details.DetailVm

/**
 * 其他直播间列表
 * matchType 1足球，2篮球
 */
class Index1Fragment() : BaseFragment<DetailVm, FragmentDetailTabIndexTab1Binding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createObserver() {
        vm.odds.observe(this) {
            if (it != null) {
                mDatabind.viewSFTable.setData(it.euInfo)
            }
        }
    }
}