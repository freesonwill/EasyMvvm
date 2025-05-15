package com.walisport.module.search.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.search.R
import com.walisport.module.search.ui.adapter.SearchHistoryAdapter
import com.walisport.module.search.databinding.FragmentSearchBinding
import com.walisport.module.search.utils.FoldUtils
import com.walisport.module.search.viewmodel.SearchViewModel
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as Rc
/**
 * @author: caomei
 * @date: 2025/4/24 16:31
 * @description:搜索
 */
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
    override val vbClass: KClass<FragmentSearchBinding>
        get() = FragmentSearchBinding::class
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class

    private var historyAdapter: SearchHistoryAdapter? = null
    private var isEditor: Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadSearchTitleBar(getString(R.string.please_input_content),
            afterTextChanged = { text,  binding ->
                val count = text?.length ?: 0
                val color = if (count > 0) Rc.color.search_btn
                else Rc.color.search_btn_normal
                binding.tvSearchText.setTextColor(color.getColor())
            },
            onSearch = { content, _ ->
                if (TextUtils.isEmpty(content)) {
                    showToast(getString(R.string.please_input_content))
                    return@loadSearchTitleBar
                }
                historyAdapter?.addData(content)
            })
        historyAdapter = SearchHistoryAdapter(closeAction = { position ->
            historyAdapter?.deleteData(position)
        })
        historyAdapter?.setNewData(FoldUtils.getHistoryList().toMutableList())
        mBinding.hfList.setAdapter(historyAdapter)
        mBinding.ivClickShowDelete.clickNoRepeat {
            setButton()
        }
        mBinding.txtCompletedAll.clickNoRepeat {
            setButton()
        }
    }

    private fun setButton() {
        isEditor = !isEditor
        historyAdapter?.isDelete = isEditor
        mBinding.hfList.setEditor(isEditor)
        if (isEditor) {
            mBinding.ivClickShowDelete.visibility = View.GONE
            mBinding.llShowCompleted.visibility = View.VISIBLE
        } else {
            mBinding.ivClickShowDelete.visibility = View.VISIBLE
            mBinding.llShowCompleted.visibility = View.GONE
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }


}