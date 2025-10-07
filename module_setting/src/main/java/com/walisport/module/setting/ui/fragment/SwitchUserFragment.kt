package com.walisport.module.setting.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentSwitchBinding
import com.walisport.module.setting.ui.adapter.UserAdapter
import com.walisport.module.setting.ui.viewmodel.UserViewModel
import kotlin.reflect.KClass

/**
 * 切换账户页面
 */

class SwitchUserFragment : BaseFragment<UserViewModel, FragmentSwitchBinding>() {

    override val vbClass: KClass<FragmentSwitchBinding> = FragmentSwitchBinding::class
    override val vmClass: KClass<UserViewModel> = UserViewModel::class
    private val userAdapter by lazy { UserAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(
            R.string.change_user,
            { findNavController().navigateUp() },
            { setEditStatus(true) },
            R.string.edit.getString()
        )
        mBinding.recyclerUser.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = userAdapter
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        userAdapter.setOnItemClickListener(object : UserAdapter.OnItemClickListener {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    switchUser()
                } else {
                    selectUser(id)
                }
            }

            override fun onItemDelete(id: Int, nick: String) {
                showConfirmDialog(id, nick)
            }
        })
    }

    override fun initData() {
        super.initData()
        mViewModel.getUserData()
    }

    @SuppressLint("NotifyDataSetChanged")
    override suspend fun createObserver() {
        mViewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                userAdapter.submitList(it)
                //强制刷新解决切换账户后列表无法刷新的情况
                userAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showConfirmDialog(id: Int, str: String) {
        CommonDialog.newInstance(
            getString(R.string.tip_delete_user),
            getString(R.string.tip_delete) + str,
            getString(R.string.tip_confirm),
            getString(R.string.tip_cancel),
        ).also {
            it.setOnOkClickListener {
                mViewModel.deleteUser(id)
            }
            it.show(childFragmentManager)
        }
    }

    private fun setEditStatus(status: Boolean) {
        userAdapter.setEditStatus(status)
    }

    private fun selectUser(id: Int) {
        mViewModel.selectUser(id)
    }

    private fun switchUser() {
        showToast("登录其他账户")
    }
}