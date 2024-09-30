package com.cn.game.sdk2.ui.page.fast3

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cn.game.sdk2.databinding.ItemGamehallPageBinding
import com.cn.game.sdk2.ui.adapter.GameHallItemAdapter
import com.cn.game.sdk2.ui.view.BounceRVEdgeEffectFactory
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3GameHallItemViewModel
import com.cn.game.sdk2.utils.GsonUtils
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.clearAllItemDecorations
import com.cn.game.sdk2.utils.ext.setOverScrollModeExt
import com.cn.game.sdk2.websocket.appListener
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.eetrust.lib_bounce_effect.setBounceEdgeEffect
import com.google.gson.Gson
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class Fast3GameHallItemFragment : BaseFragment<Fast3GameHallItemViewModel, ItemGamehallPageBinding>() {

    override val mBinding: ItemGamehallPageBinding by viewBind()
    override val mViewModel: Fast3GameHallItemViewModel by viewModel()
    private lateinit var adapter: GameHallItemAdapter

    override fun initView(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    override fun lazyLoadData() {
    }

    private fun setupRecyclerView() {
        adapter = GameHallItemAdapter().apply {
            onItemClickListener = { item ->
                //val dataStr = item.toString()
                val dataStr = GsonUtils.toJson(item)
                appListener?.onClickOtherGameWithBlock(dataStr)
            }
        }
        mBinding.rvContent.apply {
            itemAnimator = null
            layoutManager = GridLayoutManager(context, 4)
            clearAllItemDecorations()
            dividerSpace(25.dp2px, DividerOrientation.HORIZONTAL)
            adapter = this@Fast3GameHallItemFragment.adapter
           // edgeEffectFactory = BounceRVEdgeEffectFactory()
            // setBounceEdgeEffect(overScrollMagnitude=3f,flingMagnitude=3f)
            OverScrollDecoratorHelper.setUpOverScroll(this,OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        }
    }

    override fun initData() {
        mViewModel.setGameType(requireArguments().getInt("gameType"))
    }

    override fun createObserver() {
        mViewModel.hallItems.observe(viewLifecycleOwner) { gameList ->
            adapter.submitList(gameList)
            adapter.notifyItemRangeChanged(0,gameList.size)
        }
    }

    companion object {
        fun newInstance(gameType: Int) = Fast3GameHallItemFragment().apply {
            arguments = Bundle().apply {
                putInt("gameType", gameType)
            }
        }
    }
}