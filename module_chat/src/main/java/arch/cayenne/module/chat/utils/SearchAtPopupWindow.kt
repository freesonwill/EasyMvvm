package arch.cayenne.module.chat.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.databinding.PopupSearchAtLayoutBinding
import arch.cayenne.module.chat.ui.adapter.AtAdapter

/**
 * @author: wenxi
 * @date: 19/11/25 14:47
 * @description:
 */
class SearchAtPopupWindow {
    var atPopupWindow: PopupWindow? = null
    val atAdapter = AtAdapter()
    val list = arrayListOf("张顺", "吴用", "公孙胜", "柴让", "卢俊义")

    fun createPopupWindow(context: Context, targetView: View) {
        if (atPopupWindow == null) {
            val binding =
                PopupSearchAtLayoutBinding.inflate(LayoutInflater.from(context), null, false)
            atPopupWindow = PopupWindow(binding.root, 375.dp2px, 215.dp2px)
            atPopupWindow?.showAsDropDown(targetView, 0, 0)
            atAdapter.submitList(list)
            binding.recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = atAdapter
            }

        } else if (!atPopupWindow!!.isShowing) {
            atPopupWindow?.showAsDropDown(targetView, 0, 0)
        }
    }

}