package com.xcjh.app.ui.feed

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.xcjh.app.R
import com.xcjh.app.adapter.FeedNoticeAdapter
import com.xcjh.app.adapter.MsgFriendAdapter
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.loge
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivityFeednoticeBinding
import com.xcjh.app.databinding.ActivityWebBinding
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical

/***
 * 反馈通知
 */

class FeedNoticeActivity : BaseActivity<FeedVm, ActivityFeednoticeBinding>() {

    private val mAdapter by lazy { FeedNoticeAdapter() }
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .init()

        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.txt_feedtitle)
        mDatabind.rec.run {
            vertical()
            adapter = mAdapter
            distance(0, 0, 0, 16)
        }
        mAdapter.isEmptyViewEnable=true
        mViewModel.getFeedNoticeList(true)

    }

    override fun createObserver() {
        val empty = layoutInflater!!.inflate(R.layout.layout_empty, null)

        mViewModel.feedList.observe(this) {
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        mDatabind.smartCommon.finishRefresh()
                        mAdapter.emptyView = empty
                    }
                    //是第一页
                    it.isRefresh -> {
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.smartCommon.resetNoMoreData()
                        mAdapter.submitList(it.listData)


                    }
                    //不是第一页
                    else -> {
                        if (it.listData.isEmpty()) {
                            mDatabind.smartCommon.setEnableLoadMore(false)
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        } else {
                            mDatabind.smartCommon.setEnableLoadMore(true)
                            mDatabind.smartCommon.finishLoadMore()

                            mAdapter.addAll(it.listData)
                        }

                    }
                }
            } else {
                mAdapter.emptyView = empty
                //失败
                if (it.isRefresh) {
                    mDatabind.smartCommon.finishRefresh()
                    //如果是第一页，则显示错误界面，并提示错误信息
                    mAdapter.submitList(null)
                } else {
                    mDatabind.smartCommon.finishLoadMore(false)
                }
            }
        }
    }

}