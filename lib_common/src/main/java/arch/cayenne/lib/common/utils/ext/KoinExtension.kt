package arch.cayenne.lib.common.utils.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier


inline fun <reified VM : ViewModel, reified F : Fragment> Fragment.sharedViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<VM> = lazy(LazyThreadSafetyMode.NONE) {
    findRelative<F>().getViewModel<VM>(
        qualifier = qualifier,
        parameters = parameters
    )
}

///** Finds a relative fragment of type [F] via BFS. */
inline fun <reified F : Fragment> Fragment.findRelative(): F {
    val visited = mutableListOf<Fragment>()
    val queued = mutableListOf(this.rootFragment)
    while (queued.isNotEmpty()) {
        val current = queued.removeFirst()
        when {
            current is F -> return current
            visited.contains(current) -> continue
        }
        visited.add(current)
        queued.addAll(current.childFragmentManager.fragments)
    }
    throw IllegalStateException("Fragment does not have a relative fragment of type ${F::class.java}")
}

val Fragment.rootFragment: Fragment
    get() {
        var root = this
        while (root.parentFragment != null)
            root = root.requireParentFragment()
        return root
    }