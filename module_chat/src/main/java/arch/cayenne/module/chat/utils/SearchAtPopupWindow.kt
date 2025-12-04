package arch.cayenne.module.chat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.data.model.AtBean
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.chat.databinding.PopupSearchAtLayoutBinding
import arch.cayenne.module.chat.ui.adapter.AtAdapter

/**
 * @author: wenxi
 * @date: 19/11/25 14:47
 * @description:
 */
class SearchAtPopupWindow {
    var isSearchIng: Boolean = false
    var atPopupWindow: PopupWindow? = null
    val atAdapter = AtAdapter()
    val list = arrayListOf(
        AtBean(0, "张顺", false),
        AtBean(1, "吴用", false),
        AtBean(2, "公孙胜", false),
        AtBean(3, "柴让", false),
        AtBean(4, "卢俊义", false)
    )


    fun createPopupWindow(context: Context, itemListener: RecyclerItemListener<AtBean>) {
        if (atPopupWindow == null) {
            val binding =
                PopupSearchAtLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            atPopupWindow = PopupWindow(binding.root, 375.dp2px, 215.dp2px)
            atAdapter.setItemClickListener(object : RecyclerItemListener<AtBean> {
                override fun onItemClick(item: AtBean?, position: Int) {
                    itemListener.onItemClick(item, position)
                }
            })
            binding.recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = atAdapter
            }
        }
    }

    fun showPopupWindow(targetView: EditText) {
        if (atPopupWindow?.isShowing == false) {
            notifyAdapter(targetView)
            atPopupWindow?.showAsDropDown(targetView, 0, (-10).dp2px)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapter(editext: EditText) {
        val editAtList = getAtMsg(editext.text)
        val indexList = mutableSetOf<Int>()
        val nList: List<AtBean> = list
        editAtList.forEach { editBean ->
            val bean = nList.find { nBean -> editBean.name == nBean.name }
            bean?.let {
                indexList.add(bean.id)
            }
        }
        atAdapter.selectedSet = indexList
        atAdapter.submitList(nList)
        atAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAdapterSelect(names: List<String>) {

        if (names.isEmpty()) {
            return
        }
        names.forEach { name ->
            val index = atAdapter.currentList.indexOfFirst {
                it.name == name
            }
            if (index >= 0) {
                atAdapter.selectedSet.remove(atAdapter.currentList[index].id)
            }
        }
        atAdapter.notifyDataSetChanged()
    }

    private fun getAtMsg(editable: Editable): List<AtBean> {
        if (editable.isEmpty()) {
            return emptyList()
        }
        val spannable = SpannableStringBuilder(editable)
        val spans = spannable.getSpans(0, editable.length, MentionSpan::class.java)
        return spans.map {
            AtBean(-1, name = it.tv, false)
        }.toList()
    }

    fun isShowing() = atPopupWindow?.isShowing ?: false

    fun dismiss() {
        if (atPopupWindow != null && atPopupWindow?.isShowing == true) {
            atPopupWindow?.dismiss()
        }
    }

    fun addDismissListener(dismissListener: (() -> Unit)) {
        atPopupWindow?.setOnDismissListener {
            dismissListener.invoke()
        }
    }


    fun searchAtList(str: String) {
        val searchText = str.substring(1)
        val searchList = atAdapter.currentList.filter { it.name.contains(searchText) }
        if (searchList.isEmpty()) {
            dismiss()
        } else {
            isSearchIng = true
        }
        atAdapter.submitList(searchList)
    }


}