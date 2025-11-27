package arch.cayenne.lib.base.ui.adapter

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/***
 * @param binding: ViewBinding
 *
 * @author Link Hsieh
 */
open class BaseViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    protected val TAG by lazy { this::class.java.simpleName }
    //TODO: getString uses system language, not app language
    fun getString(@StringRes id: Int) = itemView.resources.getString(id)
    val resources: Resources
        get() = itemView.resources
}