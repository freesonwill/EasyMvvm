package com.walisport.module.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
class SearchHistoryAdapter(var closeAction:(position: Int)->Unit): FlowAdapter<String?>() {
    var isDelete:Boolean=false

    override fun getView(parent: ViewGroup?, item: String?, position: Int): View {
        return LayoutInflater.from(parent?.context).inflate(R.layout.item_search_history, null)
    }

    override fun initView(view: View?, item: String?, position: Int) {
        val textView = view?.findViewById<AppCompatTextView>(R.id.item_tv)
        val ivClose = view?.findViewById<SkinnableImageView>(R.id.ivClose)
        if(isDelete){
            ivClose?.visibility=View.VISIBLE
        }else{
            ivClose?.visibility=View.GONE
        }
        ivClose?.clickNoRepeat {
            closeAction.invoke(position)
        }
        textView?.text = item
        textView?.setOnClickListener { v: View? ->
            Toast.makeText(
                view.context,
                item,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
