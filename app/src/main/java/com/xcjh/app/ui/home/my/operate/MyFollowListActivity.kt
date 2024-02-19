package com.xcjh.app.ui.home.my.operate

import android.R.attr.button
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.drake.brv.utils.*
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.FollowAnchorBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivityMyFollowListBinding
import com.xcjh.app.databinding.ItemMyFollowBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.view.CustomHeader
import com.xcjh.base_lib.utils.dp2px


/**
 * 我的关注列表
 */
class MyFollowListActivity : BaseActivity<MyFollowListVm, ActivityMyFollowListBinding>() {
    var list=ArrayList<FollowAnchorBean>()
    var screenWidth:Int=0


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        val displayMetrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.follow_txt_title)
        adapter()
        mViewModel.getAnchorPageList(true)
        //默认取消上拉加载更多
        mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
        mDatabind.smartCommon.setRefreshHeader( CustomHeader(this))
        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getAnchorPageList(true)

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getAnchorPageList(false)
            }
        })
    }


    fun  adapter(){
        mDatabind.rcvRecommend.linear().setup {
            addType<FollowAnchorBean>(R.layout.item_my_follow)
            onBind {
                when (itemViewType) {
                    R.layout.item_my_follow -> {
                        var bindingItem=getBinding<ItemMyFollowBinding>()
                        var  bean=_data as FollowAnchorBean
                        bindingItem.txtFollowName.text=bean.nickName
                         Glide.with(context)
                            .load(bean.head) // 替换为您要加载的图片 URL
                            .error(R.drawable.default_anchor_icon)
                            .placeholder(R.drawable.default_anchor_icon)
                            .into(bindingItem.ivLiveHead)
                        bindingItem.txtFollowFansNum.text=resources.getString(R.string.follow_txt_fans_num,"${bean.fans}")
                        //是否关注
                        if (bean.isFollow){
                            bindingItem.txtFollowIsFollow.text=resources.getText(R.string.follow_txt_followed)
                            bindingItem.txtFollowIsFollow.setTextColor(ContextCompat.getColor(context,R.color.c_94999f))
                            bindingItem.txtFollowIsFollow.background=ContextCompat.getDrawable(context,R.drawable.shape_r20_f2f3f7)
                        }else{
                            bindingItem.txtFollowIsFollow.text=resources.getText(R.string.follow_txt_add)
                            bindingItem.txtFollowIsFollow.setTextColor(ContextCompat.getColor(context,R.color.c_ffffff))
                            bindingItem.txtFollowIsFollow.background=ContextCompat.getDrawable(context,R.drawable.shape_r20_34a853)

                        }

                        //判断是否在直播 //计算text显示的宽度
                        if(bean.liveId!=null&&bean.liveId.isNotEmpty()){

                            bindingItem.txtFollowName.maxWidth=screenWidth-context.dp2px(158)-context.dp2px(103)
                            bindingItem.txtMyLiveType.visibility=View.VISIBLE
                            bindingItem.stateLoadingImg.visibility=View.VISIBLE
                            bindingItem.stateLoadingImg.playAnimation()

                        }else{
                            bindingItem.txtFollowName.maxWidth=screenWidth-context.dp2px(85)
//                            bindingItem.txtFollowName.maxWidth=screenWidth
                            bindingItem.txtMyLiveType.visibility=View.GONE
                            bindingItem.stateLoadingImg.visibility= View.GONE
                        }



                    }

                }
            }
            R.id.txtFollowIsFollow.onClick {
                var  bean=_data as FollowAnchorBean
                if(bean.isFollow){
                    mViewModel.unfollowAnchor(bean.anchorId,layoutPosition)
                }else{
                    mViewModel.followAnchor(bean.anchorId,layoutPosition)
                }


            }

//            R.id.txtFollowIsFollow.onClick {
//                var  bean=_data as FollowAnchorBean
//                mViewModel.unfollowAnchor(bean.anchorId,layoutPosition)
//
//            }
            R.id.rlRoot.onClick {
                var  bean=_data as FollowAnchorBean
                if(bean.liveId!=null&&bean.liveId.isNotEmpty()){
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId, anchorId = bean.anchorId  )
                }


            }
            R.id.rlClickNo.onClick {

            }
        }



    }



    override fun createObserver() {
        super.createObserver()
        //取消关注
        mViewModel.unfollow.observe(this){

            (mDatabind.rcvRecommend.mutable[it] as FollowAnchorBean).isFollow=false
            mDatabind.rcvRecommend.bindingAdapter.notifyItemChanged(it)
//            mDatabind.rcvRecommend!!.mutable.removeAt(it) // 删除数据
//            mDatabind.rcvRecommend.adapter!!.notifyItemRemoved(it)

//            if(mDatabind.rcvRecommend.models!!.isEmpty()){
//                mDatabind.state.showEmpty()
//            }
//            mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()

        }
        //关注主播
        mViewModel.followValue.observe(this){
            (mDatabind.rcvRecommend.mutable[it] as FollowAnchorBean).isFollow=true
            mDatabind.rcvRecommend.bindingAdapter.notifyItemChanged(it)
        }


        mViewModel.followList.observe(this){
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        //取消上拉加载更多
                        mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
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