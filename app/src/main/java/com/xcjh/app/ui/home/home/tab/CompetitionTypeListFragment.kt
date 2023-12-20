package com.xcjh.app.ui.home.home.tab

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.drake.brv.utils.*
import com.drake.statelayout.StateConfig
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.HotReq
import com.xcjh.app.bean.MainTxtBean
import com.xcjh.app.databinding.FragmentCompetitionTypeListBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.view.NestedScrollView
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.listener.LiveStatusListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.myToast
import java.util.Collection

/**
 * 赛事列表足球或者篮球
 * type   比赛类型 1足球，2篮球
 */
class CompetitionTypeListFragment() : BaseFragment<CompetitionTypeListVm, FragmentCompetitionTypeListBinding>() {
        var type:Int=-1
    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt("type")
        }
        adapter()

        mViewModel.getNowLive(true, type = type.toString())

        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getNowLive(true,type = type.toString())

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getNowLive(false,type = type.toString())
            }
        })

        mDatabind.state.apply {
            onEmpty {
                var icon=this.findViewById<AppCompatImageView>(R.id.ivEmptyIcon)
                var txt=this.findViewById<AppCompatTextView>(R.id.txtEmptyName)
                icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.zwt_zwzb))
                txt.text=resources.getString(R.string.empty_txt_no_anchor)

            }

        }
        MyWsManager.getInstance(App.app)
            ?.setLiveStatusListener(this.toString(), object : LiveStatusListener {
                override fun onOpenLive(bean: LiveStatus) {

                    var isShow=false
                    if(mDatabind.rcvRecommend.models!=null){
                        for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                            if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId.equals(bean.anchorId)){
                                isShow=true
                            }

                        }
                    }
                    if(!isShow){
                        mViewModel.getOngoingMatchList(bean.id)
                    }
                }

                override fun onCloseLive(bean: LiveStatus) {
                    super.onCloseLive(bean)

                    if(mDatabind.rcvRecommend.models!=null){
                        for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                            if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId.equals(bean.anchorId)){
                                mDatabind.rcvRecommend.mutable.removeAt(i)
                                mDatabind.rcvRecommend.bindingAdapter.notifyItemRemoved(i) // 通知更新
                                mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                            }

                        }
                    }
                }
            })


    }


    fun  adapter(){
        mDatabind.rcvRecommend.grid(2).setup {
            addType<BeingLiveBean>(R.layout.item_main_live_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_main_live_list -> {
                        var bindingItem=getBinding<ItemMainLiveListBinding>()
                        var  bean=_data as BeingLiveBean
                        Glide.with(context)
                            .load(bean.titlePage) // 替换为您要加载的图片 URL
                            .error(R.drawable.main_top_load)
                            .placeholder(R.drawable.main_top_load)
                            .into(bindingItem.ivLiveBe)
                        Glide.with(context)
                            .load(bean.userLogo) // 替换为您要加载的图片 URL
                            .error(R.drawable.default_anchor_icon)
                            .placeholder(R.drawable.default_anchor_icon)
                            .into(bindingItem.ivLiveHead)
                        bindingItem.txtLiveName.text=bean.nickName
                        //比赛类型 1足球，2篮球,可用值:1,2
                        if(type==1){
                            bindingItem.txtLiveTeam.text="${bean.homeTeamName}VS${bean.awayTeamName}"
                        }else{
                            bindingItem.txtLiveTeam.text="${bean.awayTeamName }VS${bean.homeTeamName}"
                        }


                        bindingItem.txtLiveCompetition.text=bean.competitionName
                        if(bean.hotValue<=9999){
                            bindingItem.txtLiveHeat.text="${bean.hotValue}"
                        }else{
                            bindingItem.txtLiveHeat.text="9999+"
                        }

                        if(layoutPosition%2==0){
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, context.dp2px(4), context.dp2px(8))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }else{
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(context.dp2px(4), 0, 0, context.dp2px(8))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }
                    }


                }

            }
            R.id.llLiveSpacing.onClick {
                val bean=_data as BeingLiveBean
                MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId,videoUrl = bean.playUrl )
            }

        }
    }

    override fun createObserver() {
        super.createObserver()
        //打开直播的时候获取到详情
        mViewModel.beingLive.observe(this){
            var list =ArrayList<BeingLiveBean>()
            list.add(it)
            mDatabind.rcvRecommend.addModels(list)
            mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
        }
        //登录或者登出
        appViewModel.updateLoginEvent.observe(this){
            mViewModel.getNowLive(true,type = type.toString())
        }


        //正在直播的热门比赛
        mViewModel.liveList.observe(this){
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        if(mDatabind.rcvRecommend.models!=null){
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