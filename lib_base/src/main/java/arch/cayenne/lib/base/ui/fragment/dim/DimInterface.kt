package arch.cayenne.lib.base.ui.fragment.dim

import androidx.fragment.app.Fragment


/***
 * 作為操作dim的載體，須實作才能使用DimController
 */
interface DimInterface {
    fun getIsDismissing(): Boolean
    fun getHostFragment(): Fragment
}