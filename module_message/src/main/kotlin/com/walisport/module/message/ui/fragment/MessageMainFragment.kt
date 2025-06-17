package com.walisport.module.message.ui.fragment

import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.message.R
import com.walisport.module.message.databinding.FragmentMessageMainBinding
import com.walisport.module.message.ui.adapter.MessageAdapter
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import kotlin.reflect.KClass

/**
 * 通知消息页
 */

class MessageMainFragment : BaseFragment<MessageMainViewModel, FragmentMessageMainBinding>() {

    override val vbClass: KClass<FragmentMessageMainBinding> = FragmentMessageMainBinding::class
    override val vmClass: KClass<MessageMainViewModel> = MessageMainViewModel::class
    private var msgAdapter = MessageAdapter()

    companion object {
        const val MSG_ALL = 0
        const val MSG_SYS = 1
        const val MSG_ACT = 2
        const val MSG_MAT = 3
        const val MSG_PAY = 4
    }

    class MessageDecoration(
        private val spacing: Int = 12.dp2px,
        private val leftRight: Int = 8.dp2px,
        private val bottomSpacing: Int = 20.dp2px,
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount ?: 0
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
            outRect.left = leftRight
            outRect.right = leftRight
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.notification_message.getString(), {
                findNavController().navigateUp()
            })
            refreshLayout.setOnRefreshListener {
                mViewModel.getMessageList()
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel.getMoreMessageList()
            }
            recyclerMessage.apply {
                itemAnimator = null
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = msgAdapter
                addItemDecoration(MessageDecoration())
            }
            msgAdapter.setOnItemClickListener(object : MessageAdapter.OnClickListener {
                override fun onDelete(id: Long) {
                    showConfirmDialog(id)
                }

                override fun onDetail(id: Long) {
                }
            })
        }
    }

    override fun initListener() {
        mBinding.layMsgAll.setOnClickListener {
            selectMessageType(MSG_ALL)
        }
        mBinding.layMsgSys.setOnClickListener {
            selectMessageType(MSG_SYS)
        }
        mBinding.layMsgAct.setOnClickListener {
            selectMessageType(MSG_ACT)
        }
        mBinding.layMsgMatch.setOnClickListener {
            selectMessageType(MSG_MAT)
        }
        mBinding.layMsgPay.setOnClickListener {
            selectMessageType(MSG_PAY)
        }
        selectMessageType(MSG_ALL)
    }

    override fun createObserver() {
        mViewModel.notificationBean.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            it?.let {
                msgAdapter.submitList(it)
            }
        }
        mViewModel.notificationSelect.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            it?.let {
                msgAdapter.submitList(it)
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMessageList()
    }

    private fun selectMessageType(type: Int) {
        mViewModel.selectMessage(type)
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

    private fun showConfirmDialog(id: Long) {
        CommonDialog.newInstance(
            "",
            getString(R.string.notification_delete),
            getString(R.string.notification_confirm),
            getString(R.string.notification_cancel),
        ).also {
            it.setOnOkClickListener {
                mViewModel.deleteMessage(id)
            }
            it.show(childFragmentManager)
        }
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}