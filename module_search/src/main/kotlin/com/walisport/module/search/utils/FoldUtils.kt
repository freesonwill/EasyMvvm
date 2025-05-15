package com.walisport.module.search.utils

import android.view.View
import android.view.ViewGroup

/**
 * @author: caomei
 * @date: 2025/4/22 15:18
 * @description:
 */
object FoldUtils {

    /**
     * 移除父布局中的子布局
     *
     * @param view
     */
    fun removeFromParent(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
    }

    fun getViewWidth(view: View): Int {
        view.measure(0, 0)
        return view.measuredWidth
    }

    fun getHistoryList(): List<String> {
        val historyList: MutableList<String> = ArrayList()
        historyList.add("衣服")
        historyList.add("T恤宽松男")
        historyList.add("男鞋")
        historyList.add("香蕉苹果")
        historyList.add("休闲裤")
        historyList.add("牛仔裤")
        historyList.add("红薯")
        historyList.add("西红柿")
        historyList.add("玩具大白")
        historyList.add("丝绵被")
        historyList.add("保温杯")
        historyList.add("花生油")
        historyList.add("折叠椅")
        historyList.add("小米笔记本")
        historyList.add("三星手机")
        historyList.add("显示器")
        historyList.add("小风扇")
        historyList.add("卫生纸")
        historyList.add("视频教程")
        historyList.add("学习")
        historyList.add("学习")

        return historyList
    }
}