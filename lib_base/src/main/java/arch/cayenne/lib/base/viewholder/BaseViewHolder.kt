package arch.cayenne.lib.base.viewholder

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/***
 * @param binding: ViewBinding
 *
 * @author Link Hsieh
 */
open class BaseViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun getString(id: Int) = itemView.resources.getString(id)
    val resources: Resources
        get() = itemView.resources
}