package com.walisport.module.message.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.ViewPagerAnimHelper
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

    private val viewPagerAnimHelper by lazy {
        ViewPagerAnimHelper()
    }

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
    }

    override fun initListener() {
        mBinding.layMsgAll.addScaleOnTouchAnimation()
        mBinding.layMsgAll.clickNoRepeat {
            selectMessageType(MSG_ALL)
        }
        mBinding.layMsgSys.addScaleOnTouchAnimation()
        mBinding.layMsgSys.clickNoRepeat {
            selectMessageType(MSG_SYS)
        }
        mBinding.layMsgAct.addScaleOnTouchAnimation()
        mBinding.layMsgAct.clickNoRepeat {
            selectMessageType(MSG_ACT)
        }
        mBinding.layMsgMatch.addScaleOnTouchAnimation()
        mBinding.layMsgMatch.clickNoRepeat {
            selectMessageType(MSG_MAT)
        }
        mBinding.layMsgPay.addScaleOnTouchAnimation()
        mBinding.layMsgPay.clickNoRepeat {
            selectMessageType(MSG_PAY)
        }
        selectMessageType(MSG_ALL)
    }

    override fun createObserver() {
    }

    private fun selectMessageType(type: Int) {
        //ViewPager切换动画
        viewPagerAnimHelper.doViewPagerAnim(
            targetPosition = type,
            viewPager = mBinding.vpMessage,
            fakeViewPager = mBinding.fragmentFakeViewPager,
        )
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
                mBinding.tvMsgMat.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }
    }
}