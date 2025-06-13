package arch.cayenne.lib.base.utils.ext

import androidx.fragment.app.Fragment

/**
 * @author: zhangsan
 * @date: 2025/5/28 10:13
 * @description:
 */
object FragmentExt {
    private val TAG = "FragmentExt"

    /**
     * Fragment是否根节点Fragment（直接附加到Activity）
     *
     */
    val Fragment.isRootFragment get() = parentFragment?.parentFragment == null
}