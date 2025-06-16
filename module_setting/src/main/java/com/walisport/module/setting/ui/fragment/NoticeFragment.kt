package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentNoticeBinding
import com.walisport.module.setting.ui.dialog.MatchNoticeDialog
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 通知设置
 */

class NoticeFragment : BaseFragment<SettingViewModel, FragmentNoticeBinding>() {

    override val vbClass: KClass<FragmentNoticeBinding> = FragmentNoticeBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    companion object {
        const val TYPE_SYS_GOAL = 0     //系统通知-进球
        const val TYPE_SYS_MATCH = 1    //系统通知-开赛
        const val TYPE_APP_GOAL = 2     //应用内通知-进球
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_notice_set.getString(),
            { findNavController().navigateUp() })
        if (mViewModel.getSystemAllOrPart()) {
            mBinding.tvNoticeSysGoal.text = getString(R.string.menu_all)
        } else {
            mBinding.tvNoticeSysGoal.text = getString(R.string.menu_part)
        }
        if (mViewModel.getKickAllOrPart()) {
            mBinding.tvNoticeSysStart.text = getString(R.string.menu_all)
        } else {
            mBinding.tvNoticeSysStart.text = getString(R.string.menu_part)
        }
        if (mViewModel.getAppAllOrPart()) {
            mBinding.tvNoticeGoalApp.text = getString(R.string.menu_all)
        } else {
            mBinding.tvNoticeGoalApp.text = getString(R.string.menu_part)
        }
    }

    override fun initListener() {
        mBinding.noticeGoal.clickNoRepeat {
            val bet = mViewModel.getSystemBet()
            val fav = mViewModel.getSystemFav()
            val all = mViewModel.getSystemAll()
            showMatchNoticeDialog(TYPE_SYS_GOAL, bet, fav, all)
        }
        mBinding.noticeStart.clickNoRepeat {
            val bet = mViewModel.getKickBet()
            val fav = mViewModel.getKickFav()
            val all = mViewModel.getKickAll()
            showMatchNoticeDialog(TYPE_SYS_MATCH, bet, fav, all)
        }
        mBinding.noticeAppGoal.clickNoRepeat {
            val bet = mViewModel.getAppBet()
            val fav = mViewModel.getAppFav()
            val all = mViewModel.getAppAll()
            showMatchNoticeDialog(TYPE_APP_GOAL, bet, fav, all)
        }
    }

    override fun createObserver() {

    }

    fun setToggleValue(type: Int, bet: Boolean, fav: Boolean, all: Boolean) {
        when (type) {
            TYPE_SYS_GOAL -> {
                mViewModel.setSystemGoal(bet, fav, all)
                if (bet && fav && all) {
                    mBinding.tvNoticeSysGoal.text = getString(R.string.menu_all)
                } else {
                    mBinding.tvNoticeSysGoal.text = getString(R.string.menu_part)
                }
            }

            TYPE_SYS_MATCH -> {
                mViewModel.setKickGoal(bet, fav, all)
                if (bet && fav && all) {
                    mBinding.tvNoticeSysStart.text = getString(R.string.menu_all)
                } else {
                    mBinding.tvNoticeSysStart.text = getString(R.string.menu_part)
                }
            }

            TYPE_APP_GOAL -> {
                mViewModel.setAppGoal(bet, fav, all)
                if (bet && fav && all) {
                    mBinding.tvNoticeGoalApp.text = getString(R.string.menu_all)
                } else {
                    mBinding.tvNoticeGoalApp.text = getString(R.string.menu_part)
                }
            }
        }
    }

    private fun showMatchNoticeDialog(type: Int, bet: Boolean, fav: Boolean, all: Boolean) {
        var betBool: Boolean = bet
        var favBool: Boolean = fav
        var allBool: Boolean = all
        val fragmentManager = requireActivity().supportFragmentManager
        MatchNoticeDialog().apply {
            arguments = Bundle().apply {
                putBoolean(betValue, bet)
                putBoolean(favValue, fav)
                putBoolean(allValue, all)
            }
            setOnItemClickListener(object : MatchNoticeDialog.OnClickListener {
                override fun onClickBet(isChecked: Boolean) {
                    betBool = isChecked
                    setToggleValue(type, betBool, favBool, allBool)
                }

                override fun onClickFav(isChecked: Boolean) {
                    favBool = isChecked
                    setToggleValue(type, betBool, favBool, allBool)
                }

                override fun onClickAll(isChecked: Boolean) {
                    allBool = isChecked
                    setToggleValue(type, betBool, favBool, allBool)
                }

                override fun onClickClose() {
                    mBinding.titleBar.postDelayed({
                        dialog?.dismiss()
                    }, 300)
                }
            })
        }.show(fragmentManager)
    }
}