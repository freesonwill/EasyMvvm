package com.xcjh.app.ui.home.msg

import  android.os.Bundle
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.xcjh.app.MyApplication
import com.xcjh.app.R
import com.xcjh.app.adapter.MsgListAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgListBean
import com.xcjh.app.databinding.FrMsgchildBinding
import com.xcjh.app.ui.room.MsgBeanData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MsgChildFragment : BaseFragment<MsgVm, FrMsgchildBinding>() {
    private val mAdapter by lazy { MsgListAdapter() }
    var listdata: MutableList<MsgListBean> = ArrayList<MsgListBean>()

    var chatId = "0"

    companion object {

        fun newInstance(): MsgChildFragment {
            val args = Bundle()
            val fragment = MsgChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.rec.run {
            vertical()
            adapter = mAdapter
            distance(0, 0, 0, 0)
        }

        mAdapter.isEmptyViewEnable = true
        mAdapter.addOnItemChildClickListener(R.id.lltDelete) { adapter, view, position ->
            mViewModel.getDelMsg(mAdapter.getItem(position)?.anchorId.toString())
            mAdapter.removeAt(position)
            if (listdata.size == 0) {
                val empty = layoutInflater!!.inflate(R.layout.layout_empty, null)
                mAdapter.emptyView = empty
            }
        }
        if (CacheUtil.isLogin()) {
            mViewModel.getMsgList(true, "")
            mDatabind.smartCommon.setOnRefreshListener {
                mViewModel.getMsgList(true, "")
            }.setOnLoadMoreListener {
                mViewModel.getMsgList(false, "")
            }
            initEvent()
        }
        //登录或者登出
        appViewModel.updateLoginEvent.observe(this) {
            if (it) {
                mViewModel.getMsgList(true, "")
                mDatabind.smartCommon.setOnRefreshListener {
                    mViewModel.getMsgList(true, "")
                }.setOnLoadMoreListener {
                    mViewModel.getMsgList(false, "")
                }
                initEvent()
            } else {
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
        MyWsManager.getInstance(requireActivity())!!
            .setC2CListener(javaClass.name, object : C2CListener {
                override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {
                    if (isOk) {

                    }
                }

                override fun onC2CReceive(chat: ReceiveChatMsg) {
                    refshMsg(chat)
                }

                override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {

                }
            })
        appViewModel.updateMsgEvent.observeForever {
            if (isAdded) {
                chatId = it
                if (it == "-1") {//清除消息红点
                    mViewModel.getClreaAllMsg()

                } else {

                }
                //                else {
//                    var bean = ReceiveChatMsg()
//                    bean.fromAvatar = it.avatar
//                    bean.content = it.content
//                    bean.createTime = it.createTime
//                    bean.msgType = it.msgType
//                    bean.fromNickName = it.nick
//                    bean.from = it.anchorId
//                    bean.anchorId = it.anchorId
//                  //  refshMsg(bean)
//                    LogUtils.d("消息变化了" + it.content)
//                }
            }
        }
    }

    fun updataMsg(list:MutableList<MsgListBean>) {

        var num=0
        for (i in 0 until list.size){
            num += list[i].noReadSum
        }
       // appViewModel.updateMainMsgNum.postValue(num)
    }

    override fun createObserver() {
        val empty = layoutInflater!!.inflate(R.layout.layout_empty, null)

        mViewModel.msgList.observe(this) {
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
                        mAdapter.submitList(listdata)
                        mAdapter.notifyDataSetChanged()
                       // updataMsg(listdata)


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
                           // updataMsg(it.listData)
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
        mViewModel.clreaAllMsg.observe(this) {
            appViewModel.updateMainMsgNum.postValue("0")
            mViewModel.getMsgList(true, "")
        }
    }

    override fun onDestroy() {
        MyWsManager.getInstance(requireActivity())!!.removeC2CListener(javaClass.name)
        super.onDestroy()
    }

    fun refshMsg(msg: ReceiveChatMsg) {
        addData(msg)
       // updataMsg(listdata)
        var hasMsg = false
        for (i in 0 until listdata.size) {
            if (msg.anchorId == listdata[i].anchorId) {
                hasMsg = true
                var bean = MsgListBean(
                    if (msg.anchorId == msg.from) {//主播发送的消息
                        msg.fromAvatar!!
                    } else {
                        msg.toAvatar!!
                    },
                    msg.content.toString(),
                    msg.createTime!!,
                    msg.msgType!!,
                    msg.from!!,
                    msg.anchorId!!,
                    listdata[i].id,
                    if (msg.anchorId == msg.from) {//主播发送的消息
                        msg.fromNickName!!
                    } else {
                        msg.toNickName!!
                    },
                    if (chatId == msg.anchorId) {
                        0
                    } else {
                        listdata[i].noReadSum + 1
                    }
                )

                mAdapter[i] = bean//更新Item数据
                mAdapter.swap(i, 0)
                break
            }
        }
        if (!hasMsg) {
            var bean = MsgListBean(
                if (msg.anchorId == msg.from) {//主播发送的消息
                    msg.fromAvatar!!
                } else {
                    msg.toAvatar!!
                },
                msg.content.toString(),
                msg.createTime!!,
                msg.msgType!!,
                msg.from!!,
                msg.anchorId!!,
                msg.id!!,
                if (msg.anchorId == msg.from) {//主播发送的消息
                    msg.fromNickName!!
                } else {

                    msg.toNickName!!
                },
                if (chatId == msg.anchorId) {
                    0
                } else {
                    1
                }
            )
            mAdapter.add(0, bean)
        }


    }

    fun addData(chat: ReceiveChatMsg) {
        if (chat.from==chat.anchorId) {
            var beanmy: MsgBeanData = MsgBeanData()
            beanmy.anchorId = chat.anchorId
            beanmy.fromId = chat.from
            beanmy.content = chat.content
            beanmy.chatType = chat.chatType
            beanmy.cmd = 11
            beanmy.msgType = chat.msgType
            beanmy.createTime = chat.createTime

            GlobalScope.launch {

                MyApplication.dataBase!!.chatDao?.insert(beanmy)

            }
        }
    }
}