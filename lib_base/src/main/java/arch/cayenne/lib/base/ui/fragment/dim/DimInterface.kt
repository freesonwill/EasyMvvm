package arch.cayenne.lib.base.ui.fragment.dim

import androidx.fragment.app.Fragment


interface DimInterface {
    fun getIsDismissing(): Boolean
    fun getHostFragment(): Fragment
}