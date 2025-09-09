package com.walisport.module.login.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arch.cayenne.lib.database.entity.TournamentDataModel
import com.walisport.module.login.ui.fragment.EMailVerifyFragment
import com.walisport.module.login.ui.fragment.MobileVerifyFragment

/**
 * @author: ricky.chang
 * @date: 2025/7/16 下午5:17
 * @description:
 */
class IdentityVerifyPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0-> MobileVerifyFragment.newInstance(position)
            else ->  EMailVerifyFragment.newInstance(position)
        }
    }
}
