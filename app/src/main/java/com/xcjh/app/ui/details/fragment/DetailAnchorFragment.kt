package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
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
import com.xcjh.base_lib.utils.toHtml
import com.xcjh.base_lib.utils.view.textString


/**
 * 主播
 */

class DetailAnchorFragment(
    var anchorId: String,
    override val typeId: Long = 2,
) : BaseVpFragment<BaseViewModel, FragmentDetailTabAnchorBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }

    override fun initView(savedInstanceState: Bundle?) {

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
                mDatabind.tvTabAnchorNotice.movementMethod = LinkMovementMethod.getInstance()
              /*  it.notice?.toHtml {
                    Handler(Looper.getMainLooper()).post {
                        mDatabind.tvTabAnchorNotice.text = it
                    }
                }*/
                mDatabind.tvTabAnchorNotice.text = it.notice?.toHtml() //主播公告

                Glide.with(this).load(it.head)
                    .placeholder(mDatabind.ivTabAnchorAvatar.drawable)
                    .into(mDatabind.ivTabAnchorAvatar) //主播头像

            }
        }

        vm.anchorInfo.observe(this) {
            updateInfo(it.userId)
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