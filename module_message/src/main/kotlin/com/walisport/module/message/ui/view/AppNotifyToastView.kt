package com.walisport.module.message.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import arch.cayenne.lib.common.data.constants.AppNotifyBean
import com.bumptech.glide.Glide
import com.walisport.module.message.R
import com.walisport.module.message.databinding.ViewAppNotifyBinding

/**
 * APP消息通知
 */

class AppNotifyToastView : LinearLayout {

    companion object {
        var sportId: Int = 0
        var matchId: Long = 0L
        fun canShowToast(activity: FragmentActivity): Boolean {
            fun checkFragments(fragments: List<Fragment>): Boolean {
                for (fragment in fragments) {
                    if (fragment is Block && fragment.isResumed) return false
                    if (fragment.isAdded) {
                        if (!checkFragments(fragment.childFragmentManager.fragments)) return false
                    }
                }
                return true
            }

            return checkFragments(activity.supportFragmentManager.fragments)
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mBinding: ViewAppNotifyBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        mBinding = ViewAppNotifyBinding.inflate(layoutInflater, this, true)
    }

    fun sendNotifyMsg(msg: AppNotifyBean) {
        sportId = msg.sportId
        matchId = msg.matchId
        mBinding.tvNotifyTitle.text = msg.title
        mBinding.tvNotifyContent.text = msg.content
        if (msg.type == 1) {
            Glide.with(this).load(R.drawable.icon_message_football).into(mBinding.ivNotifyLogo)
        } else {
            Glide.with(this).load(R.drawable.icon_message_kai).into(mBinding.ivNotifyLogo)
        }
    }

    interface Block
}