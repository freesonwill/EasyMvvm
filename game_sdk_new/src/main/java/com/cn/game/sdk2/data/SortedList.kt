package com.cn.game.sdk2.data

import java.util.*

/**
 * Description: 排序List
 * author       : zhangsan
 * createTime   : 2023/5/19 15:45
 **/
class SortedList<T> @JvmOverloads constructor(
    private var comparator: Comparator<T> = Comparator { o1, o2 -> o1.hashCode().compareTo(o2.hashCode()) }
) : ArrayList<T>() {

    fun changeComparator(c: Comparator<T>) {
        this.comparator = c
        Collections.sort(this, comparator)
    }

    override fun add(element: T): Boolean {
        var pos = 0
        while (pos < size) {
            if (this.comparator.compare(get(pos), element) > 0) {
                break
            }
            pos++
        }
        super.add(pos, element)
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach {
            add(it)
        }
        return true
    }

}