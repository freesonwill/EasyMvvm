//package com.walisport.module.home.test
//
///**
// * @author: zhangsan
// * @date: 2025/4/4 10:31
// * @description:
// */
//import android.content.Context
//import android.os.Bundle
//import androidx.fragment.app.FragmentManager
//import androidx.navigation.NavDestination
//import androidx.navigation.NavOptions
//import androidx.navigation.Navigator
//import androidx.navigation.fragment.FragmentNavigator
//import androidx.navigation.fragment.NavHostFragment
//
//@Navigator.Name("keep_state_fragment")
//class KeepStateNavigator(
//    private val context: Context,
//    private val fragmentManager: FragmentManager,
//    private val containerId: Int
//) : Navigator<FragmentNavigator.Destination>() {
//    companion object {
//
//        // 注册方式（在 Activity 中）
//        fun setupKeepStateNavigator(navHostFragment: NavHostFragment, graphResId: Int) {
//            val navController = navHostFragment.navController
//            val keepStateNavigator = KeepStateNavigator(
//                navHostFragment.requireContext(),
//                navHostFragment.childFragmentManager,
//                navHostFragment.id
//            )
//            navController.navigatorProvider.addNavigator(keepStateNavigator)
//            navController.setGraph(graphResId)
//        }
//    }
//
//    private val fragmentTagPrefix = "android:switcher:"
//
//    override fun createDestination(): FragmentNavigator.Destination {
//        return FragmentNavigator.Destination(this)
//    }
//
//    override fun navigate(
//        destination: FragmentNavigator.Destination,
//        args: Bundle?,
//        navOptions: NavOptions?,
//        navigatorExtras: Extras?
//    ): NavDestination? {
//        val tag = fragmentTagPrefix + destination.id
//        val transaction = fragmentManager.beginTransaction()
//
//        val currentFragment = fragmentManager.primaryNavigationFragment
//        if (currentFragment != null) {
//            transaction.hide(currentFragment)
//        }
//
//        var fragment = fragmentManager.findFragmentByTag(tag)
//        if (fragment == null) {
//            fragment = fragmentManager.fragmentFactory.instantiate(
//                context.classLoader, destination.className
//            ).apply {
//                arguments = args
//            }
//            transaction.add(containerId, fragment, tag)
//        } else {
//            transaction.show(fragment)
//        }
//
//        transaction.setPrimaryNavigationFragment(fragment)
//        transaction.setReorderingAllowed(true)
//        transaction.commit()
//
//        return destination
//    }
//
//    override fun popBackStack(): Boolean {
//        return fragmentManager.popBackStackImmediate()
//    }
//}
//
//
