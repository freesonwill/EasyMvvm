package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.fragment.CommonBottomDialog
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.data.entity.AddressBean
import com.walisport.module.topup.databinding.FragmentAddressBinding
import com.walisport.module.topup.databinding.TitleAddressNoteBinding
import com.walisport.module.topup.ui.adapter.AddressAdapter
import com.walisport.module.topup.ui.viewmodel.AddressViewModel
import com.walisport.module.topup.ui.widget.CopyEditDialog
import kotlin.reflect.KClass

/**
 * 地址本页面
 */
class AddressNoteFragment : BaseFragment<AddressViewModel, FragmentAddressBinding>() {

    override val vbClass: KClass<FragmentAddressBinding> = FragmentAddressBinding::class
    override val vmClass: KClass<AddressViewModel> = AddressViewModel::class
    private val addressAdapter by lazy { AddressAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val bind = TitleAddressNoteBinding.inflate(LayoutInflater.from(context), root, false)
            titleBar.loadDynamicsTitleBar(bind.root, null)
            bind.apply {
                tvTitle.text = R.string.address_note.getString()
                ivBack.clickNoRepeat {
                    findNavController().navigateUp()
                }
                ivRight.clickNoRepeat {

                }
            }
        }
        mBinding.recyclerAddress.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = addressAdapter
        }
        mBinding.root.touchBackPressed()
    }

    override fun initData() {
        super.initData()
        mViewModel.getAddressList()
    }

    override fun initListener() {
        addressAdapter.setOnItemClickListener(object : AddressAdapter.OnItemClickListener {
            override fun onItemClick(bean: AddressBean) {
                showCopyEditDialog(bean)
            }
        })
    }

    override suspend fun createObserver() {
        mViewModel.addressData.observe(viewLifecycleOwner) {
            if (it != null) {
                addressAdapter.submitList(it)
            }
        }
    }

    private fun showCopyEditDialog(bean: AddressBean) {
        CopyEditDialog.newInstance(bean).also {
            it.setOnItemClickListener(object : CopyEditDialog.OnItemClickListener {
                override fun onCopy() {
                    copyToClipboard(bean.address) {
                        showToast(R.string.tip_copy_suc.getString())
                    }
                }

                override fun onEdit() {
                    it.dismiss()
                    val args = Bundle()
                    args.putString("address", bean.address)
                    args.putString("remark", bean.remark)
                    args.putString("coinType", bean.coinType)
                    args.putString("addressType", bean.addressType)
                    navigate(R.id.action_addressFragment_to_addressEditFragment, args)
                }
            })
            it.show(childFragmentManager)
        }
    }
}