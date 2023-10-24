package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.drake.brv.utils.*
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivityMyFollowListBinding
import com.xcjh.app.databinding.ActivityViewingHistoryListBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.utils.dp2px

/**
 * 观看历史记录
 */
class ViewingHistoryListActivity : BaseActivity<ViewingHistoryListVm, ActivityViewingHistoryListBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.viewing_txt_title)
        adapter()

        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getHistoryLive(true)

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getHistoryLive(false)
            }
        })

        mViewModel.getHistoryLive(true)

    }

    fun  adapter(){
        mDatabind.rcvRecommend.grid(2).setup {
            addType<BeingLiveBean>(R.layout.item_main_live_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_main_live_list -> {
                        var bindingItem=getBinding<ItemMainLiveListBinding>()
                        var  bean=_data as BeingLiveBean
                        bindingItem.txtLiveIsBroadcast.visibility=ViewGroup.VISIBLE
                        if(bean.liveStatus.equals("2")){
                            bindingItem.txtLiveIsBroadcast.background=(ContextCompat.getDrawable(this@ViewingHistoryListActivity,R.drawable.shape_r4_ff5151))
                            bindingItem.txtLiveIsBroadcast.text=resources.getString(R.string.main_txt_on_the_air)
                        }else{
                            bindingItem.txtLiveIsBroadcast.background=(ContextCompat.getDrawable(this@ViewingHistoryListActivity,R.drawable.shape_r4_8a91a0))
                            bindingItem.txtLiveIsBroadcast.text=resources.getString(R.string.live_txt_0ff_air)
                        }

                        Glide.with(context).asBitmap()
                            .load(bean.titlePage) // 替换为您要加载的图片 URL
                            .error(R.drawable.main_top_load)
                            .placeholder(R.drawable.main_top_load)
                            .into(bindingItem.ivLiveBe)
                        Glide.with(context).asBitmap()
                            .load(bean.userLogo) // 替换为您要加载的图片 URL
                            .error(R.drawable.load_round)
                            .placeholder(R.drawable.load_round)
                            .into(bindingItem.ivLiveHead)
                        bindingItem.txtLiveName.text=bean.nickName
                        bindingItem.txtLiveTeam.text="${bean.homeTeamName}VS${bean.awayTeamName}"
                        bindingItem.txtLiveCompetition.text=bean.competitionName
                        if(bean.hotValue<=9999){
                            bindingItem.txtLiveHeat.text="${bean.hotValue}"
                        }else{
                            bindingItem.txtLiveHeat.text="9999+"
                        }

                        if(layoutPosition%2==0){
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, context.dp2px(13), context.dp2px(20))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }else{
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, 0, context.dp2px(20))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }
                    }


                }

            }
            R.id.llLiveSpacing.onClick {
                var  bean=_data as BeingLiveBean
                if(bean.liveStatus.equals("2")){
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId )
                }else{
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}" )

                }

            }

        }
    }

    override fun createObserver() {
        super.createObserver()

        mViewModel.liveList.observe(this){
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                       if( mDatabind.rcvRecommend.models?.size!=null){
                           mDatabind.rcvRecommend.mutable.clear()
                       }
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.state.showEmpty()
                    }
                    //是第一页
                    it.isRefresh -> {
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.smartCommon.resetNoMoreData()
                        if(mDatabind.rcvRecommend.models!=null){
                            mDatabind.rcvRecommend.mutable.clear()
                        }
                        mDatabind.rcvRecommend.addModels(it.listData)
                        mDatabind.state.showContent()
                    }
                    //不是第一页
                    else -> {
                        if(it.listData.isEmpty()) {
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        }else{
                            mDatabind.smartCommon.finishLoadMore()

                        }
                        mDatabind.rcvRecommend.addModels(it.listData)
                        mDatabind.state.showContent()

                    }

                }

            }else{
                if(it.isRefresh){
                    mDatabind.smartCommon.finishRefresh()
                    mDatabind.smartCommon.resetNoMoreData()
                    if(mDatabind.rcvRecommend.models!=null){
                        mDatabind.rcvRecommend.mutable.clear()
                    }
                    mDatabind.state.showEmpty()
                }
            }

        }
    }

}