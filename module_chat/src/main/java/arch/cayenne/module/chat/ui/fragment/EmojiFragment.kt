package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.databinding.FragmentEmojiLayoutBinding
import arch.cayenne.module.chat.ui.adapter.EmojiGridAdapter
import arch.cayenne.module.chat.ui.viewmodel.EmojiViewModel
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

    override fun initView(savedInstanceState: Bundle?) {

        val value = arguments?.getInt("type") ?: 0
        emoJiType = EmojiTypeEnum.getEnum(value)
        initRecycler()
    }

    private fun initRecycler() {
        val manager = object :LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        mBinding.recycler.apply {
            layoutManager = manager
//            isNestedScrollingEnabled = false
            val nAdapter = EmojiGridAdapter()
            nAdapter.updateEmojiType(emoJiType)
            nAdapter.submitList(if (emoJiType == EmojiTypeEnum.NORMAL) mViewModel.getNormalList() else mViewModel.getBidList())
            adapter = nAdapter

        }
    }


    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}