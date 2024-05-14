package com.xcjh.app.ui.home.msg

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.drake.statelayout.StateConfig
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.FriendListBean
import com.xcjh.app.databinding.FrMsgfriendBinding
import com.xcjh.app.databinding.ItemMsgfrienddelBinding
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.utils.delFriDilog
import com.xcjh.app.view.CustomHeader
import com.xcjh.app.view.PopupKickOut
import com.xcjh.app.view.SideBarLayout.OnSideBarLayoutListener
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical

/**
 * 首页好友Fragment
 */
class MsFriendFragment : BaseFragment<MsgVm, FrMsgfriendBinding>() {

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
        try {


            mDatabind.rec.run {
                vertical()
                distance(0, 0, 0, 0)
            }
            mDatabind.state.apply {
                StateConfig.setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)
                onEmpty {
                    this.findViewById<TextView>(R.id.txtEmptyName).text =
                        resources.getString(R.string.nofriends)
                    this.findViewById<ImageView>(R.id.ivEmptyIcon)
                        .setImageDrawable(resources.getDrawable(R.drawable.ic_empety_msg))
                    this.findViewById<ImageView>(R.id.ivEmptyIcon).setOnClickListener { }
                }
            }
            mDatabind.smartCommon.setRefreshHeader(CustomHeader(requireContext()))
            mDatabind.rec.setup {
                addType<FriendListBean> {

                    R.layout.item_msgfrienddel
                }
                onBind {
                    var binding = getBinding<ItemMsgfrienddelBinding>()
                    var item = _data as FriendListBean
                    Glide.with(context).load(item?.head).placeholder(R.drawable.default_anchor_icon)
                        .into(binding.ivhead)

                    binding.tvname.text = item.nickName
                    // 设置item数据
                    binding.lltItem.setOnClickListener {
                        SoundManager.playMedia()
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
                    binding.lltDelete.setOnClickListener {
                        delFriDilog(requireActivity()) { it ->
                            if (it) {//点击了确定
                                mViewModel.getUnNoticeFriend(item.anchorId)
                                mDatabind.rec.mutable.removeAt(bindingAdapterPosition)
                                mDatabind.rec.bindingAdapter.notifyItemRemoved(
                                    bindingAdapterPosition
                                ) // 通知更新

                                var bean = mDatabind.rec.models as List<FriendListBean>
                                if (bean.isEmpty()) {
                                    mDatabind.state.showEmpty()
                                }
                            }

                        }


                    }


                }
            }.models = listdata


            mDatabind.smartCommon.setOnRefreshListener {
                mViewModel.getFriendList(true, "")
            }
                .setOnLoadMoreListener {
                    mViewModel.getFriendList(false, "")
                }


            //登录或者登出
            appViewModel.updateLoginEvent.observe(this) {
                if (it) {
                    mViewModel.getFriendList(true, "")
                } else {
                    listdata.clear()
                    mDatabind.rec.models = listdata

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
            appViewModel.appMsgResum.observe(this) {
                if (it) {
                    if (CacheUtil.isLogin()) {
                        mViewModel.getFriendList(true, "")
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        if (CacheUtil.isLogin()) {
            mViewModel.getFriendList(true, "")
        }
    }

    private fun initEvent() {


    }

    override fun createObserver() {

        try {
            mViewModel.frendList.observe(this) {
                if (it.isSuccess) {
                    //成功
                    when {
                        //第一页并没有数据 显示空布局界面
                        it.isFirstEmpty -> {
                            mDatabind.smartCommon.finishRefresh()
                            mDatabind.state.showEmpty()
                        }
                        //是第一页
                        it.isRefresh -> {
                            mDatabind.smartCommon.finishRefresh()
                            mDatabind.smartCommon.resetNoMoreData()
                            mDatabind.state.showContent()
                            var list = getPinyinList(it.listData)
                            mDatabind.rec.models = list


                        }
                        //不是第一页
                        else -> {
                            mDatabind.state.showContent()
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
                    mDatabind.state.showEmpty()
                    //mAdapter.emptyView = empty
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
        } catch (e: Exception) {
        }
    }


    fun getPinyinList(list: List<FriendListBean>): List<FriendListBean> {
        val pinyinList = mutableListOf<FriendListBean>()
        try {
            mLetters.clear()

            for (item in list) {
                val firstLetter = item.shortName
                if (!mLetters.contains(firstLetter)) {
                    mLetters.add(firstLetter)
                }
                item.pinyin = firstLetter

                pinyinList.add(item)
            }
            pinyinList.sortedWith(compareBy<FriendListBean> {
                when (it.pinyin) {//字母排序
                    "#" -> 1
                    else -> 0
                }
            }.thenBy { it.pinyin })


            mDatabind.indexBar.setNewLetter(mLetters)
        } catch (e: Exception) {

        }
        return pinyinList
    }

}