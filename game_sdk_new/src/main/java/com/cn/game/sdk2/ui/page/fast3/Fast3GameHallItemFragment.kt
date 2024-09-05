package com.cn.game.sdk2.ui.page.fast3

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.adapter.GameHallItemAdapter
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3GameHallItemViewModel
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class Fast3GameHallItemFragment :
    BaseFragment<Fast3GameHallItemViewModel, ItemGamehallPageBinding>() {

    override val mBinding: ItemGamehallPageBinding by viewBind()

    override val mViewModel: Fast3GameHallItemViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvContent.itemAnimator = null
        mBinding.rvContent.layoutManager = GridLayoutManager(requireContext(), 4)
        mBinding.rvContent.dividerSpace(
            requireContext().dp2px(20),
            DividerOrientation.HORIZONTAL
        )
        val adapter = GameHallItemAdapter().also {
            it.submitList(mViewModel.hallItems.value)
        }
        mBinding.rvContent.adapter = adapter
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        mViewModel.hallItems.observe(viewLifecycleOwner) {
            mBinding.rvContent.bindingAdapter.models = it
        }
    }


}