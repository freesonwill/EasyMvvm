package arch.cayenne.module.chat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.PopupWindow
import androidx.core.text.getSpans
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.data.model.AtBean
import arch.cayenne.module.chat.databinding.PopupSearchAtLayoutBinding
import arch.cayenne.module.chat.manager.MentionSpan
import arch.cayenne.module.chat.ui.adapter.AtAdapter
import okhttp3.internal.addHeaderLenient
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

/**
 * @author: wenxi
 * @date: 19/11/25 14:47
 * @description:
 */
class SearchAtPopupWindow {
    var atPopupWindow: PopupWindow? = null
    val atAdapter = AtAdapter()
    val list = arrayListOf(
        AtBean("张顺", false),
        AtBean("吴用", false),
        AtBean("公孙胜", false),
        AtBean("柴让", false),
        AtBean("卢俊义", false)
    )

    fun createPopupWindow(context: Context, itemListener: RecyclerItemListener<AtBean>) {
        if (atPopupWindow == null) {
            val binding =
                PopupSearchAtLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            atPopupWindow = PopupWindow(binding.root, 375.dp2px, 215.dp2px)
            atAdapter.submitList(list)
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
            atPopupWindow?.showAsDropDown(targetView)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapter(editext:EditText){
        val editAtList = getAtMsg(editext.text)
        val indexList = mutableSetOf<Int>()
        editAtList.forEach {
           val index  = atAdapter.currentList.indexOf(it)
            indexList.add(index)
        }
        atAdapter.selectedSet = indexList
        atAdapter.notifyDataSetChanged()
    }

    private fun getAtMsg(editable: Editable): List<AtBean> {
        if (editable.isEmpty()) {
            return emptyList()
        }
        val spannable = SpannableStringBuilder(editable)
        val spans = spannable.getSpans(0, editable.length, MentionSpan::class.java)
        return spans.map {
            "getAtMsg ${it.tv}2323".logd("aaa")
            AtBean(name = it.tv, false) }.toList()
    }

    fun dismiss() {
        if (atPopupWindow != null && atPopupWindow?.isShowing == true) {
            atPopupWindow?.dismiss()
        }
    }


}