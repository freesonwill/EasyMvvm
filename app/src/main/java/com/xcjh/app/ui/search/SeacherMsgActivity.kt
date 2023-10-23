package com.xcjh.app.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.adapter.SearchMsgAdapter
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivitySeachermsgBinding
import com.xcjh.app.ui.home.msg.MsgVm
import com.xcjh.app.utils.smartListData
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical

/***
 * 搜索消息
 */

class SeacherMsgActivity : BaseActivity<MsgVm, ActivitySeachermsgBinding>() {

    private val mAdapter by lazy { SearchMsgAdapter() }
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()


        mDatabind.rec.run {
            vertical()
            adapter = mAdapter
            distance(0, 0, 0, 0)
        }
        mAdapter.isEmptyViewEnable = true

        mDatabind.titleTop.tvCancel.setOnClickListener {
            if (mDatabind.titleTop.tvCancel.text.toString() == resources.getString(R.string.cancel)) {
                mDatabind.titleTop.tvTitle.setText("")
                mDatabind.titleTop.tvCancel.text=resources.getString(R.string.search)
                mAdapter.submitList(null)
            } else {
                if (mDatabind.titleTop.tvTitle.text.toString().isNotEmpty()) {
                    mViewModel.getMsgList(true, mDatabind.titleTop.tvTitle.text.toString())
                }
            }
        }
        mDatabind.titleTop.ivdel.setOnClickListener {
            mDatabind.titleTop.tvTitle.setText("")
        }
        mDatabind.titleTop.tvTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    mDatabind.titleTop.ivdel.visibility = View.VISIBLE
                } else {
                    mDatabind.titleTop.ivdel.visibility = View.INVISIBLE
                }
            }
        })


    }

    override fun createObserver() {
        val empty = layoutInflater!!.inflate(R.layout.layout_empty, null)

        mViewModel.msgList.observe(this) {
            smartListData(
                activity = this,
                it,
                mAdapter,
                mDatabind.smartCommon,
                imgEmptyId = R.drawable.main_empty_icon
            )

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
                        mDatabind.titleTop.tvCancel.text = resources.getString(R.string.cancel)
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