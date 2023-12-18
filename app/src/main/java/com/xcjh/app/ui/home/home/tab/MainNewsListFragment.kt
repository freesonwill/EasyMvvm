package com.xcjh.app.ui.home.home.tab

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.utils.*
import com.drake.engine.utils.dp
import com.drake.spannable.replaceSpan
import com.drake.spannable.span.GlideImageSpan
import com.drake.statelayout.StateConfig
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.databinding.FragmentMainNewsListBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.databinding.ItemNewsListBinding
import com.xcjh.app.web.WebActivity
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 首页新闻列表
 */
class MainNewsListFragment  : BaseFragment<MainNewsListVm, FragmentMainNewsListBinding>() {



    override fun initView(savedInstanceState: Bundle?) {

        adapter()
        mViewModel.getNewsList(true)




        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getNewsList(true)

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getNewsList(false)
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
                                .error(R.drawable.main_banner_load)
                                .placeholder(R.drawable.main_banner_load)
                                .into(bindingItem.ivNewsBe)
                            var name="%s"+bean.title
                           bindingItem.txtNewsHot.text=name.replaceSpan("%s") {
                               GlideImageSpan(bindingItem.txtNewsHot, R.drawable.icon_news_hot)
                                   .setRequestOption(RequestOptions.centerCropTransform())
                                   .setDrawableSize(50.dp,15.dp)
                           }

                        }else{
                            bindingItem.rlNewsShowHot.visibility= View.GONE
                            bindingItem.rlNewsShowList.visibility= View.VISIBLE
                            Glide.with(context)
                                .load(bean.pic) // 替换为您要加载的图片 URL
                                .error(R.drawable.main_banner_load)
                                .placeholder(R.drawable.main_banner_load)
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
                            bindingItem.txtNewsDate.text=formatter.format(date)

                        }



                    }


                }

            }
            R.id.rlNewsClick.onClick {
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