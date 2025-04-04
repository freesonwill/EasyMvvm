package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.adapter.LeagueAdapter
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.databinding.FragmentLeagueBinding
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LeagueFragment : BaseFragment<LeagueViewModel, FragmentLeagueBinding>() {

    override val mBinding: FragmentLeagueBinding by viewBind()

    override val mViewModel: LeagueViewModel by viewModel()

    private val itemDecoration: ItemDecoration = object : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(8.dp2px, 8.dp2px, 12.dp2px, 0)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerLeague.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context)
            adapter = LeagueAdapter().apply {
                addItemDecoration(itemDecoration)
                val temp = LeagueMatchBean(0, 11001010L, "", "", "阿森纳", "曼城")
                val list = listOf(temp, temp, temp, temp)
                submitList(list)
            }
        }
    }

    override fun initListener() {
        mBinding.ivLeagueClose.clickNoRepeat{
            findNavController().navigateUp()
        }
    }

    override fun createObserver() {

    }
}