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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.view.CustomHeader
import com.xcjh.app.view.NestedScrollView
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.listener.LiveStatusListener
import com.xcjh.app.websocket.listener.MOffListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.Constants
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
        mDatabind.smartCommon.setRefreshHeader( CustomHeader(requireContext()))
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
                //z直播间开播
                override fun onOpenLive(bean: LiveStatus) {

                    if(!bean.matchType.equals(type.toString())){
                        return
                    }

                    if(mDatabind.rcvRecommend.models!=null){
                        for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                            if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).matchId.equals(bean.matchId)&&
                                (mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).matchType.equals(bean.matchType)){
                                if( (mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId!=null){
                                    if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId.equals(bean.anchorId)){
                                        (mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId.equals(bean.anchorId)
                                    }

                                } else{
                                    mDatabind.rcvRecommend.mutable.removeAt(i)
                                }


                                break
                            }


                        }
                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                    }

                    var being=BeingLiveBean()
                    being.matchType=bean.matchType
                    being.matchId=bean.matchId
                    being.homeTeamName=bean.homeTeamName
                    being.awayTeamName=bean.awayTeamName
                    being.nickName=bean.nickName
                    being.userId=bean.anchorId
                    being.playUrl=bean.playUrl
                    being.hotValue=bean.hotValue
                    being.titlePage=bean.coverImg
                    being.userLogo=bean.userLogo
                    being.pureFlow=false
                    //语言 0是中文  1是繁体  2是英文
                    if(Constants.languageType==0){
                        being.homeTeamName=bean.homeTeamName
                        being.awayTeamName=bean.awayTeamName
                        being.competitionName=bean.competitionName
                    }else if(Constants.languageType==1){
                        being.homeTeamName=bean.homeTeamNameZht
                        being.awayTeamName=bean.awayTeamNameZht
                        being.competitionName=bean.competitionNameZht
                    }else{
                        being.homeTeamName=bean.homeTeamNameEn
                        being.awayTeamName=bean.awayTeamNameEn
                        being.competitionName=bean.competitionNameEn
                    }




                    var num=0   //保存这个应该插入哪个
                    var fuzhi=false

                    if(mDatabind.rcvRecommend.models!=null){
                        num=mDatabind.rcvRecommend.mutable.size-1
                        for (i in 0 until mDatabind.rcvRecommend.mutable!!.size) {
                                 if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).pureFlow){
                                        if(fuzhi==false){
                                            num=i
                                            break
                                        }

                                    }



                        }

                        var list:ArrayList<BeingLiveBean> = arrayListOf()
                        if(mDatabind.rcvRecommend.mutable.size==0){
                            list.add(being)
                            for (i in 0 until mDatabind.rcvRecommend.mutable.size) {
                                list.add((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean))
                            }

                        }else {
                            for (i in 0 until mDatabind.rcvRecommend.mutable.size) {
                                if(num==i){
                                    list.add(being)
                                    list.add((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean))
                                }else{
                                    list.add((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean))
                                }

                            }
                        }
                         mDatabind.rcvRecommend.mutable.clear()
                        mDatabind.rcvRecommend.addModels(list)


                    }
                    mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()

                }
                //直播间关闭废弃
                override fun onCloseLive(bean: LiveStatus) {
                    super.onCloseLive(bean)

                }
            })
        //主播关闭
        MyWsManager.getInstance(App.app)?.setOtherPushListener(this.toString(),object :
            MOffListener {
            override fun onCloseLive(bean: LiveStatus) {
                if(mDatabind.rcvRecommend.models!=null){
                    for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                                if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId!=null){
                                    if((mDatabind.rcvRecommend.mutable[i] as BeingLiveBean).userId.equals(bean.anchorId)){
                                        mDatabind.rcvRecommend.mutable.removeAt(i)
                                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                                        mViewModel.getNowLive(true,type = type.toString())
                                        break
                                    }
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
                        setLiveMatchItem()
//                        var bindingItem=getBinding<ItemMainLiveListBinding>()
//                        var  bean=_data as BeingLiveBean
//                        Glide.with(context)
//                            .load(bean.titlePage) // 替换为您要加载的图片 URL
//                            .error(R.drawable.main_top_load)
//                            .placeholder(R.drawable.main_top_load)
//                            .into(bindingItem.ivLiveBe)
//                        Glide.with(context)
//                            .load(bean.userLogo) // 替换为您要加载的图片 URL
//                            .error(R.drawable.default_anchor_icon)
//                            .placeholder(R.drawable.default_anchor_icon)
//                            .into(bindingItem.ivLiveHead)
//                        bindingItem.txtLiveName.text=bean.nickName
//                        //比赛类型 1足球，2篮球,可用值:1,2
//                        if(type==1){
//                            bindingItem.txtLiveTeam.text="${bean.homeTeamName}VS${bean.awayTeamName}"
//                        }else{
//                            bindingItem.txtLiveTeam.text="${bean.awayTeamName }VS${bean.homeTeamName}"
//                        }
//
//
//                        bindingItem.txtLiveCompetition.text=bean.competitionName
//                        if(bean.hotValue<=9999){
//                            bindingItem.txtLiveHeat.text="${bean.hotValue}"
//                        }else{
//                            bindingItem.txtLiveHeat.text="9999+"
//                        }
//
//                        if(layoutPosition%2==0){
//                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
//                            layoutParams.setMargins(0, 0, context.dp2px(4), context.dp2px(8))
//                            bindingItem.llLiveSpacing.layoutParams =layoutParams
//                        }else{
//                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
//                            layoutParams.setMargins(context.dp2px(4), 0, 0, context.dp2px(8))
//                            bindingItem.llLiveSpacing.layoutParams =layoutParams
//                        }
                    }


                }

            }
            R.id.llLiveSpacing.onClick {
                SoundManager.playMedia()
                val bean=_data as BeingLiveBean
                if(bean.pureFlow){
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", pureFlow = true  )
                }else{
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId,videoUrl = bean.playUrl )
                }
            }

        }
    }

    override fun createObserver() {
        super.createObserver()

        //首页获取到数据了
        appViewModel.mainDateShowEvent.observeForever {
            if(isAdded){
                mViewModel.getNowLive(true, type = type.toString())
            }

        }

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