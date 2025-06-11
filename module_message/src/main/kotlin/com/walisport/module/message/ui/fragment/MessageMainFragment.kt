package com.walisport.module.message.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
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

    class BigSmallItemDecoration(
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
            recyclerMessage.apply {
                itemAnimator = null
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = msgAdapter
                addItemDecoration(BigSmallItemDecoration())
            }
            msgAdapter.setOnItemClickListener(object : MessageAdapter.OnClickListener {
                override fun onDelete(position: Int) {

                }
            })
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.notificationBean.observe(viewLifecycleOwner) {
            it?.let {
                msgAdapter.submitList(it)
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMessageData()
    }
}