package androidx.navigation.fragment

/**
 * @author: zhangsan
 * @date: 2025/5/31 11:49
 * @description:
 */

import android.content.Context
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import arch.cayenne.lib.common.R

@Navigator.Name("fragment")
class ReusingFragmentNavigator(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val mContainerId: Int
) : FragmentNavigator(
    mContext,
    mFragmentManager,
    mContainerId
) {
    private val TAG = "FragmentNavigator"

    private val savedIds by lazy {
        val field = FragmentNavigator::class.java.getDeclaredField("savedIds")
        field.isAccessible = true
        field.get(this) as LinkedHashSet<String>
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {

        super.popBackStack(popUpTo, savedState)
        //出栈完成后，不会回调任何方法，因此需要自己调用
        if (mFragmentManager.fragments.isNotEmpty()) {
            //出栈调用的是popBackStack，这意味着获取当前栈，得到的是即将退出的栈
            //要么直接把里面的代码copy进来，并把popBackStack改成popBackStackImmediate
            //然后调用mFragmentManager.primaryNavigationFragment就可以回调
            //要么把栈算出来再调用
            //这里应用的方法是后者
            var index = mFragmentManager.fragments.size - 2
            if (index < 0) index = 0
            val fragment = mFragmentManager.fragments[index]
            fragment?.onResume()
        }

    }

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ) {
        if (mFragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring navigate() call: FragmentManager has already saved its state"
            )
            return
        }
        for (entry in entries) {
            val backStack = state.backStack.value
            val initialNavigation = backStack.isEmpty()
            val restoreState = (
                    navOptions != null && !initialNavigation &&
                            navOptions.shouldRestoreState() &&
                            savedIds.remove(entry.id)
                    )
            if (restoreState) {
                // Restore back stack does all the work to restore the entry
                mFragmentManager.restoreBackStack(entry.id)
                state.push(entry)
                return
            }
            val destination = entry.destination as Destination
            val args = entry.arguments
            var className = destination.className
            if (className[0] == '.') {
                className = mContext.packageName + className
            }
            val ft = mFragmentManager.beginTransaction()
            //设置默认动画
            var enterAnim = animChange(navOptions?.enterAnim ?: -1, R.anim.slide_in_right)
            var exitAnim = animChange(navOptions?.exitAnim ?: -1, R.anim.slide_out_left)
            var popEnterAnim = animChange(navOptions?.popEnterAnim ?: -1, R.anim.slide_in_left)
            var popExitAnim = animChange(navOptions?.popExitAnim ?: -1, R.anim.slide_out_right)
            if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
                enterAnim = if (enterAnim != -1) enterAnim else 0
                exitAnim = if (exitAnim != -1) exitAnim else 0
                popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
                popExitAnim = if (popExitAnim != -1) popExitAnim else 0
                ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            }
            //切换fragment
            var fragment = mFragmentManager.primaryNavigationFragment
            if (fragment != null) {
                ft.hide(fragment)
            }
            val tag = destination.id.toString()
            fragment = mFragmentManager.findFragmentByTag(tag)
            var addToBackStack = false
            if (fragment != null) {
                ft.show(fragment)
                addToBackStack = false
            } else {
                fragment = instantiateFragment(mContext, mFragmentManager, className, args)
                fragment.arguments = args
                ft.add(mContainerId, fragment, tag)
                addToBackStack = true
            }
            ft.setPrimaryNavigationFragment(fragment)
            @IdRes val destId = destination.id
            // TODO Build first class singleTop behavior for fragments
            val isSingleTopReplacement = (
                    navOptions != null && !initialNavigation &&
                            navOptions.shouldLaunchSingleTop() &&
                            backStack.last().destination.id == destId
                    )
            val isAdded = when {
                initialNavigation -> {
                    true
                }

                isSingleTopReplacement -> {
                    // Single Top means we only want one instance on the back stack
                    if (backStack.size > 1) {
                        // If the Fragment to be replaced is on the FragmentManager's
                        // back stack, a simple replace() isn't enough so we
                        // remove it from the back stack and put our replacement
                        // on the back stack in its place
                        mFragmentManager.popBackStack(
                            entry.id,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        ft.addToBackStack(entry.id)
                    }
                    false
                }

                else -> {
                    if(addToBackStack) ft.addToBackStack(entry.id)
                    true
                }
            }
            if (navigatorExtras is Extras) {
                for ((key, value) in navigatorExtras.sharedElements) {
                    ft.addSharedElement(key, value)
                }
            }
            ft.setReorderingAllowed(true)
            ft.commit()
            // The commit succeeded, update our view of the world
            if (isAdded) {
                state.push(entry)
            }
        }
    }

    private fun animChange(id: Int, default: Int): Int {
        if (id == -1) {
            return default
        }
        return id
    }

}