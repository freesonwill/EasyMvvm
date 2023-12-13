package com.xcjh.app.ui.home.msg

import android.os.Bundle
import com.bumptech.glide.Glide
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.adapter.MsgFriendAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.FriendListBean
import com.xcjh.app.databinding.FrMsgfriendBinding
import com.xcjh.app.databinding.ItemMsgfrienddelBinding
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.view.SideBarLayout.OnSideBarLayoutListener
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical


class MsFriendFragment : BaseFragment<MsgVm, FrMsgfriendBinding>() {
    private val mAdapter by lazy { MsgFriendAdapter() }
    var listdata: MutableList<FriendListBean> = ArrayList<FriendListBean>()
    var mLetters = mutableListOf<String>()

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
            distance(0, 0, 0, 16)
        }
        mDatabind.rec.setup {
            addType<FriendListBean> {

                R.layout.item_msgfrienddel
            }
            onBind {
                var binding = getBinding<ItemMsgfrienddelBinding>()
                var item = _data as FriendListBean
                Glide.with(context).load(item?.head).placeholder(R.drawable.default_anchor_icon)
                    .into(binding.ivhead)
                binding.tvname.text = item?.nickName
                // 设置item数据
                binding.lltItem.setOnClickListener {

                    com.xcjh.base_lib.utils.startNewActivity<ChatActivity>() {
                        if (item?.anchorId?.isNotEmpty() == true) {
                            this.putExtra(Constants.USER_ID, item?.anchorId)
                        } else {
                            this.putExtra(Constants.USER_ID, "")
                        }
                        if (item?.nickName?.isNotEmpty() == true) {
                            this.putExtra(Constants.USER_NICK, item?.nickName)
                        } else {
                            this.putExtra(Constants.USER_NICK, "")
                        }
                        if (item?.head?.isNotEmpty() == true) {
                            this.putExtra(Constants.USER_HEAD, item?.head)
                        } else {
                            this.putExtra(Constants.USER_HEAD, "")
                        }

                    }
                }


            }
        }.models = listdata

        if (CacheUtil.isLogin()) {
            mViewModel.getFriendList(true, "")
        }
        mDatabind.smartCommon.setOnRefreshListener { mViewModel.getFriendList(true, "") }
            .setOnLoadMoreListener { mViewModel.getFriendList(false, "") }
// mViewModel.getNoticeUser()
// 需要传递控件 id
        mAdapter.addOnItemChildClickListener(R.id.lltDelete) { adapter, view, position ->

            mViewModel.getUnNoticeFriend(mAdapter.getItem(position)?.anchorId.toString())
            mAdapter.removeAt(position)
        }
//登录或者登出
        appViewModel.updateLoginEvent.observe(this) {
            if (it) {
                mViewModel.getMsgList(true, "")
            } else {
                listdata.clear()
                mAdapter.submitList(listdata)
                mAdapter.notifyDataSetChanged()
            }
        }
// 索引列表
        mDatabind.indexBar.setSideBarLayout(OnSideBarLayoutListener { word -> //根据自己业务实现
            for (i in mDatabind.rec.models!!.indices) {
                var ss = mDatabind.rec.models!![i] as FriendListBean
                if (ss.shortName == word) {
                    mDatabind.rec.smoothScrollToPosition(i)
                    break
                }
            }
        })

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
                        mDatabind.smartCommon.showEmpty()
                    }
                    //是第一页
                    it.isRefresh -> {
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.smartCommon.resetNoMoreData()
                        mDatabind.smartCommon.showContent()
                        var list = getPinyinList(it.listData)
                        mDatabind.rec.models = list


                    }
                    //不是第一页
                    else -> {
                        mDatabind.smartCommon.showContent()
                        if (it.listData.isEmpty()) {
                            mDatabind.smartCommon.setEnableLoadMore(false)
                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        } else {
                            mDatabind.smartCommon.setEnableLoadMore(true)
                            mDatabind.smartCommon.finishLoadMore()
                            var list = getPinyinList(it.listData)
                            mDatabind.rec.addModels(list)
                        }

                    }
                }
            } else {
                mDatabind.smartCommon.showEmpty()
                mAdapter.emptyView = empty
                //失败
                if (it.isRefresh) {
                    mDatabind.smartCommon.finishRefresh()
                    //如果是第一页，则显示错误界面，并提示错误信息

                } else {
                    mDatabind.smartCommon.finishLoadMore(false)
                }
            }
        }
        appViewModel.updateSomeData.observe(this) {
            if (it.equals("friends")) {
                mViewModel.getFriendList(true, "")
            }
        }
    }

    private fun getPinyinFirstLetter(str: String): String {
        val firstChar = str[0].toUpperCase()
        return if (firstChar.isLetter()) {
            firstChar.toString()

        } else {
            "#"
        }
    }

    fun getPinyinList(list: List<FriendListBean>): List<FriendListBean> {
        val pinyinList = mutableListOf<FriendListBean>()

        for (item in list) {
            val firstLetter =item.shortName
            if (!mLetters.contains(firstLetter)) {
                mLetters.add(firstLetter)
            }
            item.pinyin = firstLetter

            pinyinList.add(item)
        }

        pinyinList.sortBy { it.pinyin }


        mDatabind.indexBar.setNewLetter(mLetters)
        return pinyinList
    }

    fun findFriendByPinyin(friendList: List<FriendListBean>, pinyin: String): FriendListBean? {
        LogUtils.d("")
        return friendList.find { it.pinyin.startsWith(pinyin, ignoreCase = true) }
    }
}