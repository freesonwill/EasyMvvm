package arch.cayenne.module.chat.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.model.ChatPersonalData
import arch.cayenne.module.chat.databinding.FragmentChatReportLayoutBinding
import arch.cayenne.module.chat.ui.adapter.ChatPersonalAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatReportViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 4/11/25 14:37
 * @description:
 */
class ChatReportFragment :
    BasePreLoadBottomSheetFragment<ChatReportViewModel, FragmentChatReportLayoutBinding>() {

    companion object {
        val TAG = ChatReportFragment::class.java.simpleName

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                ChatReportFragment().customAttach(fragment, TAG)
            }
        }

        fun show(fragment: Fragment,user:ChatRefUser?,chatType: ChatType) {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? ChatReportFragment
            f?.setReportArgument(user,chatType)
            f?.customShow()
        }
    }

    override val vbClass: KClass<FragmentChatReportLayoutBinding>
        get() = FragmentChatReportLayoutBinding::class
    override val vmClass: KClass<ChatReportViewModel>
        get() = ChatReportViewModel::class

    private var chatUser:ChatRefUser? = null
    private var chatTYpe:ChatType = ChatType.LOBBY

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            setupFullScreen(dialog)
        }

        return dialog
    }

    fun setReportArgument(user:ChatRefUser?,chatType: ChatType){
        chatUser = user
        this.chatTYpe = chatType
    }

    private fun setupFullScreen(dialog: Dialog) {
        dialog.window?.apply {
            // 设置窗口标志
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            dialog.window?.attributes?.height = 398.dp2px
//
//            // 设置透明背景
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        lifecycleScope.launch {
            mViewModel.reportUserFlow.collect{
                if(it?.code == 0){
                    showToast(R.string.chat_report_success.getString())
                }else{
                    showToast(R.string.chat_report_fail.getString())
                }
                dismiss()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.minHeight = 432.dp2px
        mBinding.recycler.apply {
            val nAdapter = ChatPersonalAdapter()
            nAdapter.setRecyclerItemListener(object : RecyclerItemListener<ChatPersonalData> {
                override fun onItemClick(item: ChatPersonalData?, position: Int) {
//                    report(position)
                    showToast(R.string.chat_report_success.getString())
                    dismiss()
                }
            })
            val list = arrayListOf(
                ChatPersonalData("色情低俗"),
                ChatPersonalData("非法广告"),
                ChatPersonalData("辱骂我或他人"),
                ChatPersonalData("违法违规"),
                ChatPersonalData("涉嫌诈骗"),
                ChatPersonalData("其他"),
            )
            nAdapter.submitList(list)
            mBinding.recycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = nAdapter
            }

        }
    }

    private fun report(position:Int){
        // 举报类型 1.色情低俗 2.非法广告 3.辱骂他人 4.违法违规 5.涉嫌诈骗 6.其他
     val type = position + 1
        chatUser?.let {
            mViewModel.reportOther(it.uid,chatTYpe,type)
        }
    }

    override fun initListener() {

        mBinding.apply {

            tvCancel.setOnClickListener {
                dismiss()
            }
        }

    }
}