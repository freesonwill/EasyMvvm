package com.xcjh.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xcjh.app.base.BaseVpFragment

/**
 * 可以动态增删子类
 */
class ViewPager2Adapter(
    fragmentActivity: FragmentActivity?,
    fragmentList: List<Fragment>,
) :
    FragmentStateAdapter(fragmentActivity!!) {
    private val fragmentList: List<Fragment>
    private val fragmentIds: MutableList<Long> = arrayListOf() //用于存储更新fragment的特定标识
    private val createIds: HashSet<Long> = HashSet() //得用hashset防重，用于存储adapter内的顺序

    init {
        this.fragmentList = fragmentList
        update(fragmentList)
    }

    fun update(fragmentLists: List<Fragment>) {
        fragmentIds.clear()
        for (i in fragmentLists.indices) {
            fragmentIds.add((fragmentLists[i] as BaseVpFragment<*, *>).typeId)
        }
    }

    override fun createFragment(position: Int): Fragment {
        val ids = fragmentIds[position]
        createIds.add(ids) //创建的时候将未添加的fragment添加进来，每次刷新都会调用这里，其次调用containsItem
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    /**
     * 这两个方法必须重写，作为数据改变刷新检测的工具
     * @param position
     * @return
     */
    override fun getItemId(position: Int): Long {
        return fragmentIds[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return createIds.contains(itemId)
    }
}