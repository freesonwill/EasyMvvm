package com.cn.game.sdk.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * 公共 fragment adapter
 * Created by dzb on 2019/10/31.
 *
 */
class CommonFragmentAdapter(fm: FragmentManager,  fragments: List<Fragment>,pageTitle:ArrayList<String>) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragments: List<Fragment>?=fragments

    private var mTitleFragments: ArrayList<String>?=pageTitle


    fun setTitle(position:Int,content:String){
        mTitleFragments!![position] = content
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {

        return mFragments?.get(position)!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleFragments?.get(position)!!
    }

    override fun getCount(): Int {
        if (null != mFragments) {
            return mFragments.size
        }
        return 0
    }
}