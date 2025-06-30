package com.walisport.module.message.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
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
    private var clicklistener: OnClickListener? = null
    private var sportId: Int = 0
    private var matchId: Long = 0L

    companion object {
        fun newInstance(): AppNotifyFragment {
            return AppNotifyFragment()
        }
        private const val OPEN_NOTIFY = 1
        private const val CLOSE_NOTIFY = 2
        private const val CLICK_EVENT = 3
        private const val TOUCH_EVENT = 4
        private const val CLOSE_TIMEOUT = 3000L
    }

    fun show(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commit()
    }

    private fun showNotifyMsg(msg: AppNotifyBean) {
        sportId = msg.sportId
        matchId = msg.matchId
        mBinding.rootLayout.visibility = View.VISIBLE
        mBinding.tvNotifyTitle.text = msg.title
        mBinding.tvNotifyContent.text = msg.content
        if (msg.type == 1) {
            Glide.with(this).load(R.drawable.icon_message_football).into(mBinding.ivNotifyLogo)
        } else {
            Glide.with(this).load(R.drawable.icon_message_kai).into(mBinding.ivNotifyLogo)
        }
        showEnterAnimation()
    }

    fun sendNotifyMsg(msg: AppNotifyBean) {
        showNotifyMsg(msg)
        mBinding.root.postDelayed({
            showExitAnimation()
        }, CLOSE_TIMEOUT)
    }

    private fun showEnterAnimation() {
        offsetY = 0f
        val animator = ObjectAnimator.ofFloat(view, "translationY", -126.dp2px.toFloat(), 0f)
        animator.duration = 300
        animator.start()
    }

    private fun showExitAnimation() {
        val animator = ObjectAnimator.ofFloat(view, "translationY", offsetY, -126.dp2px.toFloat() + offsetY)
        animator.duration = 300
        animator.start()
    }

    private fun gotoMatchLive() {
        navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${matchId}&sportId=${sportId}"))
    }

    private var startY: Float = 0f
    private var offsetY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        setOnItemClickListener(
            object : OnClickListener {
                override fun onDown() {

                }

                override fun onTouch() {
                    showExitAnimation()
                }

                override fun onClick() {
                    showEnterAnimation()
                    gotoMatchLive()
                }
            }
        )
        mBinding.rootLayout.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = e.rawY
                    clicklistener?.onDown()
                    true
                }

                //点击事件弹窗消失并跳转到直播详情页，触摸事件弹窗随手指移动后消失，不跳转直播详情页
                MotionEvent.ACTION_UP -> {
                    val off = e.rawY - startY
                    if (off < 10) {
                        clicklistener?.onClick()
                    } else {
                        clicklistener?.onTouch()
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    offsetY = e.rawY - startY
                    if (offsetY > 40) {
                        offsetY = 40f
                    }
                    mBinding.rootLayout.translationY = offsetY
                    true
                }

                else -> false
            }
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }

    private fun setOnItemClickListener(listener: OnClickListener) {
        this.clicklistener = listener
    }

    interface OnClickListener {
        fun onTouch()
        fun onClick()
        fun onDown()
    }
}