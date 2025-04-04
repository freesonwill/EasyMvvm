package com.walisport.module.home.test

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.walisport.module.home.R

/**
 * @author: zhangsan
 * @date: 2025/4/4 11:04
 * @description:
 */
class ViewPager2Fragment : Fragment(R.layout.fragment_test_view_pager2) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerNew)
        val gameList = List(10) { index -> PagerBean("${index + 1}") {
                ViewPagerItem2Fragment().apply {
                    arguments = ViewPagerItem2FragmentArgs(title = "${index + 1}").toBundle()
                }
            }
        }
        viewPager.adapter = PagerAdapter(childFragmentManager, lifecycle, gameList)
    }

}