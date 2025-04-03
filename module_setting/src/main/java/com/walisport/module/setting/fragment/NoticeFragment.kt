package com.walisport.module.setting.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentNoticeBinding
import com.walisport.module.setting.data.NoticeViewModel
import com.walisport.module.setting.dialog.MatchNoticeDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 通知设置
 */

class NoticeFragment : BaseFragment<NoticeViewModel, FragmentNoticeBinding>() {

    override val mBinding: FragmentNoticeBinding by viewBind()
    override val mViewModel: NoticeViewModel by viewModel()

    companion object {
        const val TYPE_SYS_GOAL = 0    //系统通知-进球
        const val TYPE_SYS_MATCH = 1   //系统通知-开赛
        const val TYPE_APP_GOAL = 2    //应用内通知-进球
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_notice_set.getString()) {
            findNavController().navigateUp()
        }
    }

    override fun initListener() {
        mBinding.noticeGoal.setOnClickListener {
            showMatchNoticeDialog(TYPE_SYS_GOAL)
        }
        mBinding.noticeStart.setOnClickListener {
            showMatchNoticeDialog(TYPE_SYS_MATCH)
        }
        mBinding.noticeAppGoal.setOnClickListener {
            showMatchNoticeDialog(TYPE_APP_GOAL)
        }
    }

    override fun createObserver() {
    }

    private fun showMatchNoticeDialog(type: Int) {
        val fragmentManager = requireActivity().supportFragmentManager
        MatchNoticeDialog().apply {
            setOnItemClickListener(object : MatchNoticeDialog.OnClickListener {
                override fun onClickBet(isChecked: Boolean) {
                }

                override fun onClickFav(isChecked: Boolean) {
                }

                override fun onClickAll(isChecked: Boolean) {
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