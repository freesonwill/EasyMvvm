package com.walisport.module.message.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.doSmartAnim
import com.walisport.module.message.R
import com.walisport.module.message.databinding.FragmentMessageMainBinding
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import kotlin.reflect.KClass

/**
 * 通知消息页
 */

class MessageMainFragment : BaseFragment<MessageMainViewModel, FragmentMessageMainBinding>() {

    override val vbClass: KClass<FragmentMessageMainBinding> = FragmentMessageMainBinding::class
    override val vmClass: KClass<MessageMainViewModel> = MessageMainViewModel::class

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
            vpMessage.offscreenPageLimit = list.size
            vpMessage.adapter = PagerAdapter(childFragmentManager, lifecycle, list)
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.layMsgAll.addScaleOnTouchAnimation()
        mBinding.layMsgAll.clickNoRepeat {
            selectMessageType(MSG_ALL, true)
        }
        mBinding.layMsgSys.addScaleOnTouchAnimation()
        mBinding.layMsgSys.clickNoRepeat {
            selectMessageType(MSG_SYS, true)
        }
        mBinding.layMsgAct.addScaleOnTouchAnimation()
        mBinding.layMsgAct.clickNoRepeat {
            selectMessageType(MSG_ACT, true)
        }
        mBinding.layMsgMatch.addScaleOnTouchAnimation()
        mBinding.layMsgMatch.clickNoRepeat {
            selectMessageType(MSG_MAT, true)
        }
        mBinding.layMsgPay.addScaleOnTouchAnimation()
        mBinding.layMsgPay.clickNoRepeat {
            selectMessageType(MSG_PAY, true)
        }
        selectMessageType(MSG_ALL, false)
        mBinding.vpMessage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                selectMessageType(position, false)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        mBinding.root.touchBackPressed()
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

    private fun selectMessageType(type: Int, anim: Boolean) {
        //ViewPager切换动画
        if (anim) {
            mBinding.vpMessage.doSmartAnim(targetPosition = type)
        }
        mBinding.ivMsgAll.isSelected = false
        mBinding.ivMsgSys.isSelected = false
        mBinding.ivMsgAct.isSelected = false
        mBinding.ivMsgMat.isSelected = false
        mBinding.ivMsgPay.isSelected = false
        mBinding.tvMsgAll.isSelected = false
        mBinding.tvMsgSys.isSelected = false
        mBinding.tvMsgAct.isSelected = false
        mBinding.tvMsgMat.isSelected = false
        mBinding.tvMsgPay.isSelected = false
        when (type) {
            MSG_ALL -> {
                mBinding.ivMsgAll.isSelected = true
                mBinding.tvMsgAll.isSelected = true
                mBinding.tvMsgAll.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            MSG_SYS -> {
                mBinding.ivMsgSys.isSelected = true
                mBinding.tvMsgSys.isSelected = true
                mBinding.tvMsgSys.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            MSG_ACT -> {
                mBinding.ivMsgAct.isSelected = true
                mBinding.tvMsgAct.isSelected = true
                mBinding.tvMsgAct.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            MSG_MAT -> {
                mBinding.ivMsgMat.isSelected = true
                mBinding.tvMsgMat.isSelected = true
                mBinding.tvMsgMat.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            MSG_PAY -> {
                mBinding.tvMsgPay.isSelected = true
                mBinding.ivMsgPay.isSelected = true
                mBinding.tvMsgPay.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }
    }
}