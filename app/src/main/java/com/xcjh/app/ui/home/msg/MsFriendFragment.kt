package com.xcjh.app.ui.home.msg

import android.os.Bundle
import com.xcjh.app.R
import com.xcjh.app.adapter.MsgFriendAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.FriendListBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.FrMsgfriendBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical


class MsFriendFragment : BaseFragment<MsgVm, FrMsgfriendBinding>() {
    private val mAdapter by lazy { MsgFriendAdapter() }
    var listdata: MutableList<FriendListBean> = ArrayList<FriendListBean>()

    companion object {

        fun newInstance(): MsFriendFragment {
            val args = Bundle()
            val fragment = MsFriendFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.rec.run {
            vertical()
            adapter = mAdapter
            distance(0, 0, 0, 16)
        }
        mAdapter.isEmptyViewEnable = true
        if(CacheUtil.isLogin()){
            mViewModel.getFriendList(true)
        }
        mDatabind.smartCommon.setOnRefreshListener { mViewModel.getFriendList(true) }
            .setOnLoadMoreListener { mViewModel.getFriendList(false) }
       // mViewModel.getNoticeUser()
        // 需要传递控件 id
        mAdapter.addOnItemChildClickListener(R.id.lltDelete) { adapter, view, position ->

            mViewModel.getUnNoticeFriend(mAdapter.getItem(position)?.anchorId.toString())
            mAdapter.removeAt(position)
        }
        //登录或者登出
        appViewModel.updateLoginEvent.observe(this){
            if(it){
                mViewModel.getMsgList(true, "")
            }else{
                listdata.clear()
                mAdapter.submitList(listdata)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initEvent() {


    }

    override fun createObserver() {
        val empty = layoutInflater!!.inflate(R.layout.layout_empty, null)

        mViewModel.frendList.observe(this) {
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
                        listdata.clear()
                        listdata.addAll(it.listData)
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
                            listdata.addAll(it.listData)
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