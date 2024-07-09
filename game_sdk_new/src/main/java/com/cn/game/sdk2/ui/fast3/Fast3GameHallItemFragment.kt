package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3GameHallItemViewModel
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.xcjh.base_lib.base.fragment.BaseVmVbFragment
import com.xcjh.base_lib.utils.dp2px

class Fast3GameHallItemFragment(private val category: String) : BaseVmVbFragment<Fast3GameHallItemViewModel, ItemGamehallPageBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.rvContent.itemAnimator = null
        mViewBind.rvContent.dividerSpace(
            requireContext().dp2px(20),
            DividerOrientation.HORIZONTAL
        ).setup {
            it.layoutManager = GridLayoutManager(context,4)
            addType<GameHallItem>(R.layout.item_gamehall_page_item)
            onBind {
                when (itemViewType) {
                    R.layout.item_gamehall_page_item -> {
                        getBinding<ItemGamehallPageItemBinding>().apply {
                            val bean = _data as GameHallItem
                            tvName.text = bean.name
                            tvOnline.text = bean.onlineA
                        }
                    }
                }
            }
        }.models = mViewModel.hallItems.value


    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        mViewModel.hallItems.observe(viewLifecycleOwner) {
            mViewBind.rvContent.bindingAdapter.models = it
        }
    }


}