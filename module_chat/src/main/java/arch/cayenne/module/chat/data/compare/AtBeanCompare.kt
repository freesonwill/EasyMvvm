package arch.cayenne.module.chat.data.compare

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import arch.cayenne.module.chat.data.model.AtBean

/**
 * @author: wenxi
 * @date: 24/11/25 11:27
 * @description:
 */
class AtBeanCompare :ItemCallback<AtBean>() {
    override fun areItemsTheSame(oldItem: AtBean, newItem: AtBean): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: AtBean, newItem: AtBean): Boolean {
        return oldItem.name == newItem.name
    }
}