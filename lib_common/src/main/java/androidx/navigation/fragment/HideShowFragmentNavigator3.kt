package androidx.navigation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import java.util.ArrayDeque


/**
 * @author: zhangsan
 * @date: 2025/5/27 12:01
 * @description: navigation跳转时不销毁fragment的view
 */
@Navigator.Name("fragment")
class HideShowFragmentNavigator3(private val mContext: Context,
                                 private val mFragmentManager: FragmentManager,
                                 private val mContainerId: Int) :
    FragmentNavigator(mContext, mFragmentManager, mContainerId) {

    companion object {
        private const val TAG = "KeepStateFragmentNavigator"
        private const val KEY_BACK_STACK_IDS = "KeepStateFragmentNavigator:backStackIds"

        // 注册方式（在 Activity 中）
        fun setupKeepStateNavigator(
            context: Context,
            fragmentManager: FragmentManager,
            containerId: Int,
            navController: NavController,
        ) {
            val navigator = HideShowFragmentNavigator3(
                context,
                fragmentManager,
                containerId
            )
            navController.navigatorProvider.addNavigator(navigator)
        }
    }
    private val mBackStack = ArrayDeque<Int>()


    override fun popBackStack(): Boolean {
        if (mBackStack.isEmpty()) {
            return false
        }
        if (mFragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring popBackStack() call: FragmentManager has already"
                        + " saved its state"
            )
            return false
        }
    
        if (mFragmentManager.backStackEntryCount > 0) {
            mFragmentManager.popBackStack(
                generateBackStackName(mBackStack.size, mBackStack.peekLast()!!),
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else {
            //已经是RootFragment了 无需再返回了
        }
        mBackStack.removeLast()

        return true
    }


    @Deprecated("")
    override fun instantiateFragment(
        context: Context,
        fragmentManager: FragmentManager,
        className: String,
        args: Bundle?
    ): Fragment {
        //Unable to instantiate fragment Demo11OneFragment3: could not find Fragment constructor
        //  这里想要使用构造方法的Fragment初始化，使用了包装类Fragment,让包装类默认空参初始化，
        // 内部的fragment再加载我们真正的Fragment,从而实现构造的Fragment可用

        val fragment = super.instantiateFragment(
            context,
            fragmentManager,
            "androidx.fragment.app.NavContainerFragment",
            args
        )
        fragment.arguments = args
        return fragment
    }


    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }

        val frag = instantiateFragment(mContext, mFragmentManager, className, args)

        //        frag.setArguments(args);
        val ft = mFragmentManager.beginTransaction()

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        //        ft.replace(mContainerId, frag);
        //Add的方式替换replace方式，并处理生命周期
        if (mFragmentManager.fragments.size > 0) {
            val lastFragment = mFragmentManager.fragments[mFragmentManager.fragments.size - 1]
            ft.hide(lastFragment)
            ft.setMaxLifecycle(lastFragment, Lifecycle.State.STARTED) //降级到onStart,onPause调用
            ft.setMaxLifecycle(frag, Lifecycle.State.RESUMED) //升级到onResume
            if (frag.isAdded) {
                ft.show(frag)
            } else {
                ft.add(mContainerId, frag)
            }
        } else {
            ft.replace(mContainerId, frag)
        }

        ft.setPrimaryNavigationFragment(frag)

        @IdRes val destId = destination.id
        val initialNavigation = mBackStack.isEmpty()

        // Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()) && mBackStack.peekLast() == destId

        val isAdded: Boolean
        if (initialNavigation) {
            isAdded = true
        } else if (isSingleTopReplacement) {
            if (mBackStack.size > 1) {
                mFragmentManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.peekLast()!!),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            isAdded = false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            isAdded = true
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
            mBackStack.add(destId)
            return destination
        } else {
            return null
        }
    }

    override fun onSaveState(): Bundle {
        val b = Bundle()
        val backStack = IntArray(mBackStack.size)
        var index = 0
        for (id in mBackStack) {
            backStack[index++] = id
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack)
        return b
    }

    override fun onRestoreState(savedState: Bundle) {
        val backStack = savedState.getIntArray(KEY_BACK_STACK_IDS)
        if (backStack != null) {
            mBackStack.clear()
            for (destId in backStack) {
                mBackStack.add(destId)
            }
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }


}
