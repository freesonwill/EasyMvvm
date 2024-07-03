package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.databinding.ItemGamehallPageItemBinding
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3GameHallItemViewModel
import com.cn.game.sdk2.websocket.gameAboutModel
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.engine.databinding.bind
import com.xcjh.base_lib.base.fragment.BaseVmVbFragment
import com.xcjh.base_lib.utils.dp2px

class Fast3GameHallItemFragment(category: String) :
    BaseVmVbFragment<Fast3GameHallItemViewModel, ItemGamehallPageBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        val list = mutableListOf<GameHallItem>()
        for (i in 0..10) {
            list.add(GameHallItem("a", "a", "b"))
        }
        mViewBind.rvContent.dividerSpace(
            requireContext().dp2px(20),
            DividerOrientation.VERTICAL
        ).setup {
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
        }.models = list

    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        mViewModel.hallItems.observe(viewLifecycleOwner) {
            mViewBind.rvContent.bindingAdapter.models = it
        }
    }


}