package com.walisport.module.search.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.walisport.module.search.R
import com.walisport.module.search.ui.view.FlowAdapter

/**
 * @author: caomei
 * @date: 2025/4/22 16:01
 * @description: 测试适配器
 */
class SearchHistoryAdapter(
    var closeAction: (position: Int, text: String?) -> Unit,
    var onSearch: (key: String?) -> Unit = {}
) : FlowAdapter<String?>() {
    var isDelete: Boolean = false

    @SuppressLint("InflateParams")
    override fun getView(parent: ViewGroup?, item: String?, position: Int): View {
        return LayoutInflater.from(parent?.context).inflate(R.layout.item_search_history, null)
    }

    override fun initView(view: View?, item: String?, position: Int) {
        view?.findViewById<SkinnableImageView>(R.id.ivClose)?.apply {
            visibility =
                if (isDelete) View.VISIBLE
                else View.GONE

            clickNoRepeat {
                closeAction.invoke(position, item)
            }
        }

        view?.findViewById<AppCompatTextView>(R.id.item_tv)?.apply {
            text =
                if (item?.isNotEmpty() == true && item.length > 7) item.take(7) + "..."
                else item
            setOnClickListener {
                onSearch(item)
            }
        }
    }
}
