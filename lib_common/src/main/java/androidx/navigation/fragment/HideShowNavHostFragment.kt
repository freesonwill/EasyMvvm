package androidx.navigation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragivityFragmentNavigator
import androidx.navigation.Navigator
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.anim.AnimationController
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * @author DBoy
 * @date 2020/12/13
 * Class 描述 : Hide - Show NavHostFragment
 */
class HideShowNavHostFragment : NavHostFragment() {
    private lateinit var navigator: FragivityFragmentNavigator

    /**
     * @return 使用自己的FragmentNavigator 虽然是废弃的，但是源码实现最终都是调用这里返回Navigator
     */
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        //return ReusingFragmentNavigator(requireContext(), childFragmentManager, containerId)
        //return MyFragmentNavigator(requireContext(), childFragmentManager, containerId)
        return FragivityFragmentNavigator(
            requireContext(),
            childFragmentManager,
            containerId
        ).apply {
            navigator = this
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch {
            merge(
                AnimationController.defaultEnterAnim.map { "enter" to it },
                AnimationController.defaultExiAnim.map { "exit" to it },
                AnimationController.defaultPopEnterAnim.map { "popEnter" to it },
                AnimationController.defaultPopExitAnim.map { "popExit" to it }
            ).collect { (type, anim) ->
                when (type) {
                    "enter" -> navigator.setEnterAnim(anim)
                    "exit" -> navigator.setExitAnim(anim)
                    "popEnter" -> navigator.setPopEnterAnim(anim)
                    "popExit" -> navigator.setPopExitAnim(anim)
                }
            }
        }
    }

    private val containerId: Int
        get() {
            val id = id
            return if (id != 0 && id != View.NO_ID) {
                id
            } else R.id.nav_host_fragment_container//这里可能会报错，但是编译是能通过的
            // Fallback to using our own ID if this Fragment wasn't added via
            // add(containerViewId, Fragment)
        }
}