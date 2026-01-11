package com.walisport.module.hall.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import com.walisport.module.business.common.ui.fragment.BaseBannerLinkFragment
import com.walisport.module.business.common.ui.viewmodel.BaseBannerViewModel
import com.walisport.module.hall.data.GameAllContentData
import com.walisport.module.hall.databinding.FragmentGameAllBinding
import com.walisport.module.hall.ui.adapter.GameAllHeaderAdapter
import com.walisport.module.hall.ui.adapter.GameAllHeaderViewHolder
import com.walisport.module.hall.ui.adapter.GameAllListAdapter
import com.walisport.module.hall.ui.adapter.GameAllListViewHolder
import com.walisport.module.hall.ui.adapter.GameAllRankingAdapter
import com.walisport.module.hall.ui.viewmodel.GameAllViewModel
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import com.walisport.module.live.data.EventClick
import kotlin.reflect.KClass

class GameAllFragment : BaseBannerLinkFragment<GameAllViewModel, FragmentGameAllBinding>() {
    companion object {
        fun newInstance() = GameAllFragment()
    }
    private var index: Int = 0

    private val headerAdapter by lazy {
        GameAllHeaderAdapter(viewLifecycleOwner.lifecycleScope,childFragmentManager,
            object : GameAllHeaderViewHolder.OnHeaderItemClickListener {
                override suspend fun getBannerList(): List<BannerImageMatchAdapter.ImageData> {
                    return mViewModel.getBannerList()
                }

                override suspend fun getInviteFriend(): List<BannerImageMatchAdapter.ImageData> {
                    return mViewModel.getInviteFriend()
                }
            })
    }

    private val listAdapter by lazy {
        GameAllListAdapter(object : GameAllListViewHolder.OnAllItemClickListener {
            override fun onItemClick(data: GameAllContentData) {
                navigate(Uri.parse("walisport://module_hall/hallCategoryFragment?category=${data.category}&name=${data.name}"))
            }

            override fun onChildItemClick(gameId:Long) {
                mViewModel.setIsClickGame(EventClick.EVENT_CLICK_ACK_TRUE.type)
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink("gameId" to gameId))
            }
        })
    }
    private val rankingAdapter by lazy {
        GameAllRankingAdapter(
            parentFragmentManager,
            childFragmentManager,
            lifecycle
        ){
            mBinding.nestedScrollView.requestLayout()
            //更新rvContent指定position
            // 更新 rvContent 指定 position
          //  mBinding.rvContent.adapter?.notifyItemChanged(headerAdapter.itemCount - 1)
        }
    }


    override val vbClass: KClass<FragmentGameAllBinding> = FragmentGameAllBinding::class
    override val vmClass: KClass<GameAllViewModel> = GameAllViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter = ConcatAdapter(
                headerAdapter,
                listAdapter,
                rankingAdapter
            )
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = concatAdapter
            rvContent.setItemViewCacheSize(10)
        }
    }

    override fun initData() {
        mViewModel.getAllList()
        super.initData()
    }

    override fun provideBannerViewModel(): BaseBannerViewModel {
        return sharedViewModel<HallViewModel, HallFragment>().value
    }

    override fun provideBannerNestedScrollView(): Pair<NestedScrollView, View> {
        return mBinding.nestedScrollView to mBinding.ivBackToTop
    }

    //循环请求数据
    override suspend fun createObserver() {
        //title列表
        mViewModel.gameRecentList.observe(viewLifecycleOwner) {
            it.let {
                mViewModel.queryGameList(it[index].category, it[index])
                index += 1
            }
        }

        //接收全部参数
        mViewModel.gameListLiveData.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
            //LogUtils.e("response------all--getGameTitleSize-${mViewModel.getGameTitleSize()},index-${index}")
            if (index < (mViewModel.getGameTitleSize())) {
                var gameData = mViewModel.getGame(index)
                gameData?.let {
                    mViewModel.queryGameList(
                        gameData.category,gameData)/**/
                }
            }
            index += 1
        }
    }

    override fun onStart() {
        super.onStart()
        // headerAdapter.restProBannerJob(mBinding.rvContent)
    }

    override fun onStop() {
        super.onStop()
        // headerAdapter.stopProBannerJob(mBinding.rvContent)
    }

}