package com.walisport.module.message.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.AppNotifyBean
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.bumptech.glide.Glide
import com.walisport.module.message.R
import com.walisport.module.message.databinding.FragmentAppNotifyBinding
import com.walisport.module.message.ui.viewmodel.TodayMatchViewModel
import kotlin.reflect.KClass

/**
 * APP消息通知
 */

class AppNotifyFragment : BaseFragment<TodayMatchViewModel, FragmentAppNotifyBinding>() {

    override val vbClass: KClass<FragmentAppNotifyBinding> = FragmentAppNotifyBinding::class
    override val vmClass: KClass<TodayMatchViewModel> = TodayMatchViewModel::class
    private var sportId: Int = 0
    private var matchId: Long = 0L

    companion object {
        fun newInstance(): AppNotifyFragment {
            return AppNotifyFragment()
        }
    }

    fun show(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commit()
    }

    fun showNotifyMsg(msg: AppNotifyBean?) {
        if (msg != null) {
            sportId = msg.sportId
            matchId = msg.matchId
            mBinding.tvNotifyTitle.text = msg.title
            mBinding.tvNotifyContent.text = msg.content
            if (msg.type == 1) {
                Glide.with(this).load(R.drawable.icon_message_football).into(mBinding.ivNotifyLogo)
            } else {
                Glide.with(this).load(R.drawable.icon_message_kai).into(mBinding.ivNotifyLogo)
            }
            showEnterAnimation()
        }
    }

    private fun showEnterAnimation() {
        val animator = ObjectAnimator.ofFloat(view, "translationY", -126.dp2px.toFloat(), 0f)
        animator.duration = 300
        animator.start()
    }

    private fun showExitAnimation() {
        val animator = ObjectAnimator.ofFloat(view, "translationY", 0f, -126.dp2px.toFloat())
        animator.duration = 300
        animator.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rootLayout.setOnClickListener {
            navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${matchId}&sportId=${sportId}"))
            showExitAnimation()
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}