package arch.cayenne.module.home.ui.fragment

/**
 *
 * @date: 2025/11/5 17:38
 * @description:
 */
interface ISubFragmentLifecycle {
    fun onFragmentSelected()
    fun onFragmentUnSelected()
    fun reloadCurrentMatchListPagerFragment()
}