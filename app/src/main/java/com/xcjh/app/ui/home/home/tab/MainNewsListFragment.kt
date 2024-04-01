package com.xcjh.app.ui.home.home.tab

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.utils.*
import com.drake.engine.utils.dp
import com.drake.spannable.replaceSpan
import com.drake.spannable.span.GlideImageSpan
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.databinding.FragmentMainNewsListBinding
import com.xcjh.app.databinding.ItemNewsListBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.view.CustomHeader
import com.xcjh.app.web.WebActivity
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.myToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 首页新闻列表
 */
class MainNewsListFragment  : BaseFragment<MainNewsListVm, FragmentMainNewsListBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        adapter()
        mViewModel.getNewsList(true)
        mDatabind.smartCommon.setRefreshHeader( CustomHeader(requireContext()))
        //取消下拉刷新
        mDatabind.smartCommon.setEnableRefresh(false)
        mDatabind.smartCommon.setEnableOverScrollDrag(true)
        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getNewsList(true)

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getNewsList(false)
            }
        })


        //更新新闻列表
        MyWsManager.getInstance(App.app)?.setOtherPushListener(this.toString(), object :
            OtherPushListener {
            override fun onNewsUpdateDate() {
                super.onNewsUpdateDate()
                mViewModel.getNewsList(true)
            }

        })

    }



    fun  adapter(){
        mDatabind.rcvRecommend.linear().setup {
            addType<NewsBean>(R.layout.item_news_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_news_list -> {
                        var bindingItem=getBinding<ItemNewsListBinding>()
                        var  bean=_data as NewsBean
                        if(modelPosition==0){
                            bindingItem.rlNewsShowHot.visibility= View.VISIBLE
                            bindingItem.rlNewsShowList.visibility= View.GONE
                            Glide.with(context)
                                .load(bean.pic) // 替换为您要加载的图片 URL
                                .error(R.drawable.zwt_banner)
                                .placeholder(R.drawable.zwt_banner)
                                .into(bindingItem.ivNewsBe)
                            var name="%s"+bean.title

                            //语言 0是中文  1是繁体  2是英文
                            if(Constants.languageType==0){
                                bindingItem.txtNewsHot.text=name.replaceSpan("%s") {
                                    GlideImageSpan(bindingItem.txtNewsHot, R.drawable.icon_news_hot_zh)
                                        .setRequestOption(RequestOptions.centerCropTransform())
                                        .setDrawableSize(50.dp,15.dp)
                                }
                            }else    if(Constants.languageType==1){
                                bindingItem.txtNewsHot.text=name.replaceSpan("%s") {
                                    GlideImageSpan(bindingItem.txtNewsHot, R.drawable.icon_news_hot_rtw)
                                        .setRequestOption(RequestOptions.centerCropTransform())
                                        .setDrawableSize(50.dp,15.dp)
                                }
                            }else{
                                bindingItem.txtNewsHot.text=name.replaceSpan("%s") {
                                    GlideImageSpan(bindingItem.txtNewsHot, R.drawable.icon_news_hot)
                                        .setRequestOption(RequestOptions.centerCropTransform())
                                        .setDrawableSize(50.dp,15.dp)
                                }
                            }



                        }else{
                            bindingItem.rlNewsShowHot.visibility= View.GONE
                            bindingItem.rlNewsShowList.visibility= View.VISIBLE
                            Glide.with(context)
                                .load(bean.pic) // 替换为您要加载的图片 URL
                                .error(R.drawable.zwt_banner)
                                .placeholder(R.drawable.zwt_banner)
                                .into(bindingItem.ivNewsIcon)
//                            Glide.with(context)
//                                .load(bean.pic) // 替换为您要加载的图片 URL
//                                .error(R.drawable.main_banner_load)
//                                .apply(RequestOptions().transform(RoundedCorners(context.dp2px(8))))
//                                .placeholder(R.drawable.main_banner_load)
//                                .into(bindingItem.ivNewsIcon)
                            bindingItem.txtNewsName.text=bean.title
                            val date = Date(bean.publishTime.toLong())
                            var formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            bindingItem.txtNewsDate.text="${formatter.format(date)} ${resources.getString(R.string.release_txt_name)}"

                        }



                    }

                }

            }
            R.id.rlNewsShowList.onClick {
                var  bean=_data as NewsBean
                startNewActivity<WebActivity>() {
                    this.putExtra(Constants.WEB_URL, bean.sourceUrl)
                    this.putExtra(Constants.WEB_VIEW_TYPE, 1)
                    this.putExtra(Constants.WEB_VIEW_ID, bean.id)
                    this.putExtra(Constants.CHAT_TITLE, getString(R.string.news_txt_details))
                }
                mViewModel.getNewsInfo(bean.id)
            }
            R.id.rlNewsShowHot.onClick {
                var  bean=_data as NewsBean
                startNewActivity<WebActivity>() {
                    this.putExtra(Constants.WEB_URL, bean.sourceUrl)
                    this.putExtra(Constants.WEB_VIEW_TYPE, 1)
                    this.putExtra(Constants.WEB_VIEW_ID, bean.id)
                    this.putExtra(Constants.CHAT_TITLE, getString(R.string.news_txt_details))
                }
                mViewModel.getNewsInfo(bean.id)
            }

        }.addModels(arrayListOf())
    }

    override fun createObserver() {
        super.createObserver()
        //首页获取到数据了
        appViewModel.mainDateShowEvent.observeForever {
            if(isAdded){
                mViewModel.getNewsList(true)
            }

        }
        //登录或者登出
        appViewModel.updateLoginEvent.observe(this){

            mViewModel.getNewsList(true)
        }


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
                        mDatabind.smartCommon.finishLoadMore()
                        if(it.listData.isEmpty()) {
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        } else{
                            mDatabind.rcvRecommend.addModels(it.listData)
                        }

                        mDatabind.state.showContent()

                    }

                }

            }else{
                mDatabind.smartCommon.finishRefresh()
                mDatabind.smartCommon.resetNoMoreData()
                mDatabind.smartCommon.finishLoadMore()
                if(it.isRefresh){

                    if(mDatabind.rcvRecommend.models!=null){
                        mDatabind.rcvRecommend.mutable.clear()
                    }
                    mDatabind.state.showEmpty()

//                    lifecycleScope.launch {
//
//                        delay(5000) // 延迟 5 秒
////                            delay(2000) // 延迟 10 秒、
////                            mDatabind.smartCommon.autoRefresh()
//                        mViewModel.getNewsList(true)
//                    }
                }
            }

        }

    }
}