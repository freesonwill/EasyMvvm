package com.xcjh.app.ui.details.fragment.index

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.databinding.FragmentDetailTabIndexTab3Binding
import com.xcjh.app.ui.details.DetailVm

/**
 * 其他直播间列表
 * matchType 1足球，2篮球
 */
class Index3Fragment() : BaseFragment<DetailVm, FragmentDetailTabIndexTab3Binding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override fun initView(savedInstanceState: Bundle?) {


    }

    override fun createObserver() {
        vm.odds.observe(this) {
            if (it != null) {
                mDatabind.viewJQTable.setData(it.bsInfo)//进球数
            }
        }
    }
}