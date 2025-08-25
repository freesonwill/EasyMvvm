package com.walisport.module.message.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.message.R
import com.walisport.module.message.data.NotificationBean
import com.walisport.module.message.databinding.FragmentMessageListBinding
import com.walisport.module.message.ui.adapter.MessageAdapter
import com.walisport.module.message.ui.view.DeleteAnimator
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.reflect.KClass

class MessageListFragment : BaseFragment<MessageMainViewModel, FragmentMessageListBinding>() {

    override val vbClass: KClass<FragmentMessageListBinding> = FragmentMessageListBinding::class
    override val vmClass: KClass<MessageMainViewModel> = MessageMainViewModel::class
    private var msgAdapter = MessageAdapter()
    private var msgType = 0

    override fun initView(savedInstanceState: Bundle?) {
        msgType = arguments?.getInt(MSG_TYPE) ?: 0
        with(mBinding) {
            refreshLayout.setOnRefreshListener {
                mViewModel.getMessageList(msgType)
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel.getMoreMessageList()
            }
            recyclerMessage.apply {
                itemAnimator = DeleteAnimator()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = msgAdapter
            }
            msgAdapter.setOnItemClickListener(object : MessageAdapter.OnClickListener {
                override fun onDelete(id: Long) {
                    showConfirmDialog(id)
                }

                override fun onDetail(item: NotificationBean) {
                    val content = getHtmlText(item.content)
                    val url = getImageUrl(item.content)
                    val time = getTime(item.createTime)
                    navigate(MessageMainFragmentDirections.actionMessageMainFragmentToMessageDetailFragment()
                        .apply {
                            arguments.putString("title", item.title)
                            arguments.putInt("type", item.type)
                            arguments.putString("time", time)
                            arguments.putString("content", content)
                            arguments.putString("url", url)
                        })
                    mViewModel.setMessageRead(item.id)
                }
            })
        }
    }

    override fun initListener() {
    }

    override fun initData() {
        super.initData()
        if (msgType == MSG_ALL) {
            mViewModel.getMessageList(MSG_ALL)
            mBinding.loadingView.visibility = View.VISIBLE
        }
    }

    override suspend fun createObserver() {
        mViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            when (state) {
                DataState.NetworkUnavailable -> {
                    mBinding.loadingView.visibility = View.GONE
                    if (msgAdapter.itemCount == 0) {
                        mBinding.emptyState.visibility = View.VISIBLE
                        mBinding.emptyState.setState(
                            States.NETWORK_ANOMALY(),
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                    }
                }
            }
        }
        mViewModel.notificationBean.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            mBinding.loadingView.visibility = View.GONE
            it.let {
                if (it.isEmpty()) {
                    msgAdapter.notifyData(it)
                    mBinding.emptyState.visibility = View.VISIBLE
                    mBinding.emptyState.setState(
                        States.DATA_EMPTY,
                        arch.cayenne.lib.common.R.string.data_empty.getString()
                    )
                } else {
                    mBinding.emptyState.visibility = View.GONE
                    if (msgType == MSG_ALL) {
                        msgAdapter.notifyData(it)
                    } else {
                        val temp = it.filter { res -> res.type == msgType }
                        if (temp.isEmpty()) {
                            mBinding.emptyState.visibility = View.VISIBLE
                            mBinding.emptyState.setState(
                                States.DATA_EMPTY,
                                arch.cayenne.lib.common.R.string.data_empty.getString()
                            )
                        }
                        msgAdapter.notifyData(temp)
                    }
                }
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

    private fun getHtmlText(html: String): String {
        var content = ""
        val pattern: Pattern = Pattern.compile("<p>(.*?)</p>")
        val matcher: Matcher = pattern.matcher(html)
        while (matcher.find()) {
            content = matcher.group(1)?.toString() ?: ""
        }
        return content
    }

    private fun getImageUrl(html: String): String {
        var url = ""
        val pattern: Pattern = Pattern.compile("<img\\s+[^>]*?src\\s*=\\s*([\"'])(.*?)([\"'])")
        val matcher: Matcher = pattern.matcher(html)
        while (matcher.find()) {
            url = matcher.group(1)?.toString() ?: ""
        }
        return url
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MM-dd HH:mm")
        return sdf.format(date)
    }

    companion object {
        private const val MSG_TYPE = "message_type"
        private const val MSG_ALL = 0
        fun newInstance(mType: Int): MessageListFragment {
            return MessageListFragment().apply {
                arguments = Bundle().apply {
                    putInt(MSG_TYPE, mType)
                }
            }
        }
    }
}