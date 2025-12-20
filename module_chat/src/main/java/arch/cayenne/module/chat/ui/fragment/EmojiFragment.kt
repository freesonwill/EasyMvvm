package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.data.model.EmojiModel
import arch.cayenne.module.chat.databinding.FragmentEmojiLayoutBinding
import arch.cayenne.module.chat.ui.adapter.EmojiGridAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.EmojiViewModel
import arch.cayenne.module.chat.utils.EmojiScrollAlphaAnimHelper
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 23/10/25 17:19
 * @description:
 */
class EmojiFragment : BaseFragment<EmojiViewModel, FragmentEmojiLayoutBinding>() {
    override val vbClass: KClass<FragmentEmojiLayoutBinding>
        get() = FragmentEmojiLayoutBinding::class
    override val vmClass: KClass<EmojiViewModel>
        get() = EmojiViewModel::class
    private lateinit var emoJiType: EmojiTypeEnum
    private val chatViewModel by sharedViewModel<ChatHomeViewModel, ChatHomeFragment>()

    override fun initView(savedInstanceState: Bundle?) {

        val value = arguments?.getInt("type") ?: 0
        emoJiType = EmojiTypeEnum.getEnum(value)
        initRecycler()
    }

    private fun initRecycler() {

        val nAdapter = EmojiGridAdapter()
        nAdapter.updateEmojiType(emoJiType)
        nAdapter.submitList(if (emoJiType == EmojiTypeEnum.NORMAL) mViewModel.getNormalList() else mViewModel.getBidList())
        nAdapter.setRecentList(
            if (emoJiType == EmojiTypeEnum.NORMAL) mViewModel.getNormalList()
                .subList(1, 9) else mViewModel.getBidList().subList(1, 5)
        )
        nAdapter.setEmojiListener(object :RecyclerItemListener<EmojiModel>{
            override fun onItemClick(item: EmojiModel?, position: Int) {
                item?.let { chatViewModel.addEmojiToChat(it) }
            }
        })
        val spanCount = if (emoJiType == EmojiTypeEnum.NORMAL) 8 else 4
        val manager = GridLayoutManager(requireContext(), spanCount)
        manager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) spanCount else 1
            }
        }
        mBinding.recycler.apply {
            layoutManager = manager
//            isNestedScrollingEnabled = false
            adapter = nAdapter
        }
        if(emoJiType == EmojiTypeEnum.NORMAL){ //仅普通表情使用滚动渐隐动画
            val animHelper = EmojiScrollAlphaAnimHelper(mBinding.recycler)
        }
    }


    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}