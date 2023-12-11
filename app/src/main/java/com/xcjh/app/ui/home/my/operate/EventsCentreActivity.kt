package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.utils.*
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.EventsBean
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.databinding.ActivityEventsCentreBinding
import com.xcjh.app.databinding.ActivitySearchBinding
import com.xcjh.app.databinding.ItemEventsCentreBinding
import com.xcjh.app.databinding.ItemNewsListBinding
import com.xcjh.app.ui.search.SearchVm
import com.xcjh.app.web.WebActivity
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.dp2px
import java.text.SimpleDateFormat
import java.util.*

/**
 * 活动中心
 */
class EventsCentreActivity : BaseActivity<EventsCentreVm, ActivityEventsCentreBinding>() {



    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.events_txt_title)
        adapter()
        mViewModel.getEventsList(true)
        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getEventsList(true)

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getEventsList(false)
            }
        })
    }





    fun  adapter(){
        mDatabind.rcvRecommend.linear().setup {
            addType<EventsBean>(R.layout.item_events_centre)
            onBind {
                when (itemViewType) {
                    R.layout.item_events_centre -> {
                        var bindingItem=getBinding<ItemEventsCentreBinding>()
                        var  bean=_data as EventsBean
                        Glide.with(context)
                            .load(bean.mainPic) // 替换为您要加载的图片 URL
                            .error(R.drawable.load_square)
                            .apply(RequestOptions().transform(RoundedCorners(context.dp2px(8))))
                            .placeholder(R.drawable.load_square)
                            .into(bindingItem.ivNewsIcon)
                        bindingItem.txtEventsTitle.text=bean.title
                        val date = Date(bean.updateTime.toLong())
                        var formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        bindingItem.txtEventsTime.text=formatter.format(date)


                    }


                }

            }
            R.id.llEventsClick.onClick {
                var  bean=_data as EventsBean
                startNewActivity<WebActivity>() {
                    this.putExtra(Constants.WEB_URL, bean.id)
                    this.putExtra(Constants.WEB_VIEW_TYPE, 2)
                    this.putExtra(Constants.WEB_VIEW_ID, bean.id)
                    this.putExtra(Constants.CHAT_TITLE, getString(R.string.events_txt_title))
                }
            }

        }
    }


    override fun createObserver() {
        super.createObserver()

        mViewModel.eventsList.observe(this){
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