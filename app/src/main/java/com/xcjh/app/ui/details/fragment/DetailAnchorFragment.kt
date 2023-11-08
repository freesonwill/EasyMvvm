package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.databinding.FragmentDetailTabAnchorBinding
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.utils.judgeLogin
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.view.textString

/**
 * 主播
 */

class DetailAnchorFragment(
    var anchorId: String,
    override val typeId: Long = 2,
) : BaseVpFragment<BaseViewModel, FragmentDetailTabAnchorBinding>() {
    private var focus: Boolean = false
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }

    override fun initView(savedInstanceState: Bundle?) {
        //点击关注或者取消关注
        mDatabind.tvTabAnchorFollow.setOnClickListener {
            judgeLogin {
                if (!focus) {
                    vm.followAnchor(anchorId)
                } else {
                    vm.unFollowAnchor(anchorId)
                }
            }
        }
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        //主播详情接口返回监听处理
        vm.anchor.observe(this) {
            if (it != null) {
                this.anchorId = it.id
                mDatabind.tvTabAnchorNick.text = it.nickName  //主播昵称
                mDatabind.tvDetailTabAnchorFans.text = it.fansCount //主播粉丝数量
                mDatabind.tvTabAnchorNotice.text = it.notice //主播公告
                setFocusUI(it.focus)
                Glide.with(this).load(it.head).placeholder(mDatabind.ivTabAnchorAvatar.drawable)
                    .into(mDatabind.ivTabAnchorAvatar) //主播头像
                //点击私信跳转聊天界面逻辑，根据传参来跳转，介于聊天界面还在开发中，这里先占位
                mDatabind.tvTabAnchorChat.setOnClickListener { v ->
                    judgeLogin {
                        startNewActivity<ChatActivity>() {
                            putExtra(Constants.USER_ID, it.id)
                            putExtra(Constants.USER_NICK, it.nickName)
                            putExtra(Constants.USER_HEAD, it.head)
                        }
                    }
                }
            }
        }
        vm.isfocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(true)
                mDatabind.tvDetailTabAnchorFans.text =
                    (mDatabind.tvDetailTabAnchorFans.textString().toInt() + 1).toString() //主播粉丝数量+1
            }
        }
        vm.isUnFocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(false)
                mDatabind.tvDetailTabAnchorFans.text =
                    (mDatabind.tvDetailTabAnchorFans.textString().toInt() - 1).toString() //主播粉丝数量-1
            }
        }
        vm.anchorInfo.observe(this) {
            updateInfo(it.userId)
        }
    }

    private fun setFocusUI(focus: Boolean) {
        this.focus = focus
        if (this.focus) {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.dis_focus)
        } else {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.add_focus)
        }
    }

    private fun updateInfo(anchorId: String?) {
        if (anchorId.isNullOrEmpty()) {
            return
        } else {
            this.anchorId = anchorId
        }
        //  lazyLoadData()
    }

}