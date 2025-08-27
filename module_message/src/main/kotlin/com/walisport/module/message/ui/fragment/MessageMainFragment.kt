package com.walisport.module.message.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.walisport.module.message.R
import com.walisport.module.message.databinding.FragmentMessageMainBinding
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 * 通知消息页
 */

class MessageMainFragment : BaseFragment<MessageMainViewModel, FragmentMessageMainBinding>() {

    override val vbClass: KClass<FragmentMessageMainBinding> = FragmentMessageMainBinding::class
    override val vmClass: KClass<MessageMainViewModel> = MessageMainViewModel::class
    private var historyTypePosition: Int = -1

    companion object {
        const val MSG_ALL = 0
        const val MSG_SYS = 1
        const val MSG_ACT = 2
        const val MSG_MAT = 3
        const val MSG_PAY = 4
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.notification_message, {
                findNavController().navigateUp()
            })
            val list = listOf(
                PagerBean("") { MessageListFragment.newInstance(MSG_ALL) },
                PagerBean("") { MessageListFragment.newInstance(MSG_SYS) },
                PagerBean("") { MessageListFragment.newInstance(MSG_ACT) },
                PagerBean("") { MessageListFragment.newInstance(MSG_MAT) },
                PagerBean("") { MessageListFragment.newInstance(MSG_PAY) },
            )
            vpMessage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
        }
        mBinding.root.touchBackPressed()
    }

    override fun onFragmentAnimEnd(isEnter: Boolean) {
        super.onFragmentAnimEnd(isEnter)
        if (isEnter) {
            mBinding.vpMessage.offscreenPageLimit = 5
        }
    }

    override fun initListener() {
        mBinding.layMsgAll.addScaleOnTouchAnimation()
        mBinding.layMsgAll.clickNoRepeat {
            select(MSG_ALL, historyTypePosition)
        }
        mBinding.layMsgSys.addScaleOnTouchAnimation()
        mBinding.layMsgSys.clickNoRepeat {
            select(MSG_SYS, historyTypePosition)
        }
        mBinding.layMsgAct.addScaleOnTouchAnimation()
        mBinding.layMsgAct.clickNoRepeat {
            select(MSG_ACT, historyTypePosition)
        }
        mBinding.layMsgMatch.addScaleOnTouchAnimation()
        mBinding.layMsgMatch.clickNoRepeat {
            select(MSG_MAT, historyTypePosition)
        }
        mBinding.layMsgPay.addScaleOnTouchAnimation()
        mBinding.layMsgPay.clickNoRepeat {
            select(MSG_PAY, historyTypePosition)
        }
        select(MSG_ALL, historyTypePosition)

        mBinding.vpMessage.setupViewPagerScroll {
            select(it, historyTypePosition, true)
        }
    }

    //未读消息红点显示
    override suspend fun createObserver() {
        mViewModel.allUnreadMsg.observe(viewLifecycleOwner) {
            it.let {
                if (it > 0) {
                    mBinding.ivMsgAllUnread.visibility = View.VISIBLE
                } else {
                    mBinding.ivMsgAllUnread.visibility = View.INVISIBLE
                }
            }
        }
        mViewModel.sysUnreadMsg.observe(viewLifecycleOwner) {
            it.let {
                if (it > 0) {
                    mBinding.ivMsgSysUnread.visibility = View.VISIBLE
                } else {
                    mBinding.ivMsgSysUnread.visibility = View.INVISIBLE
                }
            }
        }
        mViewModel.actUnreadMsg.observe(viewLifecycleOwner) {
            it.let {
                if (it > 0) {
                    mBinding.ivMsgActUnread.visibility = View.VISIBLE
                } else {
                    mBinding.ivMsgActUnread.visibility = View.INVISIBLE
                }
            }
        }
        mViewModel.matUnreadMsg.observe(viewLifecycleOwner) {
            it.let {
                if (it > 0) {
                    mBinding.ivMsgMatUnread.visibility = View.VISIBLE
                } else {
                    mBinding.ivMsgMatUnread.visibility = View.INVISIBLE
                }
            }
        }
        mViewModel.payUnreadMsg.observe(viewLifecycleOwner) {
            it.let {
                if (it > 0) {
                    mBinding.ivMsgPayUnread.visibility = View.VISIBLE
                } else {
                    mBinding.ivMsgPayUnread.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun select(type: Int, historyType: Int, skipAnyAnim: Boolean = false) {
        if (type != historyType) {
            if (!skipAnyAnim) mBinding.vpMessage.setCurrentItem(type, false)
            selectMessageType(type, true)
            selectMessageType(historyType, false)
            historyTypePosition = type
        }
    }

    private fun selectMessageType(type: Int, select: Boolean) {
        var textView: SkinnableTextView? = null
        when (type) {
            MSG_ALL -> {
                mBinding.layMsgAll.isSelected = select
                textView = mBinding.tvMsgAll
            }

            MSG_SYS -> {
                mBinding.layMsgSys.isSelected = select
                textView = mBinding.tvMsgSys
            }

            MSG_ACT -> {
                mBinding.layMsgAct.isSelected = select
                textView = mBinding.tvMsgAct
            }

            MSG_MAT -> {
                mBinding.layMsgMatch.isSelected = select
                textView = mBinding.tvMsgMat
            }

            MSG_PAY -> {
                mBinding.layMsgPay.isSelected = select
                textView = mBinding.tvMsgPay
            }
        }
        textView?.typeface = if (select) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }
}