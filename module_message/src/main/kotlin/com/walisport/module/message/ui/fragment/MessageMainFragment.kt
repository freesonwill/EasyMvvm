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
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.message.R
import com.walisport.module.message.data.NotificationBean
import com.walisport.module.message.databinding.FragmentMessageMainBinding
import com.walisport.module.message.ui.adapter.MessageAdapter
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern
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
            titleBar.loadGeneralTitleBar(R.string.notification_message, {
                findNavController().navigateUp()
            })
            refreshLayout.setOnRefreshListener {
                mViewModel.getMessageList()
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel.getMoreMessageList()
            }
            recyclerMessage.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = msgAdapter
                for (i in 0 until itemDecorationCount) {
                    removeItemDecorationAt(i)
                }
                addItemDecoration(MessageDecoration())
            }
            msgAdapter.setOnItemClickListener(object : MessageAdapter.OnClickListener {
                override fun onDelete(id: Long) {
                    showConfirmDialog(id)
                }

                override fun onDetail(item: NotificationBean) {
                    var content = ""
                    var url = ""
                    //提取文本
                    val pattern: Pattern = Pattern.compile("<p>(.*?)</p>")
                    val matcher: Matcher = pattern.matcher(item.content)
                    while (matcher.find()) {
                        content = matcher.group(1)?.toString() ?: ""
                    }
                    //提取图片
                    val patternImg = Pattern.compile("<url>(.*?)</url>")
                    val matcherImg = patternImg.matcher(item.content)
                    while (matcherImg.find()) {
                        url = matcherImg.group(1)?.toString() ?: ""
                    }
                    navigate(
                        MessageMainFragmentDirections.actionMessageMainFragmentToMessageDetailFragment()
                            .apply {
                                arguments.putString("content", content)
                                arguments.putString("url", url)
                            })
                    mViewModel.setMessageRead(item.id)
                }
            })
        }
    }

    override fun initListener() {
        mBinding.layMsgAll.addScaleOnTouchAnimation(mBinding.ivMsgAll)
        mBinding.layMsgAll.clickNoRepeat {
            selectMessageType(MSG_ALL)
        }
        mBinding.layMsgSys.addScaleOnTouchAnimation(mBinding.ivMsgSys)
        mBinding.layMsgSys.clickNoRepeat {
            selectMessageType(MSG_SYS)
        }
        mBinding.layMsgAct.addScaleOnTouchAnimation(mBinding.ivMsgAct)
        mBinding.layMsgAct.clickNoRepeat {
            selectMessageType(MSG_ACT)
        }
        mBinding.layMsgMatch.addScaleOnTouchAnimation(mBinding.ivMsgMat)
        mBinding.layMsgMatch.clickNoRepeat {
            selectMessageType(MSG_MAT)
        }
        mBinding.layMsgPay.addScaleOnTouchAnimation(mBinding.ivMsgPay)
        mBinding.layMsgPay.clickNoRepeat {
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
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}