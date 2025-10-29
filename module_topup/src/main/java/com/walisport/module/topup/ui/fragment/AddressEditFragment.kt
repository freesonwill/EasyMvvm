package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.fragment.CommonBottomDialog
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentAddressEditBinding
import com.walisport.module.topup.databinding.TitleAddressEditBinding
import com.walisport.module.topup.databinding.TitleAddressNoteBinding
import com.walisport.module.topup.ui.viewmodel.AddressViewModel
import kotlin.reflect.KClass

/**
 * 编辑地址页面
 */

class AddressEditFragment : BaseFragment<AddressViewModel, FragmentAddressEditBinding>() {

    override val vbClass: KClass<FragmentAddressEditBinding> = FragmentAddressEditBinding::class
    override val vmClass: KClass<AddressViewModel> = AddressViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val coinType = arguments?.getString("coinType") ?: ""
        val address = arguments?.getString("address") ?: ""
        val remark = arguments?.getString("remark") ?: ""
        val addressType = arguments?.getString("addressType") ?: ""
        with(mBinding) {
            val bind = TitleAddressEditBinding.inflate(LayoutInflater.from(context), root, false)
            titleBar.loadDynamicsTitleBar(bind.root, null)
            bind.apply {
                tvTitle.text = R.string.edit_addr.getString()
                tvSave.text = R.string.save_addr.getString()
                tvBack.clickNoRepeat {
                    findNavController().navigateUp()
                }
                tvSave.clickNoRepeat {

                }
            }
            tvCoin.text = coinType
            edtAddress.setText(address)
            edtRemark.setText(remark)
            tvNetworkType.text = addressType
            btnDel.clickNoRepeat {
                showDelConfirmDialog(address)
            }
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun showDelConfirmDialog(address: String) {
        CommonBottomDialog.newInstance(
            getString(R.string.tip_del_address),
            0
        ).also {
            it.setOnItemClickListener(object : CommonBottomDialog.OnClickListener {
                override fun onClick(id: Long) {
                    mViewModel.deleteAddress(address)
                }
            })
            it.show(childFragmentManager)
        }
    }
}