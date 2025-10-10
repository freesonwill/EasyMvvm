package com.walisport.module.topup.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentBankCardBinding
import com.walisport.module.topup.databinding.TitleBankCardBinding
import com.walisport.module.topup.ui.adapter.BankCardAdapter
import com.walisport.module.topup.ui.viewmodel.BankCardViewModel
import kotlin.reflect.KClass

/**
 * 我的银行卡
 */

class BankCardFragment : BaseFragment<BankCardViewModel, FragmentBankCardBinding>() {

    override val vbClass: KClass<FragmentBankCardBinding> = FragmentBankCardBinding::class
    override val vmClass: KClass<BankCardViewModel> = BankCardViewModel::class
    private var editStatus: Boolean = false
    private val cardAdapter by lazy { BankCardAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        val bind = TitleBankCardBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        mBinding.titleBar.loadDynamicsTitleBar(bind.root, null)
        bind.apply {
            tvTitleName.text = R.string.bank_card.getString()
            tvTitleRight.text = R.string.bank_edit.getString()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
            tvTitleRight.clickNoRepeat {
                editStatus = !editStatus
                if (editStatus) {
                    tvTitleRight.text = R.string.bank_finish.getString()
                } else {
                    tvTitleRight.text = R.string.bank_edit.getString()
                }
                setEditable()
            }
        }
        mBinding.recyclerUser.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cardAdapter
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.layAddBank.clickNoRepeat {
            navigate(R.id.action_bankCardFragment_to_addBankCardFragment)
        }
        cardAdapter.setOnItemClickListener(object : BankCardAdapter.OnItemClickListener {
            override fun onItemClick(id: Int) {
                selectBankCard(id)
            }

            override fun onItemDelete(id: Int, bank: String, num: String) {
                showConfirmDialog(id, bank, num)
            }
        })
    }

    override fun initData() {
        super.initData()
        mViewModel.getMyBankCardList()
    }

    @SuppressLint("NotifyDataSetChanged")
    override suspend fun createObserver() {
        mViewModel.cardData.observe(viewLifecycleOwner) {
            if (it != null) {
                cardAdapter.submitList(it)
                //强制刷新解决切换账户后列表无法刷新的情况
                cardAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showConfirmDialog(id: Int, bank: String, num: String) {
        DelCardDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(idStr, id)
                putString(nameStr, bank)
                putString(numStr, num)
            }
            setOnItemClickListener(object : DelCardDialogFragment.OnClickListener {
                override fun onClickDelete(id: Int, bank: String, num: String) {
                    mViewModel.deleteBankCard(id)
                }
            })
        }.show(childFragmentManager)
    }

    private fun setEditable() {
        cardAdapter.setEditStatus(editStatus)
    }

    private fun selectBankCard(id: Int) {
        mViewModel.selectBankCard(id)
    }
}