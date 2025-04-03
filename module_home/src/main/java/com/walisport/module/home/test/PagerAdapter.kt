package com.walisport.module.home.test

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val pages: List<PagerBean>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position].fragment


    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val vg = holder.itemView as ViewGroup
        clipChildren(vg)
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun clipChildren(vg: ViewGroup) {
        vg.clipChildren = false
        vg.clipToPadding = false
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            if (child is ViewGroup) {
                clipChildren(child)
            }
        }
    }

}