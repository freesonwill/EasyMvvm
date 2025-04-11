package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.sendResult
import com.walisport.lib.common.ui.dialog.CommonDialog
import com.walisport.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentComboBetBinding
import com.walisport.module.bet.ui.adapter.BetSheetAdapter
import com.walisport.module.bet.util.BetSheetDecoration
import com.walisport.module.bet.viewmodel.ComboBetViewModel
import kotlin.reflect.KClass

class ComboBetFragment : BaseFragment<ComboBetViewModel, FragmentComboBetBinding>(), BetSheetListener  {

    override val vbClass: KClass<FragmentComboBetBinding> = FragmentComboBetBinding::class
    override val vmClass: KClass<ComboBetViewModel> = ComboBetViewModel::class

    private val betSheetAdapter by lazy {
        BetSheetAdapter(object : BetSheetAdapter.OnBetSheetClickListener {
            override fun onDeleteClick(item: BetBean) {
                CommonDialog.newInstance(
                    title = "",
                    message = getString(R.string.title_dialog_remove),
                    okText = getString(R.string.btn_confirm),
                    cancelText = getString(R.string.btn_cancel)
                ).apply {
                    setOnOkClickListener {
                        mViewModel.removeBet(item.gameId)
                    }
                }.show(childFragmentManager)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvBet.adapter = betSheetAdapter

        val decoration = BetSheetDecoration(12)
        mBinding.rvBet.addItemDecoration(decoration)
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnDelete.setOnClickListener {
            mViewModel.removeAll()
            dismiss()
        }
    }

    override fun createObserver() {
        mViewModel.onBetListListener.observe(viewLifecycleOwner) {
            if (it.size > 1) {
                betSheetAdapter.submitList(it)
            } else {
                navigate(ComboBetFragmentDirections.actionComboBetFragmentToSingleBetFragment(), null)
            }
        }
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.comboBetFragment)
    }
}