package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.databinding.FragmentCustomerFragmentBinding
import arch.cayenne.module.chat.ui.adapter.ChatCustomAdapter
import arch.cayenne.module.chat.ui.viewmodel.CustomerViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 7/10/25 11:13
 * @description: 客服直播
 */
class CustomerFragment :BaseFragment<CustomerViewModel,FragmentCustomerFragmentBinding>(){
    override val vbClass: KClass<FragmentCustomerFragmentBinding>
        get() = FragmentCustomerFragmentBinding::class
    override val vmClass: KClass<CustomerViewModel>
        get() = CustomerViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            val nAdapter = ChatCustomAdapter()
            nAdapter.submitList(arrayListOf(1,2,3))
            adapter = nAdapter
        }

    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}